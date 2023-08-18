package analysis;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import stmt.ReturnStmt;
import stmt.Stmt;
import stmt.TYPE;

import java.util.*;

/*
 * Vertex ID: IntWritable
 * Vertex value: VertexValue
 * Edge value: NullWritable
 * Message: MsgValue
 */

public class Alias_Analysis extends BasicComputation<IntWritable, VertexValue, NullWritable, MsgValue> {
    @Override
    public void compute(Vertex<IntWritable, VertexValue, NullWritable> vertex, Iterable<MsgValue> messages) {
        MyWorkerContext context = getWorkerContext();
        final Grammar grammar = context.grammar;
        final Singletons singletons = context.singletons;

        // 第一轮迭代处理entry节点
        if (getSuperstep() == 0) {
            final SetWritable entry = getBroadcast("entry");
            if(entry.getValues().contains(vertex.getId().get()))
            {
                // get in set
                Pegraph pegraph = new Pegraph();
                vertex.getValue().setPegraph(pegraph);
                // propagate
                for(Edge<IntWritable, NullWritable> edge : vertex.getEdges()) {
                    MsgValue msgvalue = new MsgValue();
                    msgvalue.setVertexID(vertex.getId());
                    msgvalue.setStmtWritable(vertex.getValue().getStmtWritable().getNew());
                    sendMessage(edge.getTargetVertexId(), msgvalue);
                }
            }
            vertex.voteToHalt();
        }
        else {
            // 首先确定当前收到的消息类型是 1 or 2 or 3
            long type = getSuperstep() % 3;

            // 收到1类消息,说明当前节点在此轮激活,同时需要记录有哪些前驱节点
            if (type == 1) {
                boolean beActive = false;
                Stmt stmt = vertex.getValue().getStmt();
                if(stmt.getType() == TYPE.Return){
                    if(vertex.getValue().getGraphStore() == null){
                        vertex.getValue().setGraphStore(new MapWritable());
                    }
                }
                for (MsgValue message : messages) {
                    // 对前继节点类型的特殊处理
                    Stmt preStmt = message.getNodeTuple().getStmt();
                    Stmt curStmt = vertex.getValue().getStmt();
                    if (!(curStmt.getType() == TYPE.Return && ((ReturnStmt)curStmt).getLength() == 0 &&
                            (preStmt.getType() == TYPE.Callfptr || preStmt.getType() == TYPE.Call))) {
                        beActive = true;
                    }
                    vertex.getValue().getPre().addValue(message.getVertexID());
                }
                if (beActive) {
                    // 发送2类消息,通知当前节点的前驱节点,当前节点需要前驱节点的节点属性,发出的id,方便前驱节点发回来
                    MsgValue msgvalue = new MsgValue();
                    msgvalue.setVertexID(vertex.getId());
                    msgvalue.setMsgToNull();
                    Set<Integer> pre = vertex.getValue().getPre().getValues();
                    for (Integer i : pre) {
                        sendMessage(new IntWritable(i), msgvalue);
                    }
                }
            }

            //收到2类消息,说明当前节点并没有激活.只是要发送给特定的后继节点自己的节点值.
            if (type == 2) {
                Pegraph out_pegraph = Tools.transfer(vertex.getValue().getPegraph(), vertex.getValue().getStmt(), grammar, singletons);
                for (MsgValue message : messages) {
                    MsgValue msgvalue = new MsgValue();
                    // 包含自身节点值
                    if (out_pegraph != null) {
                        msgvalue.setVertexValue(out_pegraph.getNew());
                    }
                    msgvalue.setVertexID(vertex.getId());
                    // 包含语句类型信息
                    msgvalue.setStmtWritable(vertex.getValue().getStmtWritable().getNew());
                    // 发送3类消息
                    sendMessage(message.getVertexID(), msgvalue);
                }
            }

            //收到3类消息,说明当前节点再次激活.并且的到了所有前驱节点的节点属性,要做transfer的逻辑
             if (type == 0) {
                // get in set
                Stmt stmt = vertex.getValue().getStmt();
                Pegraph pegraph;
                MapWritable oldGraphStore = vertex.getValue().getGraphStore(); // graphStore: MapWritable<IntWritable, NodeTuple>
                if(oldGraphStore != null){
                    for (MsgValue message : messages) {
                        oldGraphStore.put(new IntWritable(message.getVertexID().get()), message.getNodeTuple().getNew());
                    }
                }

                Map<Integer, NodeTuple> compute_graphStore = new HashMap<>();
                for (MsgValue message : messages) {
                    compute_graphStore.put(message.getVertexID().get(), message.getNodeTuple().getNew());
                }
                pegraph = Tools.combine_synchronous(stmt, grammar, compute_graphStore);
                // Transfer
                Pegraph out_old_peg = null;
                if(vertex.getValue().getPegraph() == null){
                    out_old_peg = null;
                }
                else{
                    out_old_peg = Tools.transfer(vertex.getValue().getPegraph(), stmt, grammar, singletons);
                }
                Pegraph out_new_peg = Tools.transfer(pegraph, stmt, grammar, singletons);

                if(!pegraph.equals(vertex.getValue().getPegraph())){
                    vertex.getValue().setPegraph(pegraph);
                }

                // propagate
                boolean canPropagate = Tools.propagate(out_old_peg, out_new_peg);
                if (canPropagate) {
                    // vertex.getValue().setPegraph(pegraph);
                    MsgValue msgvalue = new MsgValue();
                    msgvalue.setVertexID(vertex.getId());
                    msgvalue.setStmtWritable(vertex.getValue().getStmtWritable().getNew());
                    for(Edge<IntWritable,NullWritable> edge : vertex.getEdges())
                    {
                        sendMessage(edge.getTargetVertexId(), msgvalue);
                    }
                }
            }
            vertex.voteToHalt();
        }
    }
}