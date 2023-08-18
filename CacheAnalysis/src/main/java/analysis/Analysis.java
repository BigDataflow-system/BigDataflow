package analysis;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import toImplement.State;
import toImplement.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Analysis extends BasicComputation<IntWritable, VertexValue, NullWritable, Msg> {
    @Override
    public void compute(Vertex<IntWritable, VertexValue, NullWritable> vertex, Iterable<Msg> messages) {
        // 第一轮迭代处理entry节点
        if (getSuperstep() == 0) {
            final SetWritable entry = getBroadcast("entry");
            if(entry.getValues().contains(vertex.getId().get()))
            {
                // get in set
                State state = new State();
                vertex.getValue().setState(state);
                // transfer
                State out_state = Tools.transfer(vertex.getValue().getContent(), state);
                // propagate
                for (Edge<IntWritable, NullWritable> edge : vertex.getEdges()) {
                    Msg msg = new Msg();
                    // 发出的id,方便后继节点记录信息
                    msg.setVertexID(vertex.getId());
                    // 发送1类消息
                    sendMessage(edge.getTargetVertexId(), msg);
                }
            }
            vertex.voteToHalt();
        }
        else {
            long type = getSuperstep() % 3;

            if (type == 1) {
                for (Msg message : messages) {
                    vertex.getValue().getPre().addValue(message.getVertexID());
                }
                Msg msg = new Msg();
                msg.setVertexID(vertex.getId());
                Set<Integer> pre = vertex.getValue().getPre().getValues();
                for (Integer i : pre) {
                    sendMessage(new IntWritable(i), msg);
                }
            }
            
            if (type == 2) {
                State out_state = Tools.transfer(vertex.getValue().getContent(), vertex.getValue().getState());
                for (Msg message : messages) {
                    Msg msg = new Msg();
                    if (vertex.getValue().getState() != null) {
                        msg.setState(out_state.getNew());
                    }
                    msg.setVertexID(vertex.getId());
                    sendMessage(message.getVertexID(), msg);
                }
            }

            if (type == 0) {
                // get in set
                State state;
                state = Tools.combine_synchronous(messages);
                // Transfer
                State out_old_state = null;
                // if(!vertex.getValue().getState().isOutInitial()){
                //     out_old_state = Tools.transfer(vertex.getValue().getContent(), vertex.getValue().getState());
                // }
                // else{
                //     out_old_state = vertex.getValue().getState();
                // }
                if(vertex.getValue().getState() == null){
                    out_old_state = null;
                }
                else{
                    out_old_state = Tools.transfer(vertex.getValue().getContent(), vertex.getValue().getState());
                }
                State out_new_state = Tools.transfer(vertex.getValue().getContent(), state);
                // propagate
                boolean canPropagate = Tools.propagate(out_old_state, out_new_state);
                if (canPropagate) {
                    // if(vertex.getValue().getState().isOutInitial()){
                    //     state.CloseInitial();
                    // }
                    vertex.getValue().setState(state); // state is in_state
                    Msg msg = new Msg();
                    msg.setVertexID(vertex.getId());
                    for(Edge<IntWritable,NullWritable> edge : vertex.getEdges())
                    {
                        sendMessage(edge.getTargetVertexId(), msg);
                    }
                }
            }
            vertex.voteToHalt();
        }
    }
}
