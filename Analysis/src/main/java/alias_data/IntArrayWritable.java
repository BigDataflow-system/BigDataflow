package alias_data;

import org.apache.giraph.utils.ArrayListWritable;
import org.apache.hadoop.io.IntWritable;

@SuppressWarnings("serial")
public class IntArrayWritable extends ArrayListWritable<IntWritable>
{
    /** Default constructor for reflection */
    public IntArrayWritable()
    {
        super();
    }

    /** Set storage type for this ArrayListWritable */
    @Override
    public void setClass()
    {
        setClass(IntWritable.class);
    }
}
