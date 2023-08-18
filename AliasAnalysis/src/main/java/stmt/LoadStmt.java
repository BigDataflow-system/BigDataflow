package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class LoadStmt extends Stmt
{
	private int src;
	private int dst;
	private int auxiliary;
	
	public LoadStmt()
	{
		this.t = TYPE.Load;
		this.src = -1;
		this.dst = -1;
		this.auxiliary = -1;
	}
	
	public LoadStmt(Scanner sc)
	{
		this.t = TYPE.Load;
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
		str.append("load, ").append(getDst()).append("<-").append(getSrc()).append("<-").append(getAuxiliary());
	}

	@Override
	public Stmt decopy() {
		LoadStmt stmt = new LoadStmt();
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