package stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class AssignStmt extends Stmt
{
	private int src;
	private int dst;
	
	public AssignStmt()
	{
		this.t = TYPE.Assign;
		this.src = -1;
		this.dst = -1;
	}
	
	public AssignStmt(Scanner sc)
	{
		this.t = TYPE.Assign;
		this.dst = sc.nextInt();
		this.src = sc.nextInt();
	}
	
	public int getSrc() 
	{
		return src;
	}

	public int getDst() 
	{
		return dst;
	}
	
	@Override
	public  void toString_sub(StringBuilder str)
	{
		str.append("assign, ").append(getDst()).append("<-").append(getSrc());
	}

	@Override
	public Stmt decopy() {
		AssignStmt stmt = new AssignStmt();
		stmt.src = this.src;
		stmt.dst = this.dst;
		return stmt;
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {
		dataOutput.writeInt(src);
		dataOutput.writeInt(dst);
	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {
		src = dataInput.readInt();
		dst = dataInput.readInt();
	}
}