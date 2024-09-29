package sootup.java.frontend.conversion;

import static org.junit.jupiter.api.Assertions.*;
import static sootup.core.util.Utils.assertEquiv;
import static sootup.core.util.Utils.assertInstanceOfSatisfying;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.jimple.Jimple;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.JIdentityStmt;
import sootup.core.jimple.common.stmt.JReturnStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.Body;
import sootup.core.model.SootMethod;
import sootup.core.signatures.FieldSignature;
import sootup.core.types.PrimitiveType;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.types.JavaClassType;
import sootup.java.frontend.WalaClassLoaderTestUtils;

/** @author Linghui Luo */
@Tag("Java8")
public class GetInstructionConversionTest {

  private WalaJavaClassProvider loader;
  private JavaIdentifierFactory typeFactory;
  private JavaClassType declareClassSig;

  @BeforeEach
  public void loadClassesWithWala() {
    String srcDir = "../shared-test-resources/wala-tests/";
    loader = new WalaJavaClassProvider(srcDir);
    typeFactory = JavaIdentifierFactory.getInstance();
    declareClassSig = typeFactory.getClassType("alreadywalaunittests.InnerClassAA");
  }

  @Test
  public void test() {
    Optional<SootMethod> m =
        WalaClassLoaderTestUtils.getSootMethod(
            loader,
            typeFactory.getMethodSignature(
                declareClassSig, "getA_X", "int", Collections.emptyList()));
    assertTrue(m.isPresent());
    SootMethod method = m.get();

    Body body = method.getBody();
    assertNotNull(body);

    List<Stmt> stmts = body.getStmts();
    assertEquals(3, stmts.size());

    assertInstanceOfSatisfying(
        stmts.get(0),
        JIdentityStmt.class,
        stmt -> {
          assertEquiv(
              new Local("r0", typeFactory.getClassType("alreadywalaunittests.InnerClassAA")),
              stmt.getLeftOp());
          assertEquiv(
              Jimple.newThisRef(typeFactory.getClassType("alreadywalaunittests.InnerClassAA")),
              stmt.getRightOp());
        });

    assertInstanceOfSatisfying(
        stmts.get(1),
        JAssignStmt.class,
        stmt -> {
          assertEquiv(new Local("i0", PrimitiveType.getInt()), stmt.getLeftOp());
          assertInstanceOfSatisfying(
              stmt.getRightOp(),
              JInstanceFieldRef.class,
              JFieldRef -> {
                assertEquiv(
                    new Local("r0", typeFactory.getClassType("alreadywalaunittests.InnerClassAA")),
                    JFieldRef.getBase());

                FieldSignature fieldSig = JFieldRef.getFieldSignature();
                assertNotNull(fieldSig);
                assertEquals("a_x", fieldSig.getName());
                assertEquals(PrimitiveType.getInt(), fieldSig.getType());
                assertEquals(
                    typeFactory.getClassType("alreadywalaunittests.InnerClassAA"),
                    fieldSig.getDeclClassType());
              });
        });

    assertInstanceOfSatisfying(
        stmts.get(2),
        JReturnStmt.class,
        stmt -> assertEquiv(new Local("i0", PrimitiveType.getInt()), stmt.getOp()));
  }
}
