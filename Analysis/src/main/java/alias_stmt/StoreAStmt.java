package alias_stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class StoreAStmt extends AStmt
{
	private int src;
	private int dst;
	private int auxiliary;
	
	public StoreAStmt()
	{
		this.stmt_value = TYPE.Store;
		this.src = -1;
		this.dst = -1;
		this.auxiliary = -1;
	}

	public StoreAStmt(Scanner sc)
	{
		this.stmt_value = TYPE.Store;
		this.dst = sc.nextInt();
		this.src = sc.nextInt();
		this.auxiliary = sc.nextInt();
	}

	public int getSrc()
	{
		return src;
	}

	public int getDst()
	{
		return dst;
	}

	public int getAuxiliary()
	{
		return auxiliary;
	}
	
	@Override
	public  void toString_sub(StringBuilder str)
	{
		str.append("store, ").append(getDst()).append("<-").append(getSrc()).append("<-").append(getAuxiliary());
	}

	@Override
	public AStmt decopy() {
		StoreAStmt stmt = new StoreAStmt();
		stmt.src = this.src;
		stmt.dst = this.dst;
		stmt.auxiliary = this.auxiliary;
		return stmt;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(src);
		dataOutput.writeInt(dst);
		dataOutput.writeInt(auxiliary);
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		src = dataInput.readInt();
		dst = dataInput.readInt();
		auxiliary = dataInput.readInt();
	}
}
