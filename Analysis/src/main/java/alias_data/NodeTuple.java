package alias_data;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import data.StmtList;
import alias_data.AliasStmts;
import alias_stmt.AStmt;

public class NodeTuple implements Writable{
  public AliasStmts stmtlist;
  public Pegraph pegraph;

  public NodeTuple() {
    stmtlist = null;
    pegraph = null;
  }

  public AStmt getStmt() {
      return (AStmt)stmtlist.getStmts()[0];
  }

  public Pegraph getPegraph() {
    return pegraph;
  }

  public void setPegraph(Pegraph pegraph) {
    this.pegraph = pegraph;
  }

  public void setStmtList(StmtList stmt) {
      this.stmtlist = (AliasStmts)stmt;
  }

  public AliasStmts getStmtList() {
    return this.stmtlist;
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {

    if (pegraph != null) {
      dataOutput.writeByte(1);
      pegraph.write(dataOutput);
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
    if (dataInput.readByte() == 1) {
        if (pegraph == null) {
            pegraph = new Pegraph();
        }
        pegraph.readFields(dataInput);
    }
    if (dataInput.readByte() == 1) {
        if (stmtlist == null) {
          stmtlist = new AliasStmts();
        }
        stmtlist.readFields(dataInput);
    }
  }

  public NodeTuple getNew() {
      NodeTuple nodeTuple = new NodeTuple();
      nodeTuple.pegraph = new Pegraph();
      nodeTuple.stmtlist = new AliasStmts();
      nodeTuple.pegraph.setDeep(this.pegraph);
      nodeTuple.stmtlist.setDeep(this.stmtlist);
      return nodeTuple;
  }
}