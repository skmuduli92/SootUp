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
public class EvaluationOrderWithParenthesesTest extends MinimalSourceTestSuiteBase {

  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(),
        "evaluationOrderWithParentheses",
        "void",
        Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   *     public void evaluationOrderWithParentheses(){
   * int b = ((1 + 2) * 3);
   * }
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "r0 := @this: EvaluationOrderWithParentheses", "i0 = 1 + 2", "i1 = i0 * 3", "return")
        .collect(Collectors.toList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
