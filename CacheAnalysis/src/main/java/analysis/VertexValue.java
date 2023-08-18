package analysis;

import org.apache.hadoop.io.Writable;
import toImplement.State;
import toImplement.Content;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class VertexValue implements Writable {
    private final SetWritable pre;
    private Content content;
    private State state;

    public VertexValue() {
        pre = new SetWritable();
        content = new Content();
        state = null;
    }

    // 只会使用一次
    public VertexValue(String text) {
        Scanner sc = new Scanner(text);
        content = new Content(sc);
        pre = new SetWritable();
        state = null;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public SetWritable getPre() {
        return pre;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        pre.write(dataOutput);
        content.write(dataOutput);
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
        pre.readFields(dataInput);
        content.readFields(dataInput);
        if (dataInput.readByte() == 1) {
            if (state == null) {
                state = new State();
            }
            state.readFields(dataInput);
        }
    }

    public Content getContent() {
        return content;
    }
}
