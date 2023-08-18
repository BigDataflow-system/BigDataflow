package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SkipStmt extends Stmt
{
	public SkipStmt()
	{
		this.t = TYPE.Skip;
	}
	@Override
	public void toString_sub(StringBuilder str)
	{
		str.append("skip");
	}

	@Override
	public Stmt decopy() {
		return new SkipStmt();
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {

	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {

	}
}
