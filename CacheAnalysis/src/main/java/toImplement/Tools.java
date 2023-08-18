package toImplement;

import java.util.List;
import java.util.Map;
import analysis.Msg;

public final class Tools {
    //TODO: 指定entry节点文件路径
    // public static String entry = "/home/szw/wen/hadoop-2.7.2/share/hadoop/common/entry";
    // public static String entry = "hdfs://localhost:8000/cache_entrys/entry";
    // public static String entry = "hdfs://emr-header-1.cluster-287054:9000/cache_entrys/entry";
    // public static String entry =  "hdfs://emr-header-1.cluster-353227:9000/cache_entrys/entry";
    public static String entry = "hdfs://localhost:8000/cache_entrys/entry";

    public static State combine_synchronous(Iterable<Msg> messages) {
        State state = null;
        for (Msg item : messages) {
            State tmp = item.getState();
            if(state == null) {
                state = tmp.getNew(); // first not Top value
            }
            else {
                state.merge(tmp);
            }
        }
        return state;
    }

    public static State transfer(Content content, State in_state) {
        State out_state;
        if(in_state == null){ // null representing empty cache
            out_state = new State();
        }
        else{
            out_state = in_state.getNew();
        }

        List<Integer> insts = content.getAllContent();
        for (Integer inst : insts) {
            visitInstruction(inst, out_state);
        }
        return out_state;
    }

    private static void visitInstruction(Integer I, State predModel) {
        byte hit_Icache = predModel.AccessIC(I);  //只凭地址进行访问
        if (hit_Icache == 0) {

        }
        else if (hit_Icache == 1) {

        }
        else {
            System.out.println("error IR, invalid visit cache !!!!");
            System.exit(1);
        }
    }

    public static boolean propagate(State oldState, State newState) {
        // if(oldState.isOutInitial()){
        //     if(newState.size() != 0){
        //         return true;
        //     }
        //     else{
        //         return false;
        //     }
        // }
        if(oldState == null){
            return true;
        }
        else{
            return !newState.CacheConsistent(oldState);
        }
    }
}
