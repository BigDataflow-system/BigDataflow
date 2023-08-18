package alias_data;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.MapWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

import data.*;
import alias_stmt.AStmt;

public class AliasVertexValue extends VertexValue{
  private MapWritable graphStore; // graphStore: MapWritable<IntWritable, NodeTuple>

  public AliasVertexValue(){
    // pegraph = null;
    // stmtWritable = new StmtWritable();
    // graphStore = null;
    stmts = new AliasStmts();
    fact = null;
    graphStore = null;
  }

  public AliasVertexValue(String text){
    // Scanner sc = new Scanner(text);
    // stmtWritable = new StmtWritable(sc);
    // pegraph = null;
    // graphStore = null;
    Scanner sc = new Scanner(text);
    stmts = new AliasStmts(sc);
    fact = null;
    graphStore = null;
  }

  public AStmt getStmt() {
    return (AStmt)stmts.getStmts()[0];
  }

  public MapWritable getGraphStore() {
        return graphStore;
  }

  public void setGraphStore(MapWritable graphStore) {
        this.graphStore = graphStore;
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    // stmtWritable.write(dataOutput);
    // if(graphStore != null){
    //     dataOutput.writeByte(1);
    //     graphStore.write(dataOutput);
    // }
    // else{
    //     dataOutput.writeByte(0);
    // }
    
    // if (pegraph != null) {
    //     dataOutput.writeByte(1);
    //     pegraph.write(dataOutput);
    // }
    // else {
    //     dataOutput.writeByte(0);
    // }
    stmts.write(dataOutput);
    if(graphStore != null){
        dataOutput.writeByte(1);
        graphStore.write(dataOutput);
    }
    else{
        dataOutput.writeByte(0);
    }

    if (fact != null) {
      dataOutput.writeByte(1);
      fact.write(dataOutput);
    }
    else {
      dataOutput.writeByte(0);
    }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    // stmtWritable.readFields(dataInput);
    // if(dataInput.readByte() == 1){
    //   if(graphStore == null)
    //     graphStore = new MapWritable();
    //   graphStore.readFields(dataInput);
    // }
        
    // if (dataInput.readByte() == 1) {
    //   if (pegraph == null) {
    //     pegraph = new Pegraph();
    //   }
    //   pegraph.readFields(dataInput);
    // }

    stmts.readFields(dataInput);
    if(dataInput.readByte() == 1){
      if(graphStore == null)
        graphStore = new MapWritable();
      graphStore.readFields(dataInput);
    }

    if (dataInput.readByte() == 1) {
      if (fact == null) {
        fact = new Pegraph();
      }
      fact.readFields(dataInput);
    }
  }

}