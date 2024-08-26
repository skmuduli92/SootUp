/* Qilin - a Java Pointer Analysis Framework
 * Copyright (C) 2021-2030 Qilin developers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3.0 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <https://www.gnu.org/licenses/lgpl-3.0.en.html>.
 */

package qilin.core.builder;

import java.util.*;
import qilin.core.PTA;
import qilin.core.context.Context;
import qilin.core.pag.*;
import qilin.core.sets.P2SetVisitor;
import qilin.core.sets.PointsToSetInternal;
import qilin.util.DataFactory;
import qilin.util.PTAUtils;
import sootup.core.jimple.basic.Trap;
import sootup.core.jimple.common.stmt.JIdentityStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.SootMethod;
import sootup.core.types.Type;

public class ExceptionHandler {
  protected final Map<Node, Collection<ExceptionThrowSite>> throwNodeToSites;
  protected PTA pta;
  protected PAG pag;

  public ExceptionHandler(PTA pta) {
    this.pta = pta;
    this.pag = pta.getPag();
    this.throwNodeToSites = DataFactory.createMap((int) pta.getView().getClasses().count());
  }

  public Collection<ExceptionThrowSite> throwSitesLookUp(VarNode throwNode) {
    return throwNodeToSites.getOrDefault(throwNode, Collections.emptySet());
  }

  public boolean addThrowSite(Node throwNode, ExceptionThrowSite ets) {
    Collection<ExceptionThrowSite> throwSites =
        throwNodeToSites.computeIfAbsent(throwNode, k -> DataFactory.createSet());
    return throwSites.add(ets);
  }

  public void exceptionDispatch(PointsToSetInternal p2set, ExceptionThrowSite site) {
    p2set.forall(
        new P2SetVisitor(pta) {
          public void visit(Node n) {
            dispatch((AllocNode) n, site);
          }
        });
  }

  /*
   * dispatch the exception objects by following the exception-cath-links in Doop-ISSTA09.
   * */
  public void dispatch(AllocNode throwObj, ExceptionThrowSite site) {
    Type type = throwObj.getType();
    ContextMethod momc = site.container();
    SootMethod sm = momc.method();
    Context context = momc.context();
    MethodPAG mpag = pag.getMethodPAG(sm);
    MethodNodeFactory nodeFactory = mpag.nodeFactory();
    VarNode throwNode = site.getThrowNode();
    List<Trap> trapList =
        mpag.stmt2wrapperedTraps.getOrDefault(site.getUnit(), Collections.emptyList());
    for (Trap trap : trapList) {
      if (PTAUtils.canStoreType(pta.getView(), type, trap.getExceptionType())) {
        Stmt handler = trap.getHandlerStmt();
        assert handler instanceof JIdentityStmt;
        JIdentityStmt handlerStmt = (JIdentityStmt) handler;
        Node caughtParam = nodeFactory.getNode(handlerStmt.getRightOp());
        Node dst = pta.parameterize(caughtParam, context);
        pag.addEdge(throwObj, dst);
        // record an edge from base --> caughtParam on the methodPag.
        recordImplictEdge(throwNode, caughtParam, mpag);
        return;
      }
    }
    // No trap handle the throwable object in the method.
    Node methodThrowNode = nodeFactory.caseMethodThrow();
    Node dst = pta.parameterize(methodThrowNode, context);
    pag.addEdge(throwObj, dst);
    // record an edge from base --> methodThrowNode on the methodPag.
    recordImplictEdge(throwNode, methodThrowNode, mpag);
  }

  private void recordImplictEdge(Node src, Node dst, MethodPAG mpag) {
    if (src instanceof ContextVarNode) {
      src = ((ContextVarNode) src).base();
    }
    mpag.addExceptionEdge(src, dst);
  }
}
