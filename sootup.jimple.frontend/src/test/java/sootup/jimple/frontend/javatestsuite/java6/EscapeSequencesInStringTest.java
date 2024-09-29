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
public class EscapeSequencesInStringTest extends JimpleTestSuiteBase {

  @Test
  public void test() {
    SootMethod method = loadMethod(getMethodSignature("escapeBackslashB"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes backslash b \\u0008\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeBackslashT"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes backslash t \\t\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeBackslashN"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes backslash n \\n\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeBackslashF"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes backslash f \\f\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeBackslashR"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes backslash r \\r\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeDoubleQuotes"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes double quotes \\\"\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeSingleQuote"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes single quote \\'\"",
                "return")
            .collect(Collectors.toList()));

    method = loadMethod(getMethodSignature("escapeBackslash"));
    assertJimpleStmts(
        method,
        Stream.of(
                "l0 := @this: EscapeSequencesInString",
                "l1 = \"This escapes backslash \\\\\"",
                "return")
            .collect(Collectors.toList()));
  }

  public MethodSignature getMethodSignature(String methodName) {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), methodName, "void", Collections.emptyList());
  }
}
