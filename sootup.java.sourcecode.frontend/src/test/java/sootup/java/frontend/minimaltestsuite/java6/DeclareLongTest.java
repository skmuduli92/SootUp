package sootup.java.frontend.minimaltestsuite.java6;

import java.util.ArrayList;
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
public class DeclareLongTest extends MinimalSourceTestSuiteBase {
  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "declareLongMethod", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   *     void declareLongMethod(){
   * System.out.println(l1);
   * System.out.println(l2);
   * }
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of(
            "r0 := @this: DeclareLong",
            "r1 = <java.lang.System: java.io.PrintStream out>",
            "l0 = r0.<DeclareLong: long l1>",
            "virtualinvoke r1.<java.io.PrintStream: void println(long)>(l0)",
            "r2 = <java.lang.System: java.io.PrintStream out>",
            "l1 = r0.<DeclareLong: long l2>",
            "virtualinvoke r2.<java.io.PrintStream: void println(long)>(l1)",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
  }
}
