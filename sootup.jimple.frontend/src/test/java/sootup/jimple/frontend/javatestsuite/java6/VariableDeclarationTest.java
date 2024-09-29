package sootup.jimple.frontend.javatestsuite.java6;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class VariableDeclarationTest extends JimpleTestSuiteBase {

  @Test
  public void test() {

    SootMethod method = loadMethod(getMethodSignature("shortVariable"));

    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 10", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("byteVariable"));

    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 0", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("charVariable"));

    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 97", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("intVariable"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 512", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("longVariable"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 123456789L", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("floatVariable"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 3.14F", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("doubleVariable"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: VariableDeclaration", "l1 = 1.96969654", "return")
            .collect(Collectors.toList()));
  }

  public MethodSignature getMethodSignature(String methodName) {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), methodName, "void", Collections.emptyList());
  }
}
