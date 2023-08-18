package alias_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import data.*;

public class Pegraph extends Fact{

  protected final Map<Integer, EdgeArray> graph;
  
  public Pegraph(){
    this.graph = new HashMap<>();
  }

  public Map<Integer, EdgeArray> getGraph() {
    return graph;
  }

  public int getNumEdges(int src) {
      if (!graph.containsKey(src)) {
          graph.put(src, new EdgeArray());
          return 0;
      }
      return graph.get(src).getSize();
  }

  public int getNumEdges() {
      int size = 0;
      for(EdgeArray edgeArray : graph.values()) {
          size += edgeArray.getSize();
      }
      return size;
  }

  public int[] getEdges(int src) {
      return graph.get(src).getEdges();
  }

  public byte[] getLabels(int src) {
      return graph.get(src).getLabels();
  }

  public void setEdgeArray(int index, int numEdges, int[] edges, byte[] labels) {
      EdgeArray tmp = new EdgeArray();
      tmp.set(numEdges, edges, labels);
      graph.put(index, tmp);
  }

  public void setEdgeArray(int index, EdgeArray array) {
      this.graph.put(index, array);
  }

  public void setDeep(Pegraph pegraph) {
    for (Map.Entry<Integer, EdgeArray> entry : pegraph.graph.entrySet()) {
        Integer oldId = entry.getKey();
        EdgeArray oldEdgeArray = entry.getValue();
        EdgeArray edgeArray = new EdgeArray();
        edgeArray.set(oldEdgeArray.getSize(), oldEdgeArray.getEdges(), oldEdgeArray.getLabels());
        this.graph.put(oldId, edgeArray);
    }
  }

  @Override
  public void merge(Fact fact){
    Pegraph mergeGraph = (Pegraph)fact;

    for (Map.Entry<Integer, EdgeArray> entry : mergeGraph.getGraph().entrySet()) {
      Integer mergeId = entry.getKey();
      EdgeArray mergeEdgeArray = entry.getValue();
      if (!this.graph.containsKey(mergeId)) {
          this.graph.put(mergeId, mergeEdgeArray);
      }
      else {
          // merge the edgeArray with the same src in graph_1 and graph_2
          int n1 = mergeEdgeArray.getSize();
          int n2 = this.getNumEdges(mergeId);
          int[] edges = new int[n1 + n2];
          byte[] labels = new byte[n1 + n2];
          int len = AliasTool.unionTwoArray(edges, labels, n1,
                  mergeEdgeArray.getEdges(), mergeEdgeArray.getLabels(), n2,
                  this.getEdges(mergeId),
                  this.getLabels(mergeId));

          this.graph.get(mergeId).set(len, edges, labels);
      }
    }
  }

  @Override
  public Fact getNew(){
    Pegraph tmp = new Pegraph();
    tmp.setDeep(this);
    return tmp;
  }

  @Override
  public boolean consistent(Fact fact){

    if(fact == null)  return false;

    Pegraph another = (Pegraph)fact;
    if(this == another){
      return true;
    }

    if(this.graph.size() != another.graph.size()) {
        return false;
    }

    for (Map.Entry<Integer, EdgeArray> entry : graph.entrySet()) {
        Integer id = entry.getKey();
        if (!another.graph.containsKey(id)) {
            return false;
        } else if (!another.graph.get(id).equals(this.graph.get(id))) {
            return false;
        }
    }
    return true;
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    int size = graph.size();
    dataOutput.writeInt(size);
    for (Map.Entry<Integer, EdgeArray> entry : graph.entrySet()) {
      dataOutput.writeInt(entry.getKey());
      entry.getValue().write(dataOutput);
    }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    this.graph.clear();
    int size = dataInput.readInt();
    for (int i = 0; i < size; i++) {
      Integer key = dataInput.readInt();
      EdgeArray edgeArray = new EdgeArray();
      edgeArray.readFields(dataInput);
      graph.put(key, edgeArray);
    }
  }

  public String toString(){
    return String.valueOf(getNumEdges());
  }
}