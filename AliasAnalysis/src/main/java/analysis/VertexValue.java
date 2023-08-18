package analysis;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.MapWritable;
import stmt.Stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

// VertexValue不会作为消息传递
public class VertexValue implements Writable {
    private final StmtWritable stmtWritable;
    private final SetWritable pre;
    private Pegraph pegraph;
    private MapWritable graphStore; // graphStore: MapWritable<IntWritable, NodeTuple>

    public VertexValue() {
        pre = new SetWritable();
        pegraph = null;
        stmtWritable = new StmtWritable();
        graphStore = null;
    }
    // 只会使用一次
    public VertexValue(String text) {
        Scanner sc = new Scanner(text);
        stmtWritable = new StmtWritable(sc);
        pre = new SetWritable();
        pegraph = null;
        graphStore = null;
    }

    public SetWritable getPre() {
        return pre;
    }

    public Pegraph getPegraph() {
        return pegraph;
    }

    public MapWritable getGraphStore() {
        return graphStore;
    }

    public void setGraphStore(MapWritable graphStore) {
        this.graphStore = graphStore;
    }

    public void setPegraph(Pegraph pegraph) {
        this.pegraph = pegraph;
    }

    public Stmt getStmt() {
        return stmtWritable.get();
    }

    public StmtWritable getStmtWritable() {
        return stmtWritable;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        pre.write(dataOutput);
        stmtWritable.write(dataOutput);

        if(graphStore != null){
            dataOutput.writeByte(1);
            graphStore.write(dataOutput);
        }
        else{
            dataOutput.writeByte(0);
        }

        if (pegraph != null) {
            dataOutput.writeByte(1);
            pegraph.write(dataOutput);
        }
        else {
            dataOutput.writeByte(0);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        pre.readFields(dataInput);
        stmtWritable.readFields(dataInput);

        if(dataInput.readByte() == 1){
            if(graphStore == null)
                graphStore = new MapWritable();
            graphStore.readFields(dataInput);
        }
        
        if (dataInput.readByte() == 1) {
            if (pegraph == null) {
                pegraph = new Pegraph();
            }
            pegraph.readFields(dataInput);
        }
    }
}
