package toImplement;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Content implements Writable {
    private List<Integer> IRs;
    public Content(){
        IRs = new ArrayList<>();
        //TODO: 初始化节点内容
    }

    public Content(Scanner sc) {
        IRs = new ArrayList<>();
        //TODO: 由传入字符串序列初始化节点内容
        while (sc.hasNext()) {
            String str = sc.next();
            IRs.add(Integer.decode(str));
        }
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //TODO: 序列化实现
        dataOutput.writeInt(IRs.size());
        for (Integer ir : IRs) {
            dataOutput.writeInt(ir);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        //TODO: 反序列化实现
        IRs.clear();
        int size = dataInput.readInt();
        while (size > 0) {
            IRs.add(dataInput.readInt());
            size--;
        }
    }

    public List<Integer> getAllContent() {
        return IRs;
    }
}
