package sootup.jimple.frontend.javatestsuite.java6;

import java.util.ArrayList;
import java.util.Arrays;
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
public class DeclareConstructorTest extends JimpleTestSuiteBase {

  public MethodSignature getMethodSignatureInitOneParam() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "<init>", "void", Collections.singletonList("int"));
  }

  public MethodSignature getMethodSignatureInitTwoParam() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "<init>", "void", Arrays.asList("int", "int"));
  }

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignatureInitOneParam());
    assertJimpleStmts(method, expectedBodyStmts());
    method = loadMethod(getMethodSignatureInitTwoParam());
    assertJimpleStmts(method, expectedBodyStmts1());
  }

  public List<String> expectedBodyStmts() {
    return Stream.of(
            "l0 := @this: DeclareConstructor",
            "l1 := @parameter0: int",
            "specialinvoke l0.<java.lang.Object: void <init>()>()",
            "l0.<DeclareConstructor: int var1> = l1",
            "l0.<DeclareConstructor: int var2> = 0",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<String> expectedBodyStmts1() {
    return Stream.of(
            "l0 := @this: DeclareConstructor",
            "l1 := @parameter0: int",
            "l2 := @parameter1: int",
            "specialinvoke l0.<java.lang.Object: void <init>()>()",
            "l0.<DeclareConstructor: int var1> = l1",
            "l0.<DeclareConstructor: int var2> = l2",
            "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
