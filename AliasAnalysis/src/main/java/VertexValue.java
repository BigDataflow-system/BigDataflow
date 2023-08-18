import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class VertexValue implements Writable {
    private IntWritable data;
    private Set<Integer> pre;

    public VertexValue() {
        data = new IntWritable(0);
        pre = new HashSet<>();
    }

    public VertexValue(int t) {
        data = new IntWritable(t);
        pre = new HashSet<>();
    }

    public Set<Integer> getPre() {
        return pre;
    }

    public IntWritable getData() {
        return data;
    }

    public void setData(int data) {
        this.data = new IntWritable(data);
    }

    public void setPre(Set<Integer> pre) {
        this.pre = pre;
    }


    @Override
    public void write(DataOutput dataOutput) throws IOException {
        data.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        data.readFields(dataInput);
    }
}
