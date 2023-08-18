package stmt;

import org.apache.hadoop.io.Writable;

public abstract class Stmt implements Writable {
    protected TYPE t;

    public TYPE getType()
    {
        return t;
    }

    public String toString()
    {
        StringBuilder out = new StringBuilder();
        out.append("(");
        toString_sub(out);
        out.append(")");
        return out.toString();
    }

    public abstract void toString_sub(StringBuilder str);

    public abstract Stmt decopy();
}
