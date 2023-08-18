package analysis;

import org.apache.giraph.master.MasterCompute;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import toImplement.Tools;

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
    public void compute() {
        // MasterCompute body
        if (getSuperstep() == 0)
        {
            // File file = new File(Tools.entry);
            // SetWritable entrys = new SetWritable();
            // readEntrysLocal(file, entrys);

            SetWritable entrys = new SetWritable();
            readEntrys(Tools.entry, entrys);
            broadcast("entry", entrys);
        }
    }

    private void readEntrysLocal(File file, SetWritable entrys) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine())!=null)
            {
                entrys.addEntry(Integer.parseInt(s));
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFields(DataInput arg0) {
        // To deserialize this class fields (global variables) if any
    }

    public void write(DataOutput arg0) {
        // To serialize this class fields (global variables) if any
    }
}

