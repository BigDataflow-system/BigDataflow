package cache_data;

import java.util.List;
import java.util.Set;

import data.*;
import cache_data.*;

public class CacheTool implements Tool<CacheMsg> {

  public Fact combine(Iterable<CacheMsg> messages, VertexValue vertexValue){
    CacheState old_state = (CacheState)vertexValue.getFact();
    CacheState new_state;
    if(old_state == null){
      new_state = new CacheState();
    }
    else{
      new_state = (CacheState)old_state.getNew();
    }

    for (Msg item : messages) {
      CacheState tmp = (CacheState)item.getFact();
      new_state.merge(tmp);
    }
    return new_state;
  }

  public Fact combine(Set<Fact> predFacts){
    CacheState state = null;
    for (Fact item : predFacts) {
        CacheState tmp = (CacheState)item; 
        if (state == null) 
            state = (CacheState)tmp.getNew();
        else
            state.merge(tmp);
    }
    return state;
  }

  public Fact transfer(StmtList stmtlist, Fact fact){
    
    CacheIRs cacheIRs = (CacheIRs)stmtlist;
    CacheState out_state;
    if(fact == null){ // null representing empty cache
      out_state = new CacheState();
    }
    else{
      CacheState in_state = (CacheState)fact;
      out_state = (CacheState)in_state.getNew();
    }
    
    int size = cacheIRs.getSize();
    Stmt insts[] = cacheIRs.getStmts();
    for (int i = 0 ; i < size; i++) {
        visitInstruction((IRStmt)insts[i], out_state);
    }
    return out_state;
  }

  public void visitInstruction(IRStmt ir, CacheState predModel){
    byte hit_Icache = predModel.AccessIC(ir.getStmt());  //只凭地址进行访问
    if (hit_Icache == 0) {

    }
    else if (hit_Icache == 1) {

    }
    else {
        System.out.println("error IR, invalid visit cache !!!!");
        System.exit(1);
    }
  }

  public boolean propagate(Fact oldFact, Fact newFact){
    if(oldFact == null){
      return true;
    }
    else{
      CacheState oldState = (CacheState)oldFact;
      CacheState newState = (CacheState)newFact;
      return !newState.consistent(oldState);
    }
  }
}