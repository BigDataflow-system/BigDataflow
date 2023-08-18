package alias_data;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArrayWritable implements Writable {
    private List<Integer> pre;
    ArrayWritable () {
        pre = new ArrayList<>();
    }

    public List<Integer> getPre() {
        return pre;
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(pre.size());
        for (int i = 0; i < pre.size(); i++) {
            dataOutput.writeInt(pre.get(i));
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        int size = dataInput.readInt();
        for (int i = 0; i < size; i++) {
            pre.add(dataInput.readInt());
        }
    }
}
