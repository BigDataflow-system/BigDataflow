import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;

public class ShortestPathComputation extends BasicComputation<LongWritable, DoubleWritable,
        FloatWritable, DoubleWritable> {
    /**
     * 需要计算最短路径的源顶点
     */
    private static final int SOURCE_VERTEX = 0;
    /**
     * 表示节点不可达
     */
    private static final double UNREACHABLE = Double.MAX_VALUE;

    /**
     * @param vertex 待处理的顶点
     * @param messages vertex 接收到的来自其余顶点的 message
     */
    @Override
    public void compute(Vertex<LongWritable, DoubleWritable, FloatWritable> vertex,
                        Iterable<DoubleWritable> messages) throws IOException {
        if (getSuperstep() == 0) {
            //超步 0 时源顶点最短路径设置为 0，其余顶点设置为不可达，并且源顶点需要向其它顶点发送最短距离message
            if (vertex.getId().get() == SOURCE_VERTEX) {
                vertex.setValue(new DoubleWritable(0));
                sendDist(vertex);
            } else {
                vertex.setValue(new DoubleWritable(UNREACHABLE));
            }
        } else {
            //遍历处理从其余顶点收到的 message，
            // 查看 message 中传递的最短距离是否小于当前的最短距离，如果是则进行更新
            for (DoubleWritable message : messages) {
                if (message.get() < vertex.getValue().get()) {
                    vertex.setValue(message);
                    sendDist(vertex);
                }
            }
        }
        //主动将顶点置于不活跃状态，如果顶点收到 message，系统会将顶点再度激活
        vertex.voteToHalt();
    }

    /**
     * 发送顶点 vertex 到其邻接顶点的最短距离
     */
    private void sendDist(Vertex<LongWritable,
            DoubleWritable, FloatWritable> vertex) {
        for (Edge<LongWritable, FloatWritable> edge : vertex.getEdges()) {
            double distance = vertex.getValue().get() + edge.getValue().get();
            sendMessage(edge.getTargetVertexId(), new DoubleWritable(distance));
        }
    }
}