package sootup.jimple.frontend.javatestsuite.java6;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class FinalVariableTest extends JimpleTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "finalVariable", "void", Collections.emptyList());
  }

  public List<String> expectedBodyStmts() {
    return Stream.of("l0 := @this: FinalVariable", "return").collect(Collectors.toList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
