package sootup.java.bytecode.frontend.minimaltestsuite.java6;

import static org.junit.jupiter.api.Assertions.assertTrue;

import categories.TestCategories;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.java.bytecode.frontend.minimaltestsuite.MinimalBytecodeTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag(TestCategories.JAVA_8_CATEGORY)
public class ThrowExceptionMethodTest extends MinimalBytecodeTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "divideByZero", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   * void divideByZero() throws ArithmeticException{
   * int i=8/0;
   * }
   * void throwCustomException() throws CustomException {
   * throw new CustomException("Custom Exception");
   * }
   * } catch( CustomException e){
   *
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of("this := @this: ThrowExceptionMethod", "l1 = 8 / 0", "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public MethodSignature getMethodSignature1() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "throwCustomException", "void", Collections.emptyList());
  }

  public List<String> expectedBodyStmts1() {
    return Stream.of(
            "this := @this: ThrowExceptionMethod",
            "$stack1 = new CustomException",
            "specialinvoke $stack1.<CustomException: void <init>(java.lang.String)>(\"Custom Exception\")",
            "throw $stack1")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void testArithException() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    assertTrue(
        method.getExceptionSignatures().stream()
            .anyMatch(classType -> classType.getClassName().equals("ArithmeticException")));
  }

  @Test
  public void testCustomException() {

    SootMethod method = loadMethod(getMethodSignature1());
    assertJimpleStmts(method, expectedBodyStmts1());
    assertTrue(
        method.getExceptionSignatures().stream()
            .anyMatch(classType -> classType.getClassName().equals("CustomException")));
  }
}
