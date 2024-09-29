package sootup.java.bytecode.frontend.minimaltestsuite.java9;

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
public class AnonymousDiamondOperatorTest extends MinimalBytecodeTestSuiteBase {

  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "innerClassDiamond", "int", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   * public int innerClassDiamond() {
   * MyClass<Integer> obj = new MyClass<>() {
   * Integer add(Integer x, Integer y) {
   * return x+y;
   * }
   * };
   * Integer sum = obj.add(22,23);
   * return sum;
   * }
   *
   * public static void main(String args[]){
   * AnonymousDiamondOperator obj= new AnonymousDiamondOperator();
   * System.out.println(obj.innerClassDiamond());
   *
   * }
   *
   * }
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "this := @this: AnonymousDiamondOperator",
            "$stack3 = new AnonymousDiamondOperator$1",
            "specialinvoke $stack3.<AnonymousDiamondOperator$1: void <init>(AnonymousDiamondOperator)>(this)",
            "l1 = $stack3",
            "$stack5 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(22)",
            "$stack4 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(23)",
            "$stack6 = virtualinvoke l1.<MyClass: java.lang.Object add(java.lang.Object,java.lang.Object)>($stack5, $stack4)",
            "l2 = (java.lang.Integer) $stack6",
            "$stack7 = virtualinvoke l2.<java.lang.Integer: int intValue()>()",
            "return $stack7")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
