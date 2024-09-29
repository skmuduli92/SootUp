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
public class CharLiteralsTest extends JimpleTestSuiteBase {

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature("charCharacter"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 97", "return").collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("charSymbol"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 37", "return").collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("charBackslashT"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 9", "return").collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("charBackslash"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 92", "return").collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("charSingleQuote"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 39", "return").collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("charUnicode"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 937", "return").collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("specialChar"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: CharLiterals", "l1 = 8482", "return").collect(Collectors.toList()));
  }

  public MethodSignature getMethodSignature(String methodName) {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), methodName, "void", Collections.emptyList());
  }
}
