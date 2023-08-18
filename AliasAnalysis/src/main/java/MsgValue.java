import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.IntWritable;

public class MsgValue implements Writable{

    private IntWritable vertexID;
    private IntWritable msgType;
    private VertexValue vertexValue;

    public MsgValue()
    {
        vertexID = new IntWritable(0);
        msgType = new IntWritable(0);
        vertexValue = new VertexValue();
    }

    public void setVertexID(IntWritable vertexID) {
        this.vertexID = vertexID;
    }
    public void setMsgType(IntWritable msgType) {
        this.msgType = msgType;
    }
    public void setVertexValue(VertexValue vertexValue) {
        this.vertexValue = vertexValue;
    }

    public IntWritable getVertexID() { return vertexID; }
    public IntWritable getMsgType() {
        return msgType;
    }
    public VertexValue getVertexValue() {
        return vertexValue;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
        msgType.write(dataOutput);
        vertexValue.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        vertexID.readFields(dataInput);
        msgType.readFields(dataInput);
        vertexValue.readFields(dataInput);
    }
}
