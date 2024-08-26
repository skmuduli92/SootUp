package sootup.core.jimple.javabytecode.stmt;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999-2020 Patrick Lam, Markus Schmidt, Linghui luo and others
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

import java.util.stream.Stream;
import javax.annotation.Nonnull;
import sootup.core.jimple.Jimple;
import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.stmt.AbstractStmt;
import sootup.core.jimple.common.stmt.FallsThroughStmt;
import sootup.core.jimple.visitor.StmtVisitor;
import sootup.core.util.printer.StmtPrinter;

/** A statement that enters a JVM monitor, thereby synchronizing its following statements. */
public final class JEnterMonitorStmt extends AbstractStmt implements FallsThroughStmt {

  protected final Immediate op;

  public JEnterMonitorStmt(@Nonnull Immediate op, @Nonnull StmtPositionInfo positionInfo) {
    super(positionInfo);
    this.op = op;
  }

  @Override
  public String toString() {
    return Jimple.ENTERMONITOR + " " + op.toString();
  }

  @Override
  public void toString(@Nonnull StmtPrinter up) {
    up.literal(Jimple.ENTERMONITOR);
    up.literal(" ");
    op.toString(up);
  }

  @Override
  public <V extends StmtVisitor> V accept(@Nonnull V v) {
    v.caseEnterMonitorStmt(this);
    return v;
  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }

  @Nonnull
  public Immediate getOp() {
    return op;
  }

  @Override
  @Nonnull
  public Stream<Value> getUses() {
    return Stream.concat(op.getUses(), Stream.of(op));
  }

  @Override
  public int equivHashCode() {
    return op.equivHashCode();
  }

  @Override
  public boolean equivTo(@Nonnull Object o, @Nonnull JimpleComparator comparator) {
    return comparator.caseEnterMonitorStmt(this, o);
  }

  @Nonnull
  public JEnterMonitorStmt withOp(@Nonnull Immediate op) {
    return new JEnterMonitorStmt(op, getPositionInfo());
  }

  @Nonnull
  public JEnterMonitorStmt withPositionInfo(@Nonnull StmtPositionInfo positionInfo) {
    return new JEnterMonitorStmt(getOp(), positionInfo);
  }
}
