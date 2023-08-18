package analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import toImplement.State;
import toImplement.Tools;

public class DefaultVertexOutputFormat extends TextVertexOutputFormat<IntWritable, VertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, VertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            State state = vertex.getValue().getState();
            stringBuilder.append("id: ").append(vertex.getId()).append(" ,State: ");
            
            if(state != null){
                state = Tools.transfer(vertex.getValue().getContent(), state);
                stringBuilder.append(state.toString());
            }
            else{
                stringBuilder.append("0");
            }
            
            return new Text(stringBuilder.toString());
        }
    }
}
