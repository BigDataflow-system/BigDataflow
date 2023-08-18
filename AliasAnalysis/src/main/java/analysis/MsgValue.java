package analysis;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import stmt.Stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class NodeTuple implements Writable {
    public StmtWritable stmtWritable;
    public Pegraph pegraph;

    public NodeTuple() {
        pegraph = null;
        stmtWritable = null;
    }

    public Pegraph getPegraph() {
        return pegraph;
    }

    public void setPegraph(Pegraph pegraph) {
        this.pegraph = pegraph;
    }

    public Stmt getStmt() {
        return stmtWritable.get();
    }

    public void setStmt(StmtWritable stmt) {
        this.stmtWritable = stmt;
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

        if (stmtWritable != null) {
            dataOutput.writeByte(1);
            stmtWritable.write(dataOutput);
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
            if (stmtWritable == null) {
                stmtWritable = new StmtWritable();
            }
            stmtWritable.readFields(dataInput);
        }
    }

    public NodeTuple getNew() {
        NodeTuple nodeTuple = new NodeTuple();
        nodeTuple.pegraph = new Pegraph();
        nodeTuple.stmtWritable = new StmtWritable();
        // Writable类型为可变类型
        nodeTuple.pegraph.setDeep(this.pegraph);
        nodeTuple.stmtWritable.setDeep(this.stmtWritable);
        return nodeTuple;
    }
}

public class MsgValue implements Writable {
    private IntWritable vertexID;
    private NodeTuple nodeTuple;

    public MsgValue() {
        vertexID = new IntWritable(0);
        nodeTuple = new NodeTuple();
    }

    public void setMsgToNull() {
        this.nodeTuple = null;
    }

    public void setVertexID(IntWritable vertexID) {
        this.vertexID.set(vertexID.get());
    }
    public void setVertexValue(Pegraph pegraph) {
        this.nodeTuple.setPegraph(pegraph);
    }
    public void setStmtWritable(StmtWritable stmt) {
        this.nodeTuple.setStmt(stmt);
    }

    public IntWritable getVertexID() {
        return vertexID;
    }
    public NodeTuple getNodeTuple() {
        return nodeTuple;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
        if (nodeTuple != null) {
            dataOutput.writeByte(1);
            nodeTuple.write(dataOutput);
        }
        else {
            dataOutput.writeByte(0);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        vertexID.readFields(dataInput);
        if (dataInput.readByte() == 1) {
            nodeTuple.readFields(dataInput);
        }
    }
}
