package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PhiStmt extends Stmt{
    private int length = 0;
    private int[] src = null;
    private int dst = -1;

    public PhiStmt()
    {
        this.t = TYPE.Phi;
        this.length = 0;
        this.src = null;
        this.dst = -1;
    }

    public PhiStmt(Scanner sc)
    {
        this.t = TYPE.Phi;

        String dst = sc.next();
        this.dst = Integer.parseInt(dst);

        Set<String> setString = new HashSet<>();

        while (sc.hasNext()) {
            setString.add(sc.next());
        }

        this.length = setString.size();
        this.src = new int[this.length];
        int i = 0;
        for (String str : setString) {
            this.src[i] = Integer.parseInt(str);
            i++;
        }
    }
    public int getDst() {
        return dst;
    }

    public int getLength() {
        return length;
    }

    public int[] getSrcs() {
        return src;
    }

    @Override
    public void toString_sub(StringBuilder str)
    {
        str.append("phi, ").append(getDst()).append("<-");
        for (int i = 0; i < length; i++) {
            str.append(src[i]).append(',');
        }
    }

    @Override
    public Stmt decopy() {
        PhiStmt stmt = new PhiStmt();
        stmt.dst = this.dst;
        stmt.length = this.length;
        stmt.src = new int[length];
        System.arraycopy(this.src, 0, stmt.src, 0, length);
        return stmt;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(dst);
        dataOutput.writeInt(length);
        for (int i = 0; i < length; i++) {
            dataOutput.writeInt(src[i]);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        dst = dataInput.readInt();
        length = dataInput.readInt();
        src = new int[length];
        for (int i = 0; i < length; i++) {
            src[i] = dataInput.readInt();
        }
    }
}
