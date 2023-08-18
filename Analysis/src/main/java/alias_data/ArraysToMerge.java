package alias_data;

import java.util.*;

public class ArraysToMerge {
  private int[] edges;    // edges = new vertexid_t[capacity];
  private byte[] labels;        // labels = new char[capacity];
  private int size;            // size = total number of edges
  private int capacity;        // capacity = 2 * size

  private int[] index;    // calculate offset of each array. index = new vertexid_t[arrayCapacity];
  private int[] addr;    // store the firstAddr of each array. addr = new vertexid_t[arrayCapacity];
  private int arraySize;        // arraySize = number of arrays
  private int arrayCapacity;    // arrayCapacity = 2 * arraySize

  private int numEdges;        // exclude duplicate edges (numEdges <= size)
  private int[] resEdges;
  private byte[] resLabels;

  ArraysToMerge() {
      size = capacity = arraySize = arrayCapacity = numEdges = 0;
      resEdges = null; resLabels = null;
  }


  public void addOneContainer() {
      if(arraySize == 0) {
          arrayCapacity = 8;
          capacity = 8;
          edges = new int[capacity];
          labels = new byte[capacity];
          index = new int[arrayCapacity];
          addr = new int[arrayCapacity];
          for(int i = 0;i < capacity;++i) {
              edges[i] = -1;
              labels[i] = 127;
          }
          for(int i = 0;i < arrayCapacity;++i)
              index[i] = addr[i] = 0;
      }
      else {
          if(arraySize >= arrayCapacity) {
              arrayCapacity *= 2;
              index = AliasTool.myrealloc(index, arraySize, arrayCapacity);
              addr = AliasTool.myrealloc(addr, arraySize, arrayCapacity);
              for(int i = arraySize; i < arrayCapacity; ++i)
                  index[i] = addr[i] = 0;
          }
      }
      // add one empty array
      ++arraySize;
  }

  public void addOneEdge(Integer edge, byte label) {
      if(arraySize > 0) {
          if(size >= capacity) {
              capacity *= 2;
              edges = AliasTool.myrealloc(edges,size,capacity);
              labels = AliasTool.myrealloc(labels,size,capacity);
              for(int i = size;i < capacity;++i) {
                  edges[i] = -1;
                  labels[i] = 127;
              }
          }
          // add edge
          if(index[arraySize-1] == 0) {
              addr[arraySize-1] = size;
          }
          edges[size] = edge;
          labels[size] = label;
          ++index[arraySize-1];
          ++size;
      }
      else {
          System.out.println("add edge failed! ");
      }
  }

  public void merge() {
      mergeKArrays();
  }

  private void mergeKArrays() {
      if(size != 0) {
          int[] newEdges = new int[0];
          byte[] newLabels = new byte[0];
          // minHeap algorithm to merge k arrays
          if(arraySize > 1) {
              newEdges = new int[size];
              newLabels = new byte[size];
              // initial k-MinHeap
              MinHeapNode[] harr = new MinHeapNode[arraySize];
              for(int i = 0;i < arraySize;++i) {
                  harr[i] = new MinHeapNode();
              }

              for(int i = 0;i < arraySize;++i) {
                  harr[i].key_v = edges[addr[i]];
                  harr[i].key_c = labels[addr[i]];
                  harr[i].i = i;
                  harr[i].j = 1;
              }
              MinHeap hp = new MinHeap(harr,arraySize);
              for(int i = 0;i < arraySize;++i) {
                  for(int j = 0;j < index[i];++j) {
                      MinHeapNode root = hp.getMin();
                      newEdges[addr[i] + j] = root.key_v;
                      newLabels[addr[i] + j] = root.key_c;
                      if(root.j < index[root.i]) {
                          root.key_v = edges[addr[root.i] + root.j];
                          root.key_c = labels[addr[root.i] + root.j];
                          ++root.j;
                      }
                      else
                          root.key_v = Integer.MAX_VALUE;
                      hp.replaceMin(root);
                  }
              }
          }
          // remove duplicate edges
          int[] edge_v = new int[size];
          byte[] edge_l = new byte[size];
          int len = 0;
          if(arraySize > 1) {
              len = AliasTool.removeDuple(len,edge_v,edge_l,size,newEdges,newLabels);
          }
          else
              len = AliasTool.removeDuple(len,edge_v,edge_l,size,edges,labels);

          numEdges = len;
          System.arraycopy(edge_v, 0, edges, 0, len);
          System.arraycopy(edge_l, 0, labels, 0, len);
          resEdges = edges; resLabels = labels;
      }
  }

  public int getNumEdges() {
      return numEdges;
  }

  public void clear() {
      if(capacity > 0) {
          if(edges != null) {	edges = null;}
          if(resEdges != null) { resEdges = null;}
          if(labels!= null) {labels = null;}
          if(resLabels!= null) { resLabels = null;}
          capacity = size = 0;
      }
      if(arrayCapacity> 0) {
          if(index!= null) {index = null; }
          if(addr!= null) {addr = null; }
          arrayCapacity = arraySize;
      }
      numEdges = 0;
  }

  public int[] getEdgesFirstAddr() {
      return resEdges;
  }

  public byte[] getLabelsFirstAddr() {
      return resLabels;
  }
}
