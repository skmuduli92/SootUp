package sootup.java.bytecode.frontend.minimaltestsuite.java6;

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
public class DeclareIntTest extends MinimalBytecodeTestSuiteBase {
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "declareIntMethod", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   * void declareIntMethod(){
   * System.out.println(dec);
   * System.out.println(hex);
   * System.out.println(oct);
   * }
   *
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "this := @this: DeclareInt",
            "$stack2 = <java.lang.System: java.io.PrintStream out>",
            "$stack1 = this.<DeclareInt: int dec>",
            "virtualinvoke $stack2.<java.io.PrintStream: void println(int)>($stack1)",
            "$stack4 = <java.lang.System: java.io.PrintStream out>",
            "$stack3 = this.<DeclareInt: int hex>",
            "virtualinvoke $stack4.<java.io.PrintStream: void println(int)>($stack3)",
            "$stack6 = <java.lang.System: java.io.PrintStream out>",
            "$stack5 = this.<DeclareInt: int oct>",
            "virtualinvoke $stack6.<java.io.PrintStream: void println(int)>($stack5)",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
