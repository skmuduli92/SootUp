/* Qilin - a Java Pointer Analysis Framework
 * Copyright (C) 2021-2030 Qilin developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3.0 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <https://www.gnu.org/licenses/lgpl-3.0.en.html>.
 */

package qilin.core.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import qilin.CoreConfig;
import qilin.core.ArtificialMethod;
import qilin.util.PTAUtils;
import sootup.core.IdentifierFactory;
import sootup.core.frontend.OverridingBodySource;
import sootup.core.frontend.OverridingClassSource;
import sootup.core.graph.MutableStmtGraph;
import sootup.core.inputlocation.EagerInputLocation;
import sootup.core.jimple.Jimple;
import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.NoPositionInformation;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.FallsThroughStmt;
import sootup.core.jimple.common.stmt.JNopStmt;
import sootup.core.jimple.common.stmt.JReturnVoidStmt;
import sootup.core.model.*;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.signatures.MethodSubSignature;
import sootup.core.types.ClassType;
import sootup.core.views.View;
import sootup.java.core.JavaIdentifierFactory;

public class FakeMainFactory extends ArtificialMethod {
  public static FakeMainFactory instance;

  public static int implicitCallEdges;
  private final SootClass fakeClass;
  private final SootClass mainClass;
  private final EntryPoints entryPoints;

  public FakeMainFactory(View view, SootClass mainClazz) {
    super(view);
    this.mainClass = mainClazz;
    this.entryPoints = new EntryPoints();
    this.localStart = 0;
    String className = "qilin.pta.FakeMain";
    IdentifierFactory fact = view.getIdentifierFactory();
    ClassType declaringClassSignature = JavaIdentifierFactory.getInstance().getClassType(className);
    FieldSignature ctSig =
        fact.getFieldSignature("currentThread", declaringClassSignature, "java.lang.Thread");
    SootField currentThread =
        new SootField(ctSig, EnumSet.of(FieldModifier.STATIC), NoPositionInformation.getInstance());
    FieldSignature gtSig =
        fact.getFieldSignature("globalThrow", declaringClassSignature, "java.lang.Exception");
    SootField globalThrow =
        new SootField(gtSig, EnumSet.of(FieldModifier.STATIC), NoPositionInformation.getInstance());

    MethodSignature methodSignatureOne =
        view.getIdentifierFactory()
            .getMethodSignature(className, "main", "void", Collections.emptyList());

    StmtPositionInfo noPosInfo = StmtPositionInfo.getNoStmtPositionInfo();
    final JReturnVoidStmt returnVoidStmt = new JReturnVoidStmt(noPosInfo);
    final JNopStmt jNop = new JNopStmt(noPosInfo);
    this.bodyBuilder = Body.builder();
    makeFakeMain(currentThread);
    final MutableStmtGraph stmtGraph = bodyBuilder.getStmtGraph();
    stmtGraph.addBlock(stmtList);
    stmtGraph.setStartingStmt(jNop);
    stmtGraph.putEdge(jNop, stmtList.get(0));
    stmtGraph.putEdge((FallsThroughStmt) stmtList.get(stmtList.size() - 1), returnVoidStmt);

    bodyBuilder
        .setMethodSignature(methodSignatureOne)
        .setPosition(NoPositionInformation.getInstance());

    Body bodyOne = bodyBuilder.build();
    SootMethod dummyMainMethod =
        new SootMethod(
            new OverridingBodySource(methodSignatureOne, bodyOne),
            methodSignatureOne,
            EnumSet.of(MethodModifier.PUBLIC, MethodModifier.STATIC),
            Collections.emptyList(),
            NoPositionInformation.getInstance());
    this.method = dummyMainMethod;
    this.fakeClass =
        new SootClass(
            new OverridingClassSource(
                Collections.singleton(dummyMainMethod),
                new LinkedHashSet<>(Arrays.asList(currentThread, globalThrow)),
                EnumSet.of(ClassModifier.PUBLIC),
                null,
                JavaIdentifierFactory.getInstance().getClassType("java.lang.Object"),
                null,
                NoPositionInformation.getInstance(),
                null,
                view.getIdentifierFactory().getClassType(className),
                new EagerInputLocation()),
            SourceType.Application);
  }

