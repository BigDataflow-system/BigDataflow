package alias_stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SkipAStmt extends AStmt
{
	public SkipAStmt()
	{
		this.stmt_value = TYPE.Skip;
	}
	@Override
	public void toString_sub(StringBuilder str)
	{
		str.append("skip");
	}

	@Override
	public AStmt decopy() {
		return new SkipAStmt();
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {

	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {

	}
}
