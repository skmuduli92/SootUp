package sootup.core.jimple.visitor;

/*-
 * #%L
 * SootUp
 * %%
 * Copyright (C) 1997 - 2024 Raja Vallée-Rai and others
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

import javax.annotation.Nonnull;
import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.basic.Local;
import sootup.core.jimple.common.constant.Constant;

public class AbstractImmediateVisitor extends AbstractConstantVisitor implements ImmediateVisitor {

  @Override
  public void caseLocal(@Nonnull Local local) {
    defaultCaseImmediate(local);
  }

  @Override
  public void defaultCaseConstant(@Nonnull Constant constant) {
    defaultCaseImmediate(constant);
  }

  private void defaultCaseImmediate(@Nonnull Immediate immediate) {}
}
