package alias_data;

import java.util.*;

public class ComputationSet {
    private Map<Integer, EdgeArray> Olds;
    private Map<Integer, EdgeArray> Deltas;
    private Map<Integer, EdgeArray> News;

    public ComputationSet() {
        Olds = new HashMap<>();
        Deltas = new HashMap<>();
        News = new HashMap<>();
    }
    public void init_add(Pegraph out, Map<Integer, EdgeArray> m, boolean isConservative) {
        //first get a fixed point over all the old edges
        if(isConservative) {
            // Deltas <- {m, out}
            for (Map.Entry<Integer, EdgeArray> entry : out.getGraph().entrySet()) {
                int id_old = entry.getKey();
                if (m.containsKey(id_old)) {
                    int n1 = out.getNumEdges(id_old);
                    int n2 = m.get(id_old).getSize();
                    int[] edges = new int[n1 + n2];
                    byte[] labels = new byte[n1 + n2];

                    int len = AliasTool.unionTwoArray(edges, labels, n1,
                            out.getEdges(id_old), out.getLabels(id_old), n2,
                            m.get(id_old).getEdges(), m.get(id_old).getLabels());

                    setDeltas(id_old, len, edges, labels);

                    m.remove(id_old);
                } else {
                    setDeltas(entry.getKey(), entry.getValue().getSize(), entry.getValue().getEdges(), entry.getValue().getLabels());
                }
            }

            Iterator<Map.Entry<Integer, EdgeArray>> itt = m.entrySet().iterator();
            while (itt.hasNext()) {
                Map.Entry<Integer, EdgeArray> entryy = itt.next();
                setDeltas(entryy.getKey(), entryy.getValue().getSize(), entryy.getValue().getEdges(), entryy.getValue().getLabels());
                itt.remove();
            }
        }
        else{
            System.out.println("isConservative is false!");
            System.exit(5);
        }
    }



    public Set<Integer> getVertices() {
        Set<Integer> vertexSet = new HashSet<>();
        for (Map.Entry<Integer, EdgeArray> entry : Olds.entrySet()) {
            vertexSet.add(entry.getKey());
        }
        for (Map.Entry<Integer, EdgeArray> entry : Deltas.entrySet()) {
            vertexSet.add(entry.getKey());
        }
        for (Map.Entry<Integer, EdgeArray> entry : News.entrySet()) {
            vertexSet.add(entry.getKey());
        }
        return vertexSet;
    }

    public boolean oldEmpty(int index) {
        return !Olds.containsKey(index);
    }
    public boolean deltaEmpty(int index) {
        return !Deltas.containsKey(index);
    }
    public boolean newEmpty(int index) {
        return !News.containsKey(index);
    }

    public Map<Integer, EdgeArray> getOlds() {
        return Olds;
    }
    public Map<Integer, EdgeArray> getDeltas() {
        return Deltas;
    }
    public Map<Integer, EdgeArray> getNews() {
        return News;
    }

    public int getDeltasNumEdges(int index) {
        return Deltas.get(index).getSize();
    }
    public int getOldsNumEdges(int index) {
        return Olds.get(index).getSize();
    }
    public int getNewsNumEdges(int index) {
        return News.get(index).getSize();
    }

    public int[] getDeltasEdges(int index) {
        return Deltas.get(index).getEdges();
    }

    public byte[] getDeltasLabels(int index) {
        return Deltas.get(index).getLabels();
    }

    public int[] getOldsEdges(int index) {
        return Olds.get(index).getEdges();
    }

    public byte[] getOldsLabels(int index) {
        return Olds.get(index).getLabels();
    }

    public int[] getNewsEdges(int index) {
        return News.get(index).getEdges();
    }

    public byte[] getNewsLabels(int index) {
        return News.get(index).getLabels();
    }

    public void setNews(int index, int numEdges, int[] edges, byte[] labels) {
        if(!News.containsKey(index))
            News.put(index, new EdgeArray());
        News.get(index).set(numEdges,edges,labels);
    }

    public void setOlds(int index, int numEdges, int[] edges, byte[] labels) {
        if(!Olds.containsKey(index)) {
            Olds.put(index, new EdgeArray());
        }
        Olds.get(index).set(numEdges,edges,labels);
    }

    public void setDeltas(int index, int numEdges, int[] edges, byte[] labels) {
        if (!Deltas.containsKey(index)) {
            Deltas.put(index, new EdgeArray());
        }
        Deltas.get(index).set(numEdges, edges, labels);
    }


    public long getDeltasTotalNumEdges() {
        long num = 0;
        for (Map.Entry<Integer, EdgeArray> entry : Deltas.entrySet()) {
            num += getDeltasNumEdges(entry.getKey());
        }
        return num;
    }

    public void print() {
        System.out.println("\nComputationSet<<<<\n---------------------");
        System.out.println("Olds:\n");
        print_graph_map(Olds);
        System.out.println("Deltas:\n");
        print_graph_map(Deltas);
        System.out.println("News:\n");
        print_graph_map(News);
        System.out.println("---------------------\n");
    }

    private void print_graph_map(Map<Integer, EdgeArray> graph) {
        int size = 0;
        for (Map.Entry<Integer, EdgeArray> entry : graph.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            entry.getValue().print();
            size += entry.getValue().getSize();
            System.out.println();
        }
        System.out.println("\n------------------\nsize= " + size);
    }
}
