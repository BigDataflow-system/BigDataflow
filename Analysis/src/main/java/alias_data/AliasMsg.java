package alias_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;

import data.*;
import alias_data.AliasStmts;
import alias_data.Pegraph;
import alias_stmt.AStmt;

public class AliasMsg extends Msg {
  public AliasStmts stmtlist;

  public AliasMsg() {
      vertexID = new IntWritable(0);
      fact = new Pegraph();
      stmtlist = null;
  }

  public void setStmtList(StmtList stmtlist) {
      this.stmtlist = (AliasStmts)stmtlist;
  }

  public AliasStmts getStmtList() {
    return this.stmtlist;
  }

  public AStmt getStmt() {
    return (AStmt)stmtlist.getStmts()[0];
  }

  @Override
  public void setExtra(VertexValue vertexValue){
    AliasStmts tmp = (AliasStmts)vertexValue.getStmtList();
    setStmtList(tmp.getNew());
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
      vertexID.write(dataOutput);
      if (fact != null) {
          dataOutput.writeByte(1);
          fact.write(dataOutput);
      }
      else {
          dataOutput.writeByte(0);
      }

      if (stmtlist != null) {
        dataOutput.writeByte(1);
        stmtlist.write(dataOutput);
      }
      else {
        dataOutput.writeByte(0);
      }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
      vertexID.readFields(dataInput);
      if (dataInput.readByte() == 1) {
          fact = new Pegraph();
          fact.readFields(dataInput);
      }

      if (dataInput.readByte() == 1) {
        if (stmtlist == null) {
          stmtlist = new AliasStmts();
        }
        stmtlist.readFields(dataInput);
      }
  }
}