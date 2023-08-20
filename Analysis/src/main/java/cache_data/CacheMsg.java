package cache_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import cache_data.CacheState;
import data.Msg;

public class CacheMsg extends Msg{

  public CacheMsg(){
    fact = null;
  }


  @Override
  public void write(DataOutput dataOutput) throws IOException {
    vertexID.write(dataOutput);
    if (fact != null) {
      dataOutput.writeByte(1);
      fact.write(dataOutput);
    }
    else {
      dataOutput.writeByte(0);
    }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    vertexID.readFields(dataInput);
    if (dataInput.readByte() == 1) {
      if (fact == null) {
        fact = new CacheState();
      }
      fact.readFields(dataInput);
    }
  }
}