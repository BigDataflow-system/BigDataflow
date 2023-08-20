package cache_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import data.Fact;

public class CacheState extends Fact {
  public Map[] IRs_Icache;

  public CacheState(){
    IRs_Icache = new Map[128];
    for (int i = 0; i < 128; i++) {
      IRs_Icache[i] = new HashMap<Integer, Integer>();
    }
  }

  public Map[] getIRsCache(){
    return IRs_Icache;
  }

  public byte AccessIC(Integer ir) {
    byte flag = -1;
    int setNum = getSetNum(ir);
    int beginLine = setNum * 4;

    HashMap<Integer, Integer> IRs_Set = (HashMap<Integer, Integer>) this.IRs_Icache[setNum];

    if (IRs_Set.containsKey(ir)) {//case1:  in the cache set, so is hit
        flag = 1;
        int CacheLoc = IRs_Set.get(ir);
        int age = CacheLoc % 128;

        for (Map.Entry<Integer, Integer> item : IRs_Set.entrySet()) {
            if (item.getKey().equals(ir)) {
                item.setValue(beginLine);
            }
            else if (item.getValue() < CacheLoc) { //others variable age is inc
                IRs_Set.put(item.getKey(), item.getValue() + 1);
            }
        }
        IRs_Set.entrySet().removeIf(tmp -> tmp.getValue() > beginLine + 3);
    }

    else { //case2: var not in cache set
        flag = 0;

        IRs_Set.put(ir, beginLine);
        for (Map.Entry<Integer, Integer> item : IRs_Set.entrySet()) {
            if (!item.getKey().equals(ir)) {
                item.setValue(item.getValue() + 1);
            }
        }
        IRs_Set.entrySet().removeIf(tmp -> tmp.getValue() > beginLine + 3);
    }
    return flag;	// return 1, if var in cache, else 0. use return value to count cache hits/miss
  }

  

  private int getSetNum(Integer addr) {
    int sum = addr / 64;
    sum = sum % 128;
    return sum;
  }

  @Override
  public void merge(Fact fact){
   
    if (fact == null) return;
    CacheState tmp_state = (CacheState)fact; 

    for (int j = 0; j < 128; j++) {
      // HashMap<Integer, Integer> set1 = (HashMap<Integer, Integer>) tmp_fact.IRs_Icache[j];
      // HashMap<Integer, Integer> set2 = (HashMap<Integer, Integer>) this.IRs_Icache[j];
      HashMap<Integer, Integer> set1 = (HashMap<Integer, Integer>) tmp_state.IRs_Icache[j];
      HashMap<Integer, Integer> current_set = (HashMap<Integer, Integer>) this.IRs_Icache[j];

      // Iterator<Map.Entry<Integer, Integer>> it = set2.entrySet().iterator();
      Iterator<Map.Entry<Integer, Integer>> it = set1.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<Integer, Integer> tmp = it.next();
        int val = tmp.getKey();
        if (current_set.containsKey(val)) {
          int line2 = tmp.getValue();
          int line1 = current_set.get(val);
          int minLine = Math.min(line2, line1);
          current_set.put(tmp.getKey(), minLine);
        } else { // cannot find in this, add it for lower
          current_set.put(tmp.getKey(), tmp.getValue());
        }
      }
    }

  }

  @Override
  public Fact getNew(){
    CacheState state = new CacheState();
    for (int i = 0; i < 128; i++) {
      state.IRs_Icache[i].putAll((HashMap<Integer, Integer>) this.IRs_Icache[i]);
    }
    return state;
  }
  
  @Override
  public boolean consistent(Fact oldFact){
    CacheState oldState = (CacheState)oldFact; 
    for (int k = 0; k < 128; k++) {
      if (this.IRs_Icache[k].size() != oldState.IRs_Icache[k].size()) {
        return false;
      }

      for (Map.Entry<Integer, Integer> item : ((HashMap<Integer, Integer>)this.IRs_Icache[k]).entrySet()) {
        if (!oldState.IRs_Icache[k].containsKey(item.getKey())) return false;
        if (!item.getValue().equals(oldState.IRs_Icache[k].get(item.getKey()))) return false;
      }
    }
    return true;
  }

  @Override
  public void write(DataOutput out) throws IOException{
    if(IRs_Icache != null){
      out.writeBoolean(true);
      for (int i = 0; i < 128; i++) {
        HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) IRs_Icache[i];
        out.writeInt(hashMap.size());
        for (Map.Entry<Integer, Integer> tmp : hashMap.entrySet()) {
          out.writeInt(tmp.getKey());
          out.writeInt(tmp.getValue());
        }
      }
    }
    else{
      out.writeBoolean(false);
    }
  }

  @Override
  public void readFields(DataInput in) throws IOException{
    if(in.readBoolean()){
      for (int i = 0; i < 128; i++) {
          HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) IRs_Icache[i];
          hashMap.clear();
          int size = in.readInt();
          while (size > 0) {
              hashMap.put(in.readInt(), in.readInt());
              --size;
          }
      }
    }
  }

  public void print() {
    if(IRs_Icache == null){
      return ;
    }
    for (int i = 0; i < 128; i++) {
      HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) IRs_Icache[i];
      if (hashMap.size() != 0) {
        System.out.println("vector index: " + i + " size: " + hashMap.size() + " ");
        for (Map.Entry<Integer, Integer> tmp : hashMap.entrySet()) {
          System.out.println(tmp.getKey() + " : " + tmp.getValue());
        }
      }
    }
  }

  public int size() {
    int size = 0;
    for (int i = 0; i < 128; i++) {
        size += IRs_Icache[i].size();
    }
    return size;
  }

  public String toString() {
    return String.valueOf(this.size());
  }
}