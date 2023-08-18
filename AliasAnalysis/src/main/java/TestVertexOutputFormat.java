import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.Set;

public class TestVertexOutputFormat extends TextVertexOutputFormat<IntWritable,VertexValue, NullWritable>
{
    private static final String VALUE_TOKEN_SEPARATOR = " ";

    /**
     * {@inheritDoc}
     */
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        /**
         * {@inheritDoc}
         */
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable,VertexValue,NullWritable> vertex)throws IOException
        {
            // vertex id
            StringBuilder sb = new StringBuilder(vertex.getId().toString());
            sb.append(VALUE_TOKEN_SEPARATOR);
            sb.append(vertex.getValue().getData().toString());
            sb.append(VALUE_TOKEN_SEPARATOR);
            sb.append("pre: ");
            sb.append(VALUE_TOKEN_SEPARATOR);

            Set<Integer> pre = vertex.getValue().getPre();
            for (Integer i : pre) {
                sb.append(i);
                sb.append(VALUE_TOKEN_SEPARATOR);
            }

//            IntWritable[] k = vertex.getValue().getPre().getMyValue();
//            for (int i = 0; i < k.length; i ++)  {
//                sb.append(k[i].get());
//                sb.append(VALUE_TOKEN_SEPARATOR);
//            }
//
////			CacheWritable inSet = vertex.getValue();
//            EdgeArray outSet = vertex.getValue();
//            ArrayList<Integer> vertex_edges = outSet.getEdges();
//            ArrayList<Character> vertex_labels = outSet.getLabels();
///*			for(Edge<LongWritable,CustomWritable> edge : vertex.getEdges())
//			{
//				outSet = edge.getValue();
//			}*/
//            sb.append(VALUE_TOKEN_SEPARATOR);
//            for(int i = 0; i < vertex_edges.size(); i ++)
//            {
//                sb.append(vertex_edges.get(i).toString());
//                sb.append(VALUE_TOKEN_SEPARATOR);
//            }
//            for(int i = 0; i < vertex_edges.size(); i ++)
//            {
//                sb.append(vertex_labels.get(i).toString());
//                sb.append(VALUE_TOKEN_SEPARATOR);
//            }
            return new Text(sb.toString());
        }
    }
}