  public SootMethod getFakeMain() {
    return this.method;
  }

  private List<SootMethod> getEntryPoints() {
    List<SootMethod> ret = new ArrayList<>();
    if (CoreConfig.v().getPtaConfig().clinitMode == CoreConfig.ClinitMode.FULL) {
      ret.addAll(entryPoints.clinits());
    } else {
      // on the fly mode, resolve the clinit methods on the fly.
      ret.addAll(Collections.emptySet());
    }

    if (CoreConfig.v().getPtaConfig().singleentry) {
      List<SootMethod> entries = entryPoints.application();
      if (entries.isEmpty()) {
        throw new RuntimeException("Must specify MAINCLASS when appmode enabled!!!");
      } else {
        ret.addAll(entries);
      }
    } else {
      ret.addAll(entryPoints.application());
      ret.addAll(entryPoints.implicit());
    }
    System.out.println("#EntrySize:" + ret.size());
    return ret;
  }

  public JStaticFieldRef getFieldCurrentThread() {
    SootField field = fakeClass.getField("currentThread").get();
    return Jimple.newStaticFieldRef(field.getSignature());
  }

  public Value getFieldGlobalThrow() {
    SootField field = fakeClass.getField("globalThrow").get();
    return Jimple.newStaticFieldRef(field.getSignature());
  }

  private void makeFakeMain(SootField currentThread) {
    implicitCallEdges = 0;
    for (SootMethod entry : getEntryPoints()) {
      if (entry.isStatic()) {
        if (entry
            .getSignature()
            .getSubSignature()
            .toString()
            .equals("void main(java.lang.String[])")) {
          Value mockStr = getNew(PTAUtils.getClassType("java.lang.String"));
          Immediate strArray = getNewArray(PTAUtils.getClassType("java.lang.String"));
          addAssign(getArrayRef(strArray), mockStr);
          addInvoke(entry.getSignature().toString(), strArray);
          implicitCallEdges++;
        } else if (CoreConfig.v().getPtaConfig().clinitMode != CoreConfig.ClinitMode.ONFLY
            || !PTAUtils.isStaticInitializer(entry)) {
          // in the on fly mode, we won't add a call directly for <clinit> methods.
          addInvoke(entry.getSignature().toString());
          implicitCallEdges++;
        }
      }
    }
    if (CoreConfig.v().getPtaConfig().singleentry) {
      return;
    }
    Local sv = getNextLocal(PTAUtils.getClassType("java.lang.String"));
    Local mainThread = getNew(PTAUtils.getClassType("java.lang.Thread"));
    Local mainThreadGroup = getNew(PTAUtils.getClassType("java.lang.ThreadGroup"));
    Local systemThreadGroup = getNew(PTAUtils.getClassType("java.lang.ThreadGroup"));

    JStaticFieldRef gCurrentThread = Jimple.newStaticFieldRef(currentThread.getSignature());
    addAssign(gCurrentThread, mainThread); // Store
    Local vRunnable = getNextLocal(PTAUtils.getClassType("java.lang.Runnable"));

    Local lThreadGroup = getNextLocal(PTAUtils.getClassType("java.lang.ThreadGroup"));
    addInvoke(
        mainThread,
        "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>",
        mainThreadGroup,
        sv);
    Local tmpThread = getNew(PTAUtils.getClassType("java.lang.Thread"));
    addInvoke(
        tmpThread,
        "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.Runnable)>",
        lThreadGroup,
        vRunnable);
    addInvoke(tmpThread, "<java.lang.Thread: void exit()>");

    addInvoke(systemThreadGroup, "<java.lang.ThreadGroup: void <init>()>");
    addInvoke(
        mainThreadGroup,
        "<java.lang.ThreadGroup: void <init>(java.lang.ThreadGroup,java.lang.String)>",
        systemThreadGroup,
        sv);

    Local lThread = getNextLocal(PTAUtils.getClassType("java.lang.Thread"));
    Local lThrowable = getNextLocal(PTAUtils.getClassType("java.lang.Throwable"));
    Local tmpThreadGroup = getNew(PTAUtils.getClassType("java.lang.ThreadGroup"));
    addInvoke(
        tmpThreadGroup,
        "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>",
        lThread,
        lThrowable); // TODO.

    // ClassLoader
    Local defaultClassLoader = getNew(PTAUtils.getClassType("sun.misc.Launcher$AppClassLoader"));
    addInvoke(defaultClassLoader, "<java.lang.ClassLoader: void <init>()>");
    Local vClass = getNextLocal(PTAUtils.getClassType("java.lang.Class"));
    Local vDomain = getNextLocal(PTAUtils.getClassType("java.security.ProtectionDomain"));
    addInvoke(
        defaultClassLoader,
        "<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>",
        sv);
    addInvoke(
        defaultClassLoader,
        "<java.lang.ClassLoader: void checkPackageAccess(java.lang.Class,java.security.ProtectionDomain)>",
        vClass,
        vDomain);
    addInvoke(
        defaultClassLoader, "<java.lang.ClassLoader: void addClass(java.lang.Class)>", vClass);

    // PrivilegedActionException
    Local privilegedActionException =
        getNew(PTAUtils.getClassType("java.security.PrivilegedActionException"));
    Local gLthrow = getNextLocal(PTAUtils.getClassType("java.lang.Exception"));
    addInvoke(
        privilegedActionException,
        "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>",
        gLthrow);
  }

