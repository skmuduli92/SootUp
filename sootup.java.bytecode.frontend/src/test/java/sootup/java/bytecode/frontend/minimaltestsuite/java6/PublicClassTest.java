package sootup.java.bytecode.frontend.minimaltestsuite.java6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import categories.TestCategories;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.model.ClassModifier;
import sootup.core.model.SootClass;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.java.bytecode.frontend.minimaltestsuite.MinimalBytecodeTestSuiteBase;

/** @author Kaustubh Kelkar */
@Tag(TestCategories.JAVA_8_CATEGORY)
public class PublicClassTest extends MinimalBytecodeTestSuiteBase {

  @Test
  public void test() {
    SootClass clazz = loadClass(getDeclaredClassSignature());
    assertEquals(EnumSet.of(ClassModifier.PUBLIC, ClassModifier.SUPER), clazz.getModifiers());

    SootMethod method;
    method = clazz.getMethod(getMethodSignature("private").getSubSignature()).get();
    assertTrue(method.isPrivate());
    assertJimpleStmts(method, expectedBodyStmts());

    method = clazz.getMethod(getMethodSignature("protected").getSubSignature()).get();
    assertTrue(method.isProtected());
    assertJimpleStmts(method, expectedBodyStmts());

    method = clazz.getMethod(getMethodSignature("public").getSubSignature()).get();
    assertTrue(method.isPublic());
    assertJimpleStmts(method, expectedBodyStmts());

    method = clazz.getMethod(getMethodSignature("noModifier").getSubSignature()).get();
    assertTrue(method.getModifiers().isEmpty());
    assertJimpleStmts(method, expectedBodyStmts());
  }

  public MethodSignature getMethodSignature(String modifier) {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), modifier + "Method", "void", Collections.emptyList());
  }

  /**
   *
   *
   * <pre>
   * public void publicMethod() {
   *
   * }
   * private void privateMethod() {
   *
   * }
   * protected void protectedMethod() {
   *
   * }
   * void noModifierMethod() {
   *
   * }
   *
   * </pre>
   */
  @Override
  public List<String> expectedBodyStmts() {
    return Stream.of("this := @this: PublicClass", "return").collect(Collectors.toList());
  }
}
