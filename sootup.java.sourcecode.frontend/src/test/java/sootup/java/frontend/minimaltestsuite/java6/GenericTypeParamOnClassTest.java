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

/** @author: Hasitha Rajapakse * */
@Tag("Java8")
public class GenericTypeParamOnClassTest extends MinimalSourceTestSuiteBase {
  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "genericTypeParamOnClass", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   * public void genericTypeParamOnClass() {
   * A<Integer> a = new A<Integer>();
   * a.set(5);
   * int x = a.get();
   * }
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "r0 := @this: GenericTypeParamOnClass",
            "r1 = new GenericTypeParamOnClass$A",
            "specialinvoke r1.<GenericTypeParamOnClass$A: void <init>()>()",
            "specialinvoke r1.<GenericTypeParamOnClass$A: void set(java.lang.Object)>(5)",
            "r2 = virtualinvoke r1.<GenericTypeParamOnClass$A: java.lang.Object get()>()",
            "r3 = (java.lang.Integer) r2",
            "return")
        .collect(Collectors.toList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
