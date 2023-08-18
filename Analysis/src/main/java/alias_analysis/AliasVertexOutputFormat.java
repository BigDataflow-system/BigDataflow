package alias_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import data.Fact;
import alias_data.AliasVertexValue;
import alias_data.Pegraph;

public class AliasVertexOutputFormat extends TextVertexOutputFormat<IntWritable, AliasVertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, AliasVertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            Fact fact = vertex.getValue().getFact();
            stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");
            int sum = 0;
            if (fact != null) {
                sum = ((Pegraph)fact).getNumEdges();
                stringBuilder.append(sum);
            }
            else{
                stringBuilder.append("0");
            }
            return new Text(stringBuilder.toString());
        }
    }
}
