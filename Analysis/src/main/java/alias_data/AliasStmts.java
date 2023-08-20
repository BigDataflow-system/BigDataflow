package alias_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.StmtList;
import alias_stmt.*;

public class AliasStmts extends StmtList{

  public AliasStmts(){
    size = 1;
    stmts = new AStmt[1];
    stmts[0] = new EmptyAStmt();
  }

  public AliasStmts(Scanner sc) {
    size = 1;
    stmts = new AStmt[1];
    stmts[0] = AliasTool.newStmt(sc);
  }

  public StmtList getNew() {
    StmtList tmp = new AliasStmts();
    AStmt astmt = (AStmt)stmts[0];
    tmp.getStmts()[0] = astmt.decopy();
    return tmp;
  }

  public void setDeep(StmtList stmtlist) {
    AStmt astmt = (AStmt)stmtlist.getStmts()[0];
    this.stmts[0] = astmt.decopy();
  }
    
  @Override
  public void write(DataOutput dataOutput) throws IOException {
    AStmt astmt = (AStmt)stmts[0];
    switch (astmt.getStmt()) {
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
    astmt.write(dataOutput);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    AStmt astmt = new EmptyAStmt();
    switch (dataInput.readByte()) {
      case 1:
        astmt = new AssignAStmt();
        break;
      case 2:
        astmt = new LoadAStmt();
        break;
      case 3:
        astmt = new StoreAStmt();
        break;
      case 4:
        astmt = new AllocAStmt();
        break;
      case 5:
        astmt = new PhiAStmt();
              break;
      case 6:
        astmt = new CallAStmt();
        break;
      case 7:
        astmt = new ReturnAStmt();
        break;
      case 8:
        astmt = new RetAStmt();
        break;
      case 9:
        astmt = new SkipAStmt();
        break;
      case 10:
        astmt = new CallfptrAStmt();
        break;
      case 11:
        astmt = new CalleefptrAStmt();
        break;
      case 12:
        astmt = new EmptyAStmt();
        break;
      default:
        System.out.println("read wrong stmt type");
      }
      astmt.readFields(dataInput);
      stmts[0] = astmt;
  }
}