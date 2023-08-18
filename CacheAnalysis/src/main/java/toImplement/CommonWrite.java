package toImplement;

import java.io.*;
import java.lang.*;

public class CommonWrite{
  public  static String file = "/home/szw/wen/hadoop-2.7.2/bin/aug_test/cache_pull_serial.txt";
  
  public static void method2(String conent) {
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
      out.write(conent+"\r\n");
    } 
    catch (Exception e) {
      e.printStackTrace();
    } 
    finally {
      try {
        out.close();
      } 
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
