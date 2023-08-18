package toImplement;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class State implements Writable {
    private Map[] IRs_Icache;
    private boolean out_initialized;

    public State() {
        IRs_Icache = new Map[128];
        for (int i = 0; i < 128; i++) {
            IRs_Icache[i] = new HashMap<Integer, Integer>();
        }
        out_initialized = false;
    }

    public State(boolean initialized) {
        IRs_Icache = null;
        out_initialized = true;
    }

    public boolean isOutInitial(){
        return out_initialized;
    }

    public void CloseInitial(){
        out_initialized = false;
    }

    public void print() {
        if(IRs_Icache == null){
            CommonWrite.method2("IRs_Icache is null");
            return ;
        }
        for (int i = 0; i < 128; i++) {
            HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) IRs_Icache[i];
            if (hashMap.size() != 0) {
                // System.out.println("vector index: " + i + " size: " + hashMap.size() + " ");
                CommonWrite.method2("\tset index: " + String.valueOf(i) + ", size: " + String.valueOf(hashMap.size() )+ ": ");
                for (Map.Entry<Integer, Integer> tmp : hashMap.entrySet()) {
                    // System.out.println(tmp.getKey() + " : " + tmp.getValue());
                    CommonWrite.method2("\t\tK: "+ String.valueOf(tmp.getKey()) + "-> V: " + String.valueOf(tmp.getValue()) + "");
                }
            }
        }
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //TODO: 序列化实现
        dataOutput.writeBoolean(out_initialized);
        if(IRs_Icache != null){
            dataOutput.writeBoolean(true);
            for (int i = 0; i < 128; i++) {
                HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) IRs_Icache[i];
                dataOutput.writeInt(hashMap.size());
                for (Map.Entry<Integer, Integer> tmp : hashMap.entrySet()) {
                    dataOutput.writeInt(tmp.getKey());
                    dataOutput.writeInt(tmp.getValue());
                }
            }
        }
        else{
            dataOutput.writeBoolean(false);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        //TODO: 反序列化实现
        out_initialized = dataInput.readBoolean();
        if(dataInput.readBoolean()){
            for (int i = 0; i < 128; i++) {
                HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) IRs_Icache[i];
                hashMap.clear();
                int size = dataInput.readInt();
                while (size > 0) {
                    hashMap.put(dataInput.readInt(), dataInput.readInt());
                    --size;
                }
            }
        }
    }

    public int isCacheEmpty() {
        if(IRs_Icache == null){
            return -1;
        }
        return size();
    }

    public State getNew() {
        //TODO: 返回节点属性的深拷贝
        State state = new State();
        for (int i = 0; i < 128; i++) {
            state.IRs_Icache[i].putAll((HashMap<Integer, Integer>) this.IRs_Icache[i]);
        }
        return state;
    }
   

    @Override
    public String toString() {
        return String.valueOf(this.size());
    }

    public int size() {
        //TODO: 打印节点属性
        int size = 0;
        for (int i = 0; i < 128; i++) {
            size += IRs_Icache[i].size();
        }
        return size;
    }
    

    public void merge(State state) {// if can merge,then this state cannot be null
        // pre_state is null, means it is an empty cache(Top), no need for meet
        if (state == null) {
            return; 
        }

        for (int j = 0; j < 128; j++) {
            HashMap<Integer, Integer> set1 = (HashMap<Integer, Integer>) state.IRs_Icache[j];
            HashMap<Integer, Integer> current_set = (HashMap<Integer, Integer>) this.IRs_Icache[j];

            // calculates based on this state
            Iterator<Map.Entry<Integer, Integer>> it = set1.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> tmp = it.next();
                int val = tmp.getKey();
                // if in both, get minimal age
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

    public byte AccessIC(Integer ir) {

        /*
         *	case1: flag 为-1，表示cache的非正常访问（初始状态）
         *	case2: flag 为1，IR在cache内，hit++，age变化
         *	case3: flag 为0，IR在cache外，miss++，map表与age都需变化
         */
        byte flag = -1;

        //在所在的组内进行查找
        int setNum = getSetNum(ir);
        int beginLine = setNum * 4;

        HashMap<Integer, Integer> IRs_Set = (HashMap<Integer, Integer>) this.IRs_Icache[setNum];

        if (IRs_Set.containsKey(ir)) {//case1: 在该set中，则为hit的情况


            flag = 1; //在cache中

            /*	需要更新，所在set的age信息，时间访问为常量	*/


            /*  根据组相联，hit时:
             *	1) 先找到所在组
             *	2) 自己的age更新为0
             *	3) 再找到所在组中，age小于其的行，行号++
             */

            int CacheLoc = IRs_Set.get(ir); //行号
            int age = CacheLoc % 128; //取模对应真实的age

            for (Map.Entry<Integer, Integer> item : IRs_Set.entrySet()) {
                if (item.getKey().equals(ir)) {
                    item.setValue(beginLine); //返回到该组的第一行
                }
                else if (item.getValue() < CacheLoc) { //组内在其前面个的行，变量都会后移一行
                    IRs_Set.put(item.getKey(), item.getValue() + 1);
                }
            }

            IRs_Set.entrySet().removeIf(tmp -> tmp.getValue() > beginLine + 3);
        }

        else { //case2: 访问该mem IR，但不在其对应的cache  set中
            flag = 0;
            //printf("Fatal: try to access IR, but not added to Mem-Cache map!\n\n");

            /*  根据组相联，miss时:
             *	1) 先找到所在组
             *	2) 构建映射关系，特别是对应到哪一行
             *	3) 剩余变量的行号++，大于组内行数则移出该组
             */

            //newInst->Line = beginLine;  //1) 初次进入组内时，需设置行号，且为该组第0行
            IRs_Set.put(ir, beginLine);
            /*
             *  1) 设置为组内第0行
             *  2) mem与cache的映射表发生更新
             */
            for (Map.Entry<Integer, Integer> item : IRs_Set.entrySet()) {
                if (!item.getKey().equals(ir)) {
                    item.setValue(item.getValue() + 1); //2) 旧变量的行号都得+1
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

    public boolean CacheConsistent(State oldState) {
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
}
