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
    stmts = new AliasStmts();
    fact = null;
    graphStore = null;
  }

  public AliasVertexValue(String text){
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