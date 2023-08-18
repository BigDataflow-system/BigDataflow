package data;

import java.lang.*;
import java.io.*;
import org.apache.hadoop.io.Writable;

public abstract class Fact implements Writable{
  public abstract void merge(Fact fact);
  public abstract Fact getNew();
  public abstract boolean consistent(Fact fact);

  @Override
  public void write(DataOutput out) throws IOException{

  }

  @Override
  public void readFields(DataInput in) throws IOException{

  }

}