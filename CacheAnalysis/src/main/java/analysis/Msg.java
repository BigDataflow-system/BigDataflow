package analysis;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import toImplement.State;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Msg implements Writable {
    private IntWritable vertexID;
    private State state;

    public Msg()
    {
        vertexID = new IntWritable(0);
        state = null;
    }

    public void setVertexID(IntWritable id) {
        this.vertexID.set(id.get());
    }

    public IntWritable getVertexID() {
        return vertexID;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
        if (state != null) {
            dataOutput.writeByte(1);
            state.write(dataOutput);
        }
        else {
            dataOutput.writeByte(0);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        vertexID.readFields(dataInput);
        if (dataInput.readByte() == 1) {
            // 保证flushing过程中以及遍历message时对象的重用
            if (state == null) {
                state = new State();
            }
            state.readFields(dataInput);
        }
    }
}

