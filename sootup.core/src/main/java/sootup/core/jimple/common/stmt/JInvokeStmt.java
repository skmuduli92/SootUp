package sootup.core.jimple.common.stmt;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999-2020 Patrick Lam, Linghui Luo, Markus Schmidt and others
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
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import sootup.core.jimple.basic.JimpleComparator;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.basic.Value;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;
import sootup.core.jimple.visitor.StmtVisitor;
import sootup.core.util.printer.StmtPrinter;

/** A method call */
public final class JInvokeStmt extends AbstractStmt implements FallsThroughStmt, InvokableStmt {

  @Nonnull private final AbstractInvokeExpr invokeExpr;

  public JInvokeStmt(
      @Nonnull AbstractInvokeExpr invokeExpr, @Nonnull StmtPositionInfo positionInfo) {
    super(positionInfo);
    this.invokeExpr = invokeExpr;
  }

  @Override
  public boolean containsInvokeExpr() {
    return true;
  }

  @Override
  public boolean invokesStaticInitializer() {
    return invokeExpr instanceof JStaticInvokeExpr;
  }

  @Override
  public String toString() {
    return invokeExpr.toString();
  }

  @Override
  public void toString(@Nonnull StmtPrinter up) {
    invokeExpr.toString(up);
  }

  @Override
  @Nonnull
  public Optional<AbstractInvokeExpr> getInvokeExpr() {
    return Optional.of(invokeExpr);
  }

  @Nonnull
  @Override
  public Stream<Value> getUses() {
    return Stream.concat(invokeExpr.getUses(), Stream.of(invokeExpr));
  }

  @Override
  public <V extends StmtVisitor> V accept(@Nonnull V v) {
    v.caseInvokeStmt(this);
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

  @Override
  public boolean equivTo(Object o, @Nonnull JimpleComparator comparator) {
    return comparator.caseInvokeStmt(this, o);
  }

  @Override
  public int equivHashCode() {
    return invokeExpr.equivHashCode();
  }

  @Nonnull
  public JInvokeStmt withInvokeExpr(AbstractInvokeExpr invokeExpr) {
    return new JInvokeStmt(invokeExpr, getPositionInfo());
  }

  @Nonnull
  public JInvokeStmt withPositionInfo(@Nonnull StmtPositionInfo positionInfo) {
    return new JInvokeStmt(getInvokeExpr().get(), positionInfo);
  }
}
