import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import com.google.common.collect.Lists;

public class TestVertexIntputFormat extends TextVertexInputFormat<IntWritable,VertexValue, NullWritable>
{
    /**
     * Separator of the vertex and neighbors
     */
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    /**
     * {@inheritDoc}
     *
     */

    @Override
    public TextVertexReader createVertexReader(InputSplit split,TaskAttemptContext context) throws IOException
    {
        return new VertexReader();
    }

    /**
     * Reads a vertex with two values from an input line.
     */
    public class VertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
    {
        /**
         * Vertex id for the current line.
         */
        private int id;
        /**
         * {@inheritDoc}
         */
        @Override
        protected String[] preprocessLine(Text line) throws IOException
        {
            String[] tokens = SEPARATOR.split(line.toString());
            id = Integer.parseInt(tokens[0]);
            return tokens;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected IntWritable getId(String[] tokens) throws IOException
        {
            return new IntWritable(id);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected VertexValue getValue(String[] tokens) throws IOException
        {
            int data = Integer.parseInt(tokens[2]);
            return new VertexValue(data);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
        {
            List<Edge<IntWritable, NullWritable>> edges = Lists.newArrayList();
//	      int[] kkk = new int[]{3,3,3};
            edges.add(EdgeFactory.create(new IntWritable(Integer.parseInt(tokens[1]))));
	    /*for (int n = 1; n < tokens.length; n++)
	      {
	    	if(tokens[n].equals("")) continue ;
	        edges.add(EdgeFactory.create(new LongWritable(Long.parseLong(tokens[n])),new CacheWritable()));
	      }*/
//	      if(edges.size()==0)
//	    	  edges.add(EdgeFactory.create(new LongWritable(id)));
            return edges;
        }
    }
}

