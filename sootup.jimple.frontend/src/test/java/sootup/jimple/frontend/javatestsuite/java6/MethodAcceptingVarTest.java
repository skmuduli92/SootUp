package sootup.jimple.frontend.javatestsuite.java6;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class MethodAcceptingVarTest extends JimpleTestSuiteBase {

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature("short"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: MethodAcceptingVar",
                "l1 := @parameter0: short",
                "$stack2 = l1 + 1",
                "l1 = (short) $stack2",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("byte"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: MethodAcceptingVar",
                "l1 := @parameter0: byte",
                "$stack2 = l1 + 1",
                "l1 = (byte) $stack2",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("char"));
    assertJimpleStmts(
        method,
        Stream.of("l0 := @this: MethodAcceptingVar", "l1 := @parameter0: char", "l1 = 97", "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("int"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: MethodAcceptingVar",
                "l1 := @parameter0: int",
                "l1 = l1 + 1",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("long"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: MethodAcceptingVar",
                "l1 := @parameter0: long",
                "l1 = 123456777L",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("float"));

    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: MethodAcceptingVar",
                "l1 := @parameter0: float",
                "l1 = 7.77F",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("double"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: MethodAcceptingVar",
                "l1 := @parameter0: double",
                "l1 = 1.787777777",
                "return")
            .collect(Collectors.toList()));
  }

  public MethodSignature getMethodSignature(String datatype) {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(),
        datatype + "Variable",
        "void",
        Collections.singletonList(datatype));
  }
}
