package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class CalleefptrStmt extends Stmt {
    private int dst;

    public CalleefptrStmt()
    {
        this.t = TYPE.Calleefptr;
        this.dst = -1;
    }

    public CalleefptrStmt(Scanner sc)
    {
        this.t = TYPE.Calleefptr;
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
    public Stmt decopy() {
        CalleefptrStmt stmt = new CalleefptrStmt();
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
