package sootup.jimple.frontend.javatestsuite.java8;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import sootup.core.model.ClassModifier;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag("Java8")
public class RepeatingAnnotationsTest extends JimpleTestSuiteBase {

  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), "annotaionMethod", "void", Collections.emptyList());
  }

  @Disabled
  public void annotationTest() {
    // TODO: after annotations are implemented
    SootMethod method = loadMethod(getMethodSignature());
    assertJimpleStmts(method, expectedBodyStmts());
    SootClass sootClass = loadClass(getDeclaredClassSignature());
    assertTrue(ClassModifier.isAnnotation(sootClass.getModifiers()));
  }

  public List<String> expectedBodyStmts() {
    return Stream.of("r0 := @this: RepeatingAnnotations", "r1 = \"\"", "r2 = \"\"", "return")
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
