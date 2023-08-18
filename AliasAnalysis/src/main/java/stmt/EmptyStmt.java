package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EmptyStmt extends Stmt{

    public EmptyStmt()
    {
        this.t = TYPE.Empty;
    }
    @Override
    public void toString_sub(StringBuilder str) {
        str.append("empty");
    }

    @Override
    public Stmt decopy() {
        return new EmptyStmt();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }
}
