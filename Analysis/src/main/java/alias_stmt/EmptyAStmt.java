package alias_stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EmptyAStmt extends AStmt{

    public EmptyAStmt()
    {
        this.stmt_value = TYPE.Empty;
    }
    @Override
    public void toString_sub(StringBuilder str) {
        str.append("empty");
    }

    @Override
    public AStmt decopy() {
        return new EmptyAStmt();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {

    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {

    }
}
