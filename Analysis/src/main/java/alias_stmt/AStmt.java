package alias_stmt;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import data.Stmt;

public class AStmt extends Stmt<TYPE> implements Writable{

  public String toString()
  {
      StringBuilder out = new StringBuilder();
      out.append("(");
      toString_sub(out);
      out.append(")");
      return out.toString();
  }

  public void toString_sub(StringBuilder str){}

  public AStmt decopy(){
    return new AStmt(); 
  }

  public void write(DataOutput out) throws IOException {
    // Wait for instance implementation
  }
  public void readFields(DataInput in) throws IOException {
    // Wait for instance implementation
  }
}
