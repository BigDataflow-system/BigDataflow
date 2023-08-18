package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RetStmt extends Stmt
{
	public RetStmt()
	{
		this.t = TYPE.Ret;
	}
	@Override
	public void toString_sub(StringBuilder str)
	{
		str.append("ret");
	}

	@Override
	public Stmt decopy() {
		return new RetStmt();
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {

	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {

	}
}
