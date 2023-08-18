package analysis;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetWritable implements Writable {
    private final Set<Integer> values;

    public SetWritable() {
        this.values = new HashSet<>();
    }

    public Set<Integer> getValues() {
        return values;
    }

    public void addValue(IntWritable value) {
        this.values.add(value.get());
    }

    public void addEntry(int value) {
        this.values.add(value);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(values.size());
        for(Integer t : values) {
            out.writeInt(t);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        values.clear();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            values.add(in.readInt());
        }
    }
}
