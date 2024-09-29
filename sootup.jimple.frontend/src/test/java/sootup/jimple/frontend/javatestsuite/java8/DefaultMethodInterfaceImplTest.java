package sootup.jimple.frontend.javatestsuite.java8;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
public class DefaultMethodInterfaceImplTest extends JimpleTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "interfaceMethod", "void", Collections.emptyList());
  }

  public MethodSignature getDefaultMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "defaultInterfaceMethod", "void", Collections.emptyList());
  }

  @Test
  public void test() {
    SootMethod method1 = loadMethod(getMethodSignature());
    assertJimpleStmts(method1, expectedBodyStmts());
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    method = loadMethod(getDefaultMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts1());
    SootClass clazz = loadClass(getDeclaredClassSignature());
    assertTrue(
        clazz.getInterfaces().stream()
            .anyMatch(
                javaClassType -> {
                  return javaClassType.getClassName().equalsIgnoreCase("DefaultMethodInterface");
                }));
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: DefaultMethodInterfaceImpl",
            "$stack1 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack1.<java.io.PrintStream: void println(java.lang.String)>(\"Method interfaceMethod() is implemented\")",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<String> expectedBodyStmts1() {
    return Stream.of(
            "l0 := @this: DefaultMethodInterfaceImpl",
            "specialinvoke l0.<DefaultMethodInterface: void defaultInterfaceMethod()>()",
            "$stack1 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack1.<java.io.PrintStream: void println(java.lang.String)>(\"Method defaultInterfaceMethod() is implemented\")",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
