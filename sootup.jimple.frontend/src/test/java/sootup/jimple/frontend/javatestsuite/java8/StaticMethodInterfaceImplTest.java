package sootup.jimple.frontend.javatestsuite.java8;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class StaticMethodInterfaceImplTest extends JimpleTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(),
        "methodStaticMethodInterfaceImpl",
        "void",
        Collections.emptyList());
  }

  private MethodSignature getStaticMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "initStatic", "void", Collections.emptyList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getStaticMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts1());
    SootMethod staticMethod = loadMethod(getStaticMethodSignature());
    assertJimpleStmts(staticMethod, expectedBodyStmts1());
    assertTrue(staticMethod.isStatic() && staticMethod.getName().equals("initStatic"));
    SootClass sootClass = loadClass(getDeclaredClassSignature());
    assertTrue(
        sootClass.getInterfaces().stream()
            .anyMatch(
                javaClassType -> {
                  return javaClassType.getClassName().equals("StaticMethodInterface");
                }));
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "$stack0 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack0.<java.io.PrintStream: void println(java.lang.String)>(\"Inside initStatic - StaticmethodInterfaceImpl\")",
            "return")
        .collect(Collectors.toList());
  }

  public List<String> expectedBodyStmts1() {
    return Stream.of(
            "$stack0 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack0.<java.io.PrintStream: void println(java.lang.String)>(\"Inside initStatic - StaticmethodInterfaceImpl\")",
            "return")
        .collect(Collectors.toList());
  }
}
