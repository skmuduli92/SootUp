package sootup.jimple.frontend.javatestsuite.java6;

import java.util.ArrayList;
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
public class DeclareFloatTest extends JimpleTestSuiteBase {
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "declareFloatMethod", "void", Collections.emptyList());
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: DeclareFloat",
            "$stack2 = <java.lang.System: java.io.PrintStream out>",
            "$stack1 = l0.<DeclareFloat: float f1>",
            "virtualinvoke $stack2.<java.io.PrintStream: void println(float)>($stack1)",
            "$stack4 = <java.lang.System: java.io.PrintStream out>",
            "$stack3 = l0.<DeclareFloat: float f2>",
            "virtualinvoke $stack4.<java.io.PrintStream: void println(float)>($stack3)",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
