package data;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Msg implements Writable{
    protected IntWritable vertexID;
    protected Fact fact;

    public Msg()
    {
        vertexID = new IntWritable(0);
        fact = null;
    }

    public void setVertexID(IntWritable id) {
        this.vertexID.set(id.get());
    }

    public IntWritable getVertexID() {
        return vertexID;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }

    public Fact getFact() {
        return fact;
    }
    
    public void setExtra(VertexValue vertexValue){

    }

    public void setMsgToNull() {
        fact = null;
    }
  
    @Override
    public void write(DataOutput dataOutput) throws IOException{
      // wait for implementation
    }
    
    @Override
    public void readFields(DataInput dataInput) throws IOException{
      // wait for implementation
    }
}
