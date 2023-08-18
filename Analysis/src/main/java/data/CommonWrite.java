package data;

import java.io.*;
import java.lang.*;

public class CommonWrite{
  public  static String file = "/home/szw/wen/bigdata_apis/serial.txt";
  
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
