package analysis;

import org.apache.hadoop.io.Writable;
import stmt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

public class StmtWritable implements Writable {
    private Stmt stmt;

    public StmtWritable() {
        stmt = new EmptyStmt();
    }

    public StmtWritable(Scanner sc) {
        stmt = Tools.newStmt(sc);
    }

    public Stmt get() {
        return stmt;
    }

    public StmtWritable getNew() {
        StmtWritable tmp = new StmtWritable();
        tmp.stmt = this.stmt.decopy();
        return tmp;
    }

    public void setDeep(StmtWritable stmtWritable) {
        this.stmt = stmtWritable.stmt.decopy();
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        switch (stmt.getType()) {
            case Assign:
                dataOutput.writeByte(1);
                break;
            case Load:
                dataOutput.writeByte(2);
                break;
            case Store:
                dataOutput.writeByte(3);
                break;
            case Alloca:
                dataOutput.writeByte(4);
                break;
            case Phi:
                dataOutput.writeByte(5);
                break;
            case Call:
                dataOutput.writeByte(6);
                break;
            case Return:
                dataOutput.writeByte(7);
                break;
            case Ret:
                dataOutput.writeByte(8);
                break;
            case Skip:
                dataOutput.writeByte(9);
                break;
            case Callfptr:
                dataOutput.writeByte(10);
                break;
            case Calleefptr:
                dataOutput.writeByte(11);
                break;
            case Empty:
                dataOutput.writeByte(12);
                break;
            default:
                System.out.println("write wrong stmt type");
        }
        stmt.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        switch (dataInput.readByte()) {
            case 1:
                stmt = new AssignStmt();
                break;
            case 2:
                stmt = new LoadStmt();
                break;
            case 3:
                stmt = new StoreStmt();
                break;
            case 4:
                stmt = new AllocStmt();
                break;
            case 5:
                stmt = new PhiStmt();
                break;
            case 6:
                stmt = new CallStmt();
                break;
            case 7:
                stmt = new ReturnStmt();
                break;
            case 8:
                stmt = new RetStmt();
                break;
            case 9:
                stmt = new SkipStmt();
                break;
            case 10:
                stmt = new CallfptrStmt();
                break;
            case 11:
                stmt = new CalleefptrStmt();
                break;
            case 12:
                stmt = new EmptyStmt();
                break;
            default:
                System.out.println("read wrong stmt type");
        }
        stmt.readFields(dataInput);
    }
}
