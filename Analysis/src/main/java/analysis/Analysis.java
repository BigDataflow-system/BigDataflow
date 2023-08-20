package analysis;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import java.util.*;

import java.lang.Iterable;

import data.*;

public class Analysis<V extends VertexValue, M extends Msg> extends BasicComputation<IntWritable, V , NullWritable, M> {
  public Tool tool = null;
  public Fact fact = null;
  public M msg  = null;

  public void setAnalysisConf(){
    // TODO for initialize tool. fact/msg type according to specific dataflow analysis
    // e.g.
    // tool = new CacheTool(); 
    // fact = new CacheState(); 
    // msg = new CacheMsg();
  }

  public boolean beActive(Iterable<M> messages, VertexValue vertexValue){
    // TODO
    return true;
  }

  @Override
  public void compute(Vertex<IntWritable, V, NullWritable> vertex, Iterable<M> messages) {
    setAnalysisConf();

    if (getSuperstep() == 0) {
      final SetWritable entry = getBroadcast("entry");
      /// CommonWrite.method2(entry.toString());
      if(entry.getValues().contains(vertex.getId().get()))
      { 
        //initialize new fact
        // fact = new Fact(); // done by setAnalysisConf
        vertex.getValue().setFact(fact);
        // transfer
        Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
        for(Edge<IntWritable, NullWritable> edge : vertex.getEdges()) {
            msg.setVertexID(vertex.getId());
            msg.setExtra(vertex.getValue());
            msg.setFact(out_fact.getNew());
            sendMessage(edge.getTargetVertexId(), msg);
        }
      }
      vertex.voteToHalt();
    }
    else {
      if(beActive(messages, vertex.getValue())){
        // merge based on old incoming fact and curretn messages to get the new incoming fact
        fact = tool.combine(messages, vertex.getValue());

        // transfer
        Fact out_old_fact = null;
        if(vertex.getValue().getFact() == null){
          out_old_fact = null;
        }
        else{
          out_old_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        }
        Fact out_new_fact = tool.transfer(vertex.getValue().getStmtList(), fact);

        // after transfer(in_fact), different in_facts can result in same out_fact, 
        // so omitted, just compare out_facts 
        // if(!fact.consistent(vertex.getValue().getFact())){ 
        //   vertex.getValue().setFact(fact);
        // }
        
        // propagate
        boolean canPropagate = tool.propagate(out_old_fact, out_new_fact);
        if (canPropagate) {
          vertex.getValue().setFact(fact);
          msg.setVertexID(vertex.getId());
          msg.setExtra(vertex.getValue());
          msg.setFact(out_new_fact.getNew());
          for(Edge<IntWritable,NullWritable> edge : vertex.getEdges()){
            sendMessage(edge.getTargetVertexId(), msg);
          }
        }
      }
      vertex.voteToHalt();
    }
  }
}

