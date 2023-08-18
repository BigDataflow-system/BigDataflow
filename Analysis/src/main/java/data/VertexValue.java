package data;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VertexValue implements Writable {
    protected StmtList stmts;
    protected Fact fact;

    public VertexValue() {
        stmts = null;
        fact = null;
    }
    
    public Fact getFact() {
        return fact;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }

    public StmtList getStmtList() {
        return stmts;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        // wait for implementation to serialize vertexvalue under specific dataflow analysis
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        // wait for implementation to deserialize vertexvalue under specific dataflow analysis
    }
    
}
