package alias_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.regex.Pattern;

import alias_data.AliasVertexValue;

public class AliasVertexInputFormat extends TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable>.TextVertexReader createVertexReader(InputSplit split,TaskAttemptContext context) throws IOException
    {
        return new AliasVertexReader();
    }

    public class AliasVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
    {
        @Override
        protected String[] preprocessLine(Text line) {
            String[] tokens = SEPARATOR.split(line.toString());
            return tokens;
        }

        @Override
        protected IntWritable getId(String[] tokens) {
            int id = Integer.parseInt(tokens[0]);
            return new IntWritable(id);
        }

        @Override
        protected AliasVertexValue getValue(String[] tokens) {
            StringBuilder stmt = new StringBuilder();
            for(int i = 1; i < tokens.length - 1; i++)
            {
                stmt.append(tokens[i]);
                stmt.append('\t');
            }
            stmt.append(tokens[tokens.length - 1]);
            return new AliasVertexValue(stmt.toString());
        }

        @Override
        protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
        {
            return ImmutableList.of();
        }
    }
}
