package alias_stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class CalleefptrAStmt extends AStmt {
    private int dst;

    public CalleefptrAStmt()
    {
        this.stmt_value = TYPE.Calleefptr;
        this.dst = -1;
    }

    public CalleefptrAStmt(Scanner sc)
    {
        this.stmt_value = TYPE.Calleefptr;
        this.dst = sc.nextInt();
    }

    public int getDst()
    {
        return dst;
    }

    @Override
    public void toString_sub(StringBuilder str)
    {
        str.append("calleefptr, ").append(getDst());
    }

    @Override
    public AStmt decopy() {
        CalleefptrAStmt stmt = new CalleefptrAStmt();
        stmt.dst = this.dst;
        return stmt;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(dst);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        dst = dataInput.readInt();
    }
}
