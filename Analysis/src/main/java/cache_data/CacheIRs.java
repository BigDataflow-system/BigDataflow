package cache_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.Stmt;
import data.StmtList;

public class CacheIRs extends StmtList{

  public CacheIRs(){
    super();
  }

  public CacheIRs(Scanner sc) {
    List<Stmt> newStmts = new ArrayList<>();
    
    while (sc.hasNext()) {
      String str = sc.next();
      IRStmt irstmt =  new IRStmt();
      irstmt.setStmt(Integer.decode(str));
      newStmts.add(irstmt);
    }
    setStmts(newStmts);
  }

  public void printIRs(){
    for (int k = 0; k < size; k++){
      IRStmt irstmt = (IRStmt)stmts[k];
      System.out.print(irstmt.getStmt()+" ");
    }
    System.out.print("\n");
  }

  public void write(DataOutput out) throws IOException{
     out.writeInt(size);
     for (int k = 0; k < size; k++){
        IRStmt irstmt = (IRStmt)stmts[k];
        irstmt.write(out);
     }
  }

  public void readFields(DataInput in) throws IOException{
    clear();
    size = in.readInt();
    stmts = new Stmt[size];
    
    int k = 0;
    while (k < size) {
      IRStmt irstmt =  new IRStmt();
      irstmt.readFields(in);
      stmts[k] = irstmt;
      k++;
    }
  }
}