package data;

import java.util.*;

public interface Tool<M> {
  public Fact combine(Iterable<M> message, VertexValue vertexValue);
  public Fact combine(Set<Fact> predFacts);
  public Fact transfer(StmtList stmts, Fact incomingFact);
  public boolean propagate(Fact oldFact, Fact newFact);
}