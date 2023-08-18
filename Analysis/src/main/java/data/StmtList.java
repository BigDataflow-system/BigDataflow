package data;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class StmtList implements Writable {
  protected Stmt[] stmts;
  protected int size = 0;

  public StmtList(){
    stmts = null;
    size = 0;
  }
  
  public int getSize(){
    return size;
  }

  public Stmt[] getStmts(){
    return stmts;
  }

  public void setSize(int newSize){
    size = newSize;
  }

  public void setStmts(List<Stmt> newStmts){
    for(int j = 0; j < size; j++)
      stmts[j] = null;
    
    size = newStmts.size();
    stmts = new Stmt[size];
    for(int j = 0; j < size; j++)
      stmts[j] = newStmts.get(j);
  }

  public void setStmts(Stmt[] newStmts, int newSize){
    for(int j = 0; j < size; j++)
      stmts[j] = null;
    
    size = newSize;
    stmts = new Stmt[size];
    for(int j = 0; j < size; j++)
      stmts[j] = newStmts[j];
  }

  // public void print(){
  //   for(int j = 0; j < size; j++)
  //     stmts[j].print();
  // }

  public void clear(){
    for(int j = 0; j < size; j++)
      stmts[j] = null;
    size = 0;
  }

  @Override
  public void write(DataOutput out) throws IOException{

  }

  @Override
  public void readFields(DataInput in) throws IOException{

  }
}