  public class EntryPoints {

    final MethodSubSignature sigMain;
    final MethodSubSignature sigFinalize;
    final MethodSubSignature sigExit;
    final MethodSubSignature sigClinit;
    final MethodSubSignature sigInit;
    final MethodSubSignature sigStart;
    final MethodSubSignature sigRun;
    final MethodSubSignature sigObjRun;
    final MethodSubSignature sigForName;

    private EntryPoints() {
      sigMain = JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_MAIN);
      sigFinalize =
          JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_FINALIZE);

      sigExit = JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_EXIT);
      sigClinit =
          JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_CLINIT);
      sigInit = JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_INIT);
      sigStart = JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_START);
      sigRun = JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_RUN);
      sigObjRun =
          JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_OBJ_RUN);
      sigForName =
          JavaIdentifierFactory.getInstance().parseMethodSubSignature(JavaMethods.SIG_FOR_NAME);
    }

    protected void addMethod(List<SootMethod> set, SootClass cls, MethodSubSignature methodSubSig) {
      Optional<? extends SootMethod> osm = cls.getMethod(methodSubSig);
      osm.ifPresent(set::add);
    }

    protected void addMethod(List<SootMethod> set, String methodSig) {
      MethodSignature ms = JavaIdentifierFactory.getInstance().parseMethodSignature(methodSig);
      Optional<? extends SootMethod> osm = view.getMethod(ms);
      osm.ifPresent(set::add);
    }

    /**
     * Returns only the application entry points, not including entry points invoked implicitly by
     * the VM.
     */
    public List<SootMethod> application() {
      List<SootMethod> ret = new ArrayList<>();
      if (mainClass != null) {
        addMethod(ret, mainClass, sigMain);
        for (SootMethod clinit : clinitsOf(mainClass)) {
          ret.add(clinit);
        }
      }
      return ret;
    }

    /** Returns only the entry points invoked implicitly by the VM. */
    public List<SootMethod> implicit() {
      List<SootMethod> ret = new ArrayList<SootMethod>();

      //        if (Options.v().src_prec() == Options.src_prec_dotnet) {
      //            return ret;
      //        }

      addMethod(ret, JavaMethods.INITIALIZE_SYSTEM_CLASS);
      addMethod(ret, JavaMethods.THREAD_GROUP_INIT);
      // addMethod( ret, "<java.lang.ThreadGroup: void
      // remove(java.lang.Thread)>");
      addMethod(ret, JavaMethods.THREAD_EXIT);
      addMethod(ret, JavaMethods.THREADGROUP_UNCAUGHT_EXCEPTION);
      // addMethod( ret, "<java.lang.System: void
      // loadLibrary(java.lang.String)>");
      addMethod(ret, JavaMethods.CLASSLOADER_INIT);
      addMethod(ret, JavaMethods.CLASSLOADER_LOAD_CLASS_INTERNAL);
      addMethod(ret, JavaMethods.CLASSLOADER_CHECK_PACKAGE_ACC);
      addMethod(ret, JavaMethods.CLASSLOADER_ADD_CLASS);
      addMethod(ret, JavaMethods.CLASSLOADER_FIND_NATIVE);
      addMethod(ret, JavaMethods.PRIV_ACTION_EXC_INIT);
      // addMethod( ret, "<java.lang.ref.Finalizer: void
      // register(java.lang.Object)>");
      addMethod(ret, JavaMethods.RUN_FINALIZE);
      addMethod(ret, JavaMethods.THREAD_INIT_RUNNABLE);
      addMethod(ret, JavaMethods.THREAD_INIT_STRING);
      return ret;
    }

    /** Returns all the entry points. */
    public List<SootMethod> all() {
      List<SootMethod> ret = new ArrayList<SootMethod>();
      ret.addAll(application());
      ret.addAll(implicit());
      return ret;
    }

    /** Returns a list of all static initializers. */
    public List<SootMethod> clinits() {
      List<SootMethod> ret = new ArrayList<>();
      view.getClasses().forEach(cl -> addMethod(ret, cl, sigClinit));
      return ret;
    }

    /** Returns a list of all clinits of class cl and its superclasses. */
    public Iterable<SootMethod> clinitsOf(SootClass cl) {
      // Do not create an actual list, since this method gets called quite often
      // Instead, callers usually just want to iterate over the result.
      Optional<? extends SootMethod> oinit = cl.getMethod(sigClinit);
      Optional<? extends ClassType> osuperClass = cl.getSuperclass();
      // check super classes until finds a constructor or no super class there anymore.
      while (!oinit.isPresent() && osuperClass.isPresent()) {
        ClassType superType = osuperClass.get();
        Optional<? extends SootClass> oSuperClass = view.getClass(superType);
        if (!oSuperClass.isPresent()) {
          break;
        }
        SootClass superClass = oSuperClass.get();
        oinit = superClass.getMethod(sigClinit);
        osuperClass = superClass.getSuperclass();
      }
      if (!oinit.isPresent()) {
        return Collections.emptyList();
      }
      SootMethod initStart = oinit.get();
      return () ->
          new Iterator<SootMethod>() {
            SootMethod current = initStart;

            @Override
            public SootMethod next() {
              if (!hasNext()) {
                throw new NoSuchElementException();
              }
              SootMethod n = current;

              // Pre-fetch the next element
              current = null;
              Optional<? extends SootClass> oCurrentClass =
                  view.getClass(n.getDeclaringClassType());
              if (!oCurrentClass.isPresent()) {
                return n;
              }
              SootClass currentClass = oCurrentClass.get();
              while (true) {
                Optional<? extends ClassType> osuperType1 = currentClass.getSuperclass();
                if (!osuperType1.isPresent()) {
                  break;
                }
                ClassType classType = osuperType1.get();
                Optional<? extends SootClass> osuperClass1 = view.getClass(classType);
                if (!osuperClass1.isPresent()) {
                  break;
                }
                SootClass superClass = osuperClass1.get();
                Optional<? extends SootMethod> om = superClass.getMethod(sigClinit);
                if (om.isPresent()) {
                  current = om.get();
                  break;
                }
                currentClass = superClass;
              }

              return n;
            }

            @Override
            public boolean hasNext() {
              return current != null;
            }
          };
    }
  }
}
