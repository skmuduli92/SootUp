/*-
 * #%L
 * Soot
 * %%
 * Copyright (C) 15.11.2018 Markus Schmidt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package sootup.java.core.jimple.common.stmt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.stmt.JGotoStmt;
import sootup.core.jimple.common.stmt.JThrowStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.java.core.JavaIdentifierFactory;

/** @author Markus Schmidt, Linghui Luo */
@Tag("Java8")
// TODO: [ms] incorporate Printer i.e. Body+Targets
public class JGotoStmtTest {

  @Test
  public void test() {

    StmtPositionInfo nop = StmtPositionInfo.getNoStmtPositionInfo();
    JavaIdentifierFactory typeFactory = JavaIdentifierFactory.getInstance();

    Local local = new Local("r0", typeFactory.getType("java.lang.Exception"));

    // Stmt
    Stmt targetStmt = new JThrowStmt(local, nop);
    Stmt gStmt = new JGotoStmt(nop);

    // toString
    assertEquals("goto", gStmt.toString());

    // equivTo
    assertFalse(gStmt.equivTo(targetStmt));

    assertTrue(gStmt.equivTo(new JGotoStmt(nop)));
  }
}
