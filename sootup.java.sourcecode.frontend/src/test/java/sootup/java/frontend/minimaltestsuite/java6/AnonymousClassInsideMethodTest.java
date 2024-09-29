/** @author: Hasitha Rajapakse */
package sootup.java.frontend.minimaltestsuite.java6;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.java.frontend.minimaltestsuite.MinimalSourceTestSuiteBase;

@Tag("Java8")
public class AnonymousClassInsideMethodTest extends MinimalSourceTestSuiteBase {
  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "anonymousClassInsideMethod", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   *
   * public void anonymousClassInsideMethod() {
   *
   * MathOperation myMathOperation = new MathOperation() {
   * int i = 0;
   *
   * @Override
   * public void addition() {
   * i++;
   * }
   * };
   *
   * myMathOperation.addition();
   *
   * }
   * }
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "r0 := @this: AnonymousClassInsideMethod",
            "r1 = new AnonymousClassInsideMethod$1",
            "specialinvoke r1.<AnonymousClassInsideMethod$1: void <init>()>()",
            "interfaceinvoke r1.<AnonymousClassInsideMethod$MathOperation: void addition()>()",
            "return")
        .collect(Collectors.toList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
