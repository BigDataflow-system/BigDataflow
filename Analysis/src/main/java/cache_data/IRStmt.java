package cache_data;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import data.Stmt;

public class IRStmt extends Stmt<Integer> implements Writable {

  public void print(){
    System.out.println(stmt_value);
  }

  public void write(DataOutput out) throws IOException {
    out.writeInt(stmt_value);
  }

  public void readFields(DataInput in) throws IOException {
    setStmt(in.readInt());
  }
}