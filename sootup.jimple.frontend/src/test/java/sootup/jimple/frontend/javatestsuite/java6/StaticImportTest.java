package sootup.jimple.frontend.javatestsuite.java6;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class StaticImportTest extends JimpleTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "mathFunctions", "void", Collections.emptyList());
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    SootClass sootClass = loadClass(getDeclaredClassSignature());
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: StaticImport",
            "$stack1 = <java.lang.System: java.io.PrintStream out>",
            "$stack2 = staticinvoke <java.lang.Math: double sqrt(double)>(4.0)",
            "virtualinvoke $stack1.<java.io.PrintStream: void println(double)>($stack2)",
            "$stack3 = <java.lang.System: java.io.PrintStream out>",
            "$stack4 = staticinvoke <java.lang.Math: double pow(double,double)>(2.0, 5.0)",
            "virtualinvoke $stack3.<java.io.PrintStream: void println(double)>($stack4)",
            "$stack5 = <java.lang.System: java.io.PrintStream out>",
            "$stack6 = staticinvoke <java.lang.Math: double ceil(double)>(5.6)",
            "virtualinvoke $stack5.<java.io.PrintStream: void println(double)>($stack6)",
            "$stack7 = <java.lang.System: java.io.PrintStream out>",
            "virtualinvoke $stack7.<java.io.PrintStream: void println(java.lang.String)>(\"Static import for System.out\")",
            "return")
        .collect(Collectors.toList());
  }
}
