package analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import org.apache.hadoop.fs.Path;
import org.apache.giraph.worker.WorkerContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AliasVertexOutputFormat extends TextVertexOutputFormat<IntWritable, VertexValue, NullWritable> {

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, VertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            Pegraph pegraph = vertex.getValue().getPegraph();
            stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");

            if(pegraph != null){
                int sum = pegraph.getNumEdges();
                stringBuilder.append(sum);
            }
            else{
                stringBuilder.append("0");
            }
            return new Text(stringBuilder.toString());
        }
    }
}
