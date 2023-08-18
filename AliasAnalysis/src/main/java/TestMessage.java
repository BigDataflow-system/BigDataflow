import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;

import java.io.*;
import java.util.Set;

// msgType: 1 -> to be active; 2 -> send stat to succ; 3 -> do transfer
public class TestMessage extends BasicComputation<IntWritable, VertexValue,
        NullWritable, MsgValue> {
    @Override
    public void compute(Vertex<IntWritable, VertexValue, NullWritable> vertex, Iterable<MsgValue> messages) throws IOException {
        if (getSuperstep() == 16) {
            vertex.voteToHalt();
            return;
        }
        if(getSuperstep() == 0) {
            if (vertex.getId().get() == 1) {
                MsgValue msgvalue = new MsgValue();
                msgvalue.setVertexID(new IntWritable(vertex.getId().get()));
                msgvalue.setMsgType(new IntWritable(1)); // 生成1类消息

                for(Edge<IntWritable,NullWritable> edge : vertex.getEdges())
                {
                    sendMessage(edge.getTargetVertexId(),msgvalue);
                }
            }
            vertex.voteToHalt();
        }
        else {
            //首先确定当前收到的消息类型是1 or 2 or 3
            int type = 0;
            for (MsgValue message : messages) {
                type = message.getMsgType().get();
                break;
            }

            //收到1类消息,说明当前节点在此轮激活.需要记录前驱节点有谁,并要得到所有前驱节点的节点属性
            if (type == 1) {
                for (MsgValue message : messages) {
                    vertex.getValue().getPre().add(message.getVertexID().get());
                }

                MsgValue msgvalue = new MsgValue();
                msgvalue.setMsgType(new IntWritable(2)); //发送2类消息,通知当前节点的前驱节点,当前节点需要前驱节点的节点属性
                msgvalue.setVertexID(new IntWritable(vertex.getId().get())); // 发出的id,方便前驱节点发回来
                Set<Integer> pre = vertex.getValue().getPre();
                for (Integer i : pre) {
                    sendMessage(new IntWritable(i), msgvalue);
                }

                vertex.voteToHalt();
            }

            //收到2类消息,说明当前节点并没有激活.只是要发送给特定的后继节点自己的节点信息.
            if (type == 2 ) {
                for (MsgValue message : messages) {
                    MsgValue msgvalue = new MsgValue();
                    msgvalue.setVertexValue(vertex.getValue());
                    msgvalue.setMsgType(new IntWritable(3)); // 生成3类消息,包含自身节点属性
                    sendMessage(message.getVertexID(), msgvalue);
                }
                vertex.voteToHalt();
            }

            //收到3类消息,说明当前节点再次激活.并且的到了所有前驱节点的节点属性,要做transfer的逻辑
            if (type == 3) {

                // get in set
                int data = 0;
                for (MsgValue message : messages) {
                    data += message.getVertexValue().getData().get();
                }

                // transfer
                VertexValue vertexValue = vertex.getValue();
                vertexValue.setData(data);

                // propagate
                MsgValue msgvalue = new MsgValue();
                msgvalue.setVertexID(new IntWritable(vertex.getId().get()));
                msgvalue.setMsgType(new IntWritable(1)); // 生成1类消息,通知后继节点要做计算
                for(Edge<IntWritable,NullWritable> edge : vertex.getEdges())
                {
                    sendMessage(edge.getTargetVertexId(), msgvalue);
                }

                vertex.voteToHalt();
            }
        }
    }
}
