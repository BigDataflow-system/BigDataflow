package cache_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

import data.*;

public class CacheVertexValue extends VertexValue {

  public CacheVertexValue(){
    stmts = new CacheIRs();
    fact = null;
  }

  public CacheVertexValue(String text) {
    Scanner sc = new Scanner(text);
    stmts = new CacheIRs(sc);
    fact = null;
  }

  public CacheIRs getCacheIRs(){
    return (CacheIRs)stmts;
  }

  public CacheState getCacheState(){
    return (CacheState)fact;
  }

  public void write(DataOutput out) throws IOException {
    stmts.write(out);
    if (fact != null) {
      out.writeByte(1);
      fact.write(out);
    }
    else {
      out.writeByte(0);
    }
  }

  public void readFields(DataInput in) throws IOException {
    stmts.readFields(in);
    if (in.readByte() == 1) {
      if (fact == null) {
        fact = new CacheState();
      }
      fact.readFields(in);
    }
  }
}