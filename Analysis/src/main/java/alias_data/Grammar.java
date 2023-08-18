package alias_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Grammar {
    private byte[][] rawLabel = new byte[256][36];	// rawLabel[0]~rawLabel[255] -> (char)-128 ~ (char)127
    private int numRawLabels = 0;

    private byte[] erules = new byte[256];	// e-rule
    private int numErules = 0;		// number of e-rule
    private byte[] rules = new byte[65536];	// s-rule && d-rule
    // rules[0]~rules[65535] -> (short)-32768 ~ (short)32767

    public Grammar() {
        numRawLabels = 0;
        numErules = 0;
        for(int i = 0;i < 65536;++i)
            rules[i] = (byte) 127;
    }

    public int addRawLabel(byte[] label) {
        for (int i = 0;i < numRawLabels;++i) {
            if (Arrays.equals(rawLabel[i],label))
                return i;
        }
        rawLabel[numRawLabels++] = label.clone();
        return numRawLabels-1;
    }

    public byte getLabelValue(String str) {
        for(int i = 0; i < numRawLabels; i++) {
            if (isSame(rawLabel[i], str)) return (byte) (i-128);
        }
        return (byte) 127;
    }

    private boolean isSame(byte[] bytes, String str) {
        int num = bytes.length;
        if (num != str.length()) return false;
        for (int i = 0; i < num; i++) {
            if (bytes[i] != str.charAt(i)) return false;
        }
        return true;
    }

    public final byte[] getRawLabel(byte value) {return rawLabel[value+128];}

    public final int getNumErules() {return numErules;}

    public final byte getErule(int index) {return erules[index];}

    public final byte checkRules(byte edgeVal) {return rules[changeShort((byte)127,edgeVal) + 32768];} // find s-rule edges

    public final byte checkRules(byte srcVal, byte dstVal) {return rules[changeShort(srcVal,dstVal) + 32768];} // find d-rule edges;

    public final short changeShort(byte a,byte b) {return (short) ((short)a << 8 | ((short)b & 0xFF));}

    void test() {
        System.out.println("==========GRAMMER TEST START==========");
        System.out.println("rawLabels: ");
        for(int i = 0;i < numRawLabels;++i) {
            char[] tmp = new char[3];
            for (int j = 0; j < rawLabel[i].length; j++) {
                tmp[j] = (char)rawLabel[i][j];
            }
            System.out.println("(" + Arrays.toString(tmp) + ","  + (i-128) + ") ");
        }
        System.out.println("eRules :");
        for(int i = 0;i < numErules;++i)
            System.out.println((int)erules[i] + ",");

        System.out.println("s-rules and d-rules: ");
        for(int i = 0; i < 65536; i++) {
            if(rules[i] != (byte) 127)
            {
                short s = (short) (i - 32768);
                byte a = (byte) s; // s的低8位
                byte b = (byte) (s >> 8); // s的高8位
                if (b == (byte) 127)
                    System.out.println("s-rule: " + (int)rules[i] + ":= " + (int) a);
                else
                    System.out.println("d-rule: " + (int)rules[i] + ":= " + (int) b + "," + (int)a);
            }
        }
        System.out.println("==========GRAMMAR TEST END============");
    }

    public final boolean isMemoryAlias(byte label) {
        byte[] raw = this.getRawLabel(label);
        return raw.length == 1 && raw[0] == (byte)'M';
    }

    public final boolean isDereference(byte label) {
        byte[] raw = this.getRawLabel(label);
        return raw[0] == (byte)'d';
    }

    public final boolean isDereference_reverse(byte label) {
        byte[] raw = this.getRawLabel(label);
        return raw[0] == (byte)'-' && raw[1] == (byte)'d';
    }

    public final boolean isDereference_bidirect(byte label) {
        byte[] raw = this.getRawLabel(label);
        return (raw[0] == (byte)'d') || (raw[0] == (byte)'-' && raw[1] == (byte)'d');
    }

    public final boolean isValueAlias(byte label) {
        byte[] raw = this.getRawLabel(label);
        return raw[0] == (byte)'V';
    }

    public final boolean isPointsTo(byte label) {
        byte[] raw = this.getRawLabel(label);
        return raw[0] == (byte)'P' && raw[1] == (byte)'t';
    }

    public final boolean isEruleLabel(byte label) {
        for (int i = 0; i < getNumErules(); i++) {
            if (label == getErule(i)) {
                return true;
            }
        }
        return false;
    }

    public void loadGrammar(BufferedReader br) throws IOException {
        byte[][] arg = new byte[3][36];
        int[] index = new int[3];

        String str;
        while((str = br.readLine()) != null)
        {
            String[] array = str.split("\t");
            for (int i = 0; i < array.length; i++) {
                arg[i] = array[i].getBytes(StandardCharsets.UTF_8);
            }
            for (int i = 0; i < array.length; i++) {
                index[i] = addRawLabel(arg[i]);
            }

            switch (array.length) {
                case 1: // add e-rule
                    erules[numErules++] = (byte)(index[0]-128);
                    break;
                case 2: // add s-rule
                    short tmp = changeShort((byte)127, (byte)(index[1] - 128));
                    rules[tmp + 32768] = (byte)(index[0] - 128);
                    break;
                case 3: // add d-rule
                    short tmpp = changeShort((byte)(index[1] - 128), (byte)(index[2] - 128));
                    rules[tmpp + 32768] = (byte)(index[0] - 128);
                    break;
            }
        }
    }
}
