package analysis;

import org.apache.giraph.master.MasterCompute;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class MasterBroadcast extends MasterCompute
{
    public InputStreamReader readHDFS(String path) throws IOException
    {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(path), conf);
        FSDataInputStream hdfsInStream = fs.open(new Path(path));
        return new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
    }

    public void readEntrys(String entryPath, SetWritable entrys)
    {
        try {
            BufferedReader br = new BufferedReader(readHDFS(entryPath));
            String s;
            while((s = br.readLine())!=null)
            {
                entrys.addEntry(Integer.parseInt(s));
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void compute()
    {
        // MasterCompute body
        if(getSuperstep() == 0)
        {
//            File file = new File("/home/decxu/Downloads/hadoop-2.5.1/share/hadoop/common/entry.txt");
//            SetWritable entrys = new SetWritable();
//            readEntrysLocal(file, entrys);

            SetWritable entrys = new SetWritable();
            try {
                // BufferedReader start = new BufferedReader(readHDFS("hdfs://localhost:8000/analysis/start"));
                // BufferedReader start = new BufferedReader(readHDFS("hdfs://emr-header-1.cluster-273716:9000/analysis/start"));
                // BufferedReader start = new BufferedReader(readHDFS("hdfs://emr-header-1.cluster-332125:9000/analysis/start"));
                BufferedReader start = new BufferedReader(readHDFS("hdfs://emr-header-1.cluster-334120:9000/analysis/start"));
                String entryPath = start.readLine();
                start.close();
                readEntrys(entryPath, entrys);
            } catch (IOException e) {
                e.printStackTrace();
            }
            broadcast("entry", entrys);
        }
    }

    private void readEntrysLocal(File file, SetWritable entrys) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while((s = br.readLine())!=null)
            {
                entrys.addEntry(Integer.parseInt(s));
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFields(DataInput arg0) throws IOException
    {
        // To deserialize this class fields (global variables) if any
    }

    public void write(DataOutput arg0) throws IOException
    {
        // To serialize this class fields (global variables) if any
    }
}

