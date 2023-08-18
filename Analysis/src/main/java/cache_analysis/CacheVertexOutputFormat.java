package cache_analysis;

import cache_data.CacheTool;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.*;
import org.apache.hadoop.conf.Configuration;

import data.Fact;
import data.Tool;
import cache_data.CacheState;
import cache_data.CacheVertexValue;

public class CacheVertexOutputFormat extends TextVertexOutputFormat<IntWritable, CacheVertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    
    
    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, CacheVertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            Fact fact = vertex.getValue().getFact();
            stringBuilder.append("id: ").append(vertex.getId()).append(" State: ");
            if (fact != null) {
                Tool tool = new CacheTool();
                fact = tool.transfer(vertex.getValue().getStmtList(), fact);
                stringBuilder.append((CacheState)fact);
            }
            else{
                stringBuilder.append("0");
            }
            // String s = stringBuilder.toString()+"\n";
            // appendTerms(s);
            return new Text(stringBuilder.toString());
        }
    }
}
