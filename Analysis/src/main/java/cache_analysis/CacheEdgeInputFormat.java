package cache_analysis;

import org.apache.giraph.io.EdgeReader;
import org.apache.giraph.io.formats.TextEdgeInputFormat;
import org.apache.giraph.utils.IntPair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.regex.Pattern;

public class CacheEdgeInputFormat extends TextEdgeInputFormat<IntWritable, NullWritable>
{
    /** Splitter for endpoints */
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public EdgeReader<IntWritable, NullWritable> createEdgeReader
            (InputSplit split, TaskAttemptContext context) {
        return new IntNullTextEdgeReader();
    }

    public class IntNullTextEdgeReader extends TextEdgeReaderFromEachLineProcessed<IntPair>
    {
        @Override
        protected IntPair preprocessLine(Text line) {
            String[] tokens = SEPARATOR.split(line.toString());
            return new IntPair(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]));
        }

        @Override
        protected IntWritable getSourceVertexId(IntPair endpoints) {
            return new IntWritable(endpoints.getFirst());
        }

        @Override
        protected IntWritable getTargetVertexId(IntPair endpoints) {
            return new IntWritable(endpoints.getSecond());
        }

        @Override
        protected NullWritable getValue(IntPair endpoints) throws IOException
        {
            return NullWritable.get();
        }
    }
}