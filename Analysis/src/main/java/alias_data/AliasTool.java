package alias_data;

import java.util.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Writable;
import data.*;
import alias_stmt.*;
import alias_data.*;
import alias_analysis.MyWorkerContext;

public class AliasTool implements Tool<AliasMsg>{
  public static Grammar grammar;
  public static Singletons singletons;

  public AliasTool(MyWorkerContext context){
    grammar = context.grammar;
    singletons = context.singletons;
  }

  public static AStmt newStmt(Scanner sc) {
    AStmt stmt = null;
    String type = sc.next();
    if (type.equals("assign")) {
        stmt = new AssignAStmt(sc);
    }
    else if (type.equals("load")) {
        stmt = new LoadAStmt(sc);
    }
    else if (type.equals("store")) {
        stmt = new StoreAStmt(sc);
    }
    else if (type.equals("alloca")) {
        stmt = new AllocAStmt(sc);
    }
    else if (type.equals("phi")) {
        stmt = new PhiAStmt(sc);
    }
    else if (type.equals("call")) {
        stmt = new CallAStmt(sc);
    }
    else if (type.equals("return")) {
        stmt = new ReturnAStmt(sc);
    }
    else if (type.equals("ret")) {
        stmt = new RetAStmt();
    }
    else if (type.equals("block")) {
        stmt = new SkipAStmt();
    }
    else if(type.equals("callfptr")){
        stmt = new CallfptrAStmt(sc);
    }
    else if(type.equals("calleefptr")){
        stmt = new CalleefptrAStmt(sc);
    }
    else {
        System.out.println("wrong stmt type!!!");
        System.exit(2);
    }
    return stmt;
  }
  
  public Fact combine(Set<Fact> predFacts){
    return null;
  }

  public Fact combine(Iterable<AliasMsg> messages, VertexValue vertexValue){
    // Map<Integer, NodeTuple> graphStore = new HashMap<>();
    // for (AliasMsg message : messages) {
    //   NodeTuple nodeTuple = new NodeTuple();
    //   nodeTuple.setPegraph((Pegraph)message.getFact());
    //   nodeTuple.setStmtList(message.getStmtList());
    //   graphStore.put(message.getVertexID().get(), nodeTuple.getNew());
    // }

    // Pegraph out = null;
    // AliasStmts stmts = (AliasStmts)vertexValue.getStmtList();
    // AStmt stmt = (AStmt)stmts.getStmts()[0];
    // SetWritable pre = vertexValue.getMsgPreds();

    // if (graphStore.size() == 1) {
    //   AStmt preStmt = null;
    //   for (Map.Entry<Integer, NodeTuple> entry : graphStore.entrySet()) {
    //     preStmt = entry.getValue().getStmt();
    //     out = entry.getValue().getPegraph();
    //   }
    //   out = getPartial(stmt, preStmt, out, grammar, graphStore);
    //   if (out == null) {
    //     out = new Pegraph();
    //   }
    // }
    // else {
    //   out = new Pegraph();
    //   for (Integer id : pre.getValues()) {
    //     AStmt preStmt = graphStore.get(id).getStmt();
    //     Pegraph out_graph = graphStore.get(id).getPegraph();
    //     out_graph = getPartial(stmt, preStmt, out_graph, grammar, graphStore);
    //     if (out_graph == null) continue;
    //       out.merge(out_graph);
    //     }
    // }
    // TODO list: wait for implementation according to the mergeA
    // Pegraph out = null;
    // return out;

    MapWritable graphStore = null;
    AliasVertexValue aliasVertexValue = (AliasVertexValue)vertexValue;
    MapWritable oldGraphStore = aliasVertexValue.getGraphStore();
    if(oldGraphStore != null){
        graphStore = new MapWritable();
        for (Writable id : oldGraphStore.keySet()) {
            graphStore.put(id, ((NodeTuple)oldGraphStore.get(id)).getNew());
            // graphStore.put(id, ((NodeTuple)oldGraphStore.get(id)).getNew());
        }
    }

    AliasStmts stmts = (AliasStmts)vertexValue.getStmtList();
    AStmt stmt = (AStmt)stmts.getStmts()[0];
    if(stmt.getStmt() == TYPE.Return){
        Pegraph out = null;
        if (graphStore.size() == 1) {
            AStmt preStmt = null;
            for (Map.Entry<Writable, Writable> entry : graphStore.entrySet()) {
                preStmt = ((NodeTuple)(entry.getValue())).getStmt();
                out = ((NodeTuple)(entry.getValue())).getPegraph();
            }
            out = getPartial(stmt, preStmt, out, grammar, graphStore);
          if (out == null) {
            out = new Pegraph();
          }
        }
        else{
            out = new Pegraph();
            for (Map.Entry<Writable, Writable> entry : graphStore.entrySet()) {
                AStmt preStmt = ((NodeTuple)(entry.getValue())).getStmt();
                Pegraph out_graph = ((NodeTuple)(entry.getValue())).getPegraph();
                out_graph = getPartial(stmt, preStmt, out_graph, grammar, graphStore);
                if (out_graph == null) continue;
                    out.merge(out_graph);
            }
        }
        return out;
    }
    else{
        Pegraph new_peg;
        if(vertexValue.getFact() == null){ // old_peg is null
            new_peg = new Pegraph();
        }
        else{
            Pegraph old_peg = (Pegraph)(vertexValue.getFact());
            new_peg = (Pegraph)old_peg.getNew();
        }

        MapWritable new_graphStore = new MapWritable();
        for (AliasMsg message : messages){
            NodeTuple nodeTuple = new NodeTuple();
            nodeTuple.setPegraph((Pegraph)message.getFact());
            nodeTuple.setStmtList(message.getStmtList());
            new_graphStore.put(new IntWritable(message.getVertexID().get()), nodeTuple.getNew());
        }

        for (Map.Entry<Writable, Writable> entry : new_graphStore.entrySet()) {
            AStmt preStmt = ((NodeTuple)(entry.getValue())).getStmt();
            Pegraph out_graph = ((NodeTuple)(entry.getValue())).getPegraph();
            out_graph = getPartial(stmt, preStmt, out_graph, grammar, graphStore);
            if (out_graph == null) continue;
            new_peg.merge(out_graph);
        }
        return new_peg;
    }

    // if(stmt.getType() == TYPE.Return){
    //   Pegraph out = null;
    //     if (graphStore.size() == 1) {
    //       Stmt preStmt = null;
    //       for (Map.Entry<Writable, Writable> entry : graphStore.entrySet()) {
    //         preStmt = ((NodeTuple)(entry.getValue())).getStmt();
    //         out = ((NodeTuple)(entry.getValue())).getPegraph();
    //       }
    //       out = getPartial(stmt, preStmt, out, grammar, graphStore);
    //       if (out == null) {
    //         out = new Pegraph();
    //       }
    //     }
    //     else {
    //     	out = new Pegraph();
    //       for (Map.Entry<Writable, Writable> entry : graphStore.entrySet()) {
    //         Stmt preStmt = ((NodeTuple)(entry.getValue())).getStmt();
    //         Pegraph out_graph = ((NodeTuple)(entry.getValue())).getPegraph();
    //         out_graph = getPartial(stmt, preStmt, out_graph, grammar, graphStore);
    //         if (out_graph == null) continue;
    //           out.merge(out_graph);
    //       }
    //     }
    //   return out;
    // }

  }

  private static Pegraph getPartial(AStmt current, AStmt pred, Pegraph pred_graph, Grammar grammar, MapWritable graphStore) {
    Pegraph out;
    if (pred.getStmt() == TYPE.Callfptr) {
        CallfptrAStmt callfptrStmt = (CallfptrAStmt)pred;
        if (current.getStmt() == TYPE.Return) {
            out = pred_graph;
        }
        else { // other entry node in callee
            out = extractSubGraph(pred_graph, callfptrStmt.getArgs(), callfptrStmt.getLength(), callfptrStmt.getRet(), grammar);
        }
    }
    else if (pred.getStmt() == TYPE.Call) {
        CallAStmt callStmt = (CallAStmt)pred;
        if (current.getStmt() == TYPE.Return) {
            out = pred_graph;
        }
        else { // other entry node in callee
            out = extractSubGraph(pred_graph, callStmt.getArgs(), callStmt.getLength(), callStmt.getRet(), grammar);
        }
    }
    else if (current.getStmt() == TYPE.Return) {
        ReturnAStmt returnStmt = (ReturnAStmt)current;
        if(returnStmt.getLength() == 0) {
            out = new Pegraph();
        }
        else {
            out = extractSubGraph_exit(pred_graph, returnStmt.getArgs(), returnStmt.getLength(), returnStmt.getRet(), grammar, graphStore);
        }
    }
    else {
        out = pred_graph;
    }
    return out;
  }

  private static Pegraph extractSubGraph(Pegraph graph, int[] args, int len, int ret, Grammar grammar) {
    if(len == 0 && ret == -1){
        return new Pegraph();
    }
    Set<Integer> ids = new HashSet<>();
    collect_associated_variables(ids, args, len, ret, graph, grammar);

    /* keep edges associated with ids */
    Iterator<Map.Entry<Integer, EdgeArray>> it = graph.getGraph().entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Integer, EdgeArray> entry = it.next();
        if (entry.getValue().isEmpty()) {
            it.remove();
            continue;
        }
        int src = entry.getKey();
        if (!ids.contains(src)) {
            it.remove();
        }
    }
    return graph;
  }

  private static Pegraph extractSubGraph_exit(Pegraph graph, int[] args, int len, int ret, Grammar grammar, MapWritable graphStore) {
    if(len == 0 && ret == -1){
        return new Pegraph();
    }

    Set<Integer> ids = new HashSet<>();
    Pegraph pred_graph;
    for (Map.Entry<Writable, Writable> entry : graphStore.entrySet()) {
        /// AStmt stmt = entry.getValue().getStmt();
        AStmt stmt = ((NodeTuple)(entry.getValue())).getStmt();
        if (stmt.getStmt() == TYPE.Call || stmt.getStmt() == TYPE.Callfptr) {
            /// pred_graph = entry.getValue().getPegraph();
            pred_graph = ((NodeTuple)(entry.getValue())).getPegraph();
            collect_associated_variables(ids, args, len, ret, pred_graph, grammar);
            break;
        }
    }

    Pegraph fromExit = new Pegraph();
    /* extract edges associated with ids */
    for(Integer integer : ids){
        int id = integer;
        if(graph.getGraph().containsKey(id)) {
            EdgeArray edges = new EdgeArray();
            for(int i = 0; i < graph.getNumEdges(id); i++){
                int dst = graph.getEdges(id)[i];
                byte label = graph.getLabels(id)[i];
                if(ids.contains(dst)){
                    edges.addOneEdge(dst, label);
                }
            }

            if(edges.getSize() != 0){
                fromExit.setEdgeArray(id, edges);
            }
        }
    }
    return fromExit;
  }

  private static void collect_associated_variables(Set<Integer> ids, int[] args, int len, int ret, Pegraph graph, Grammar grammar) {
    LinkedList<Integer> worklist = new LinkedList<>();
    for (int i = 0; i < len; i++) {
        ids.add(args[i]);
        worklist.add(args[i]);
    }
    if (ret != -1) {
        ids.add(ret);
        worklist.add(ret);
    }
    while (!worklist.isEmpty()) {
        int id = worklist.removeLast();
        if (graph.getGraph().containsKey(id)) {
            for (int i = 0; i < graph.getNumEdges(id); i++) {
                int dst = graph.getEdges(id)[i];
                if(!ids.contains(dst)){
                    ids.add(dst);
                    worklist.add(dst);
                }
            }
        }
    }
  }
  
  public Fact transfer(StmtList stmtlist, Fact incomingFact){
    AliasStmts stmts = (AliasStmts)stmtlist;
    AStmt stmt = (AStmt)stmts.getStmts()[0];
    Pegraph pegraph = (Pegraph)incomingFact.getNew();

    switch (stmt.getStmt()) {
      case Assign:
          transfer_copy(pegraph, (AssignAStmt)stmt, grammar, singletons);
          break;
      case Load:
          transfer_load(pegraph, (LoadAStmt)stmt, grammar, singletons);
          break;
      case Store:
          transfer_store(pegraph, (StoreAStmt)stmt, grammar, singletons);
          break;
      case Alloca:
          transfer_address(pegraph, (AllocAStmt)stmt, grammar, singletons);
          break;
      case Phi:
          transfer_phi(pegraph, (PhiAStmt)stmt, grammar, singletons);
          break;
      case Call:
          transfer_call(pegraph, (CallAStmt)stmt, grammar, singletons);
          break;
      case Return:
          transfer_return(pegraph, (ReturnAStmt)stmt, grammar, singletons);
          break;
      case Ret:
          transfer_ret(pegraph, (RetAStmt)stmt, grammar, singletons);
          break;
      case Skip:
          transfer_skip(pegraph, (SkipAStmt)stmt, grammar, singletons);
          break;
      case Callfptr:
          transfer_callfptr(pegraph, (CallfptrAStmt)stmt, grammar, singletons);
          break;
      case Calleefptr:
          transfer_calleefptr(pegraph, (CalleefptrAStmt)stmt, grammar, singletons);
          break;
    }
    return pegraph;
  }

  private static void transfer_address(Pegraph pegraph, AllocAStmt stmt, Grammar grammar, Singletons singletons) {
    // the KILL set
    Set<Integer> vertices_changed = new HashSet<>();
    Set<Integer> vertices_affected = new HashSet<>();

    strong_update_simplify(stmt.getDst(), pegraph, vertices_changed, grammar, vertices_affected, singletons);

    // the GEN set
    peg_compute_add(pegraph, stmt, grammar);
  }

  private static void transfer_load(Pegraph pegraph, LoadAStmt stmt, Grammar grammar, Singletons singletons) {
      // the KILL set
      Set<Integer> vertices_changed = new HashSet<>();
      Set<Integer> vertices_affected = new HashSet<>();

      strong_update_simplify(stmt.getDst(), pegraph, vertices_changed, grammar, vertices_affected, singletons);

      // the GEN set
      peg_compute_add(pegraph, stmt, grammar);
  }

  private static void transfer_copy(Pegraph pegraph, AssignAStmt stmt, Grammar grammar, Singletons singletons) {
      // the KILL set
      Set<Integer> vertices_changed = new HashSet<>();
      Set<Integer> vertices_affected = new HashSet<>();

      strong_update_simplify(stmt.getDst(), pegraph, vertices_changed, grammar, vertices_affected, singletons);

      // the GEN set
      peg_compute_add(pegraph, stmt, grammar);
  }

  private static void transfer_phi(Pegraph pegraph, PhiAStmt stmt, Grammar grammar, Singletons singletons) {
      // the KILL set
      Set<Integer> vertices_changed = new HashSet<>();
      Set<Integer> vertices_affected = new HashSet<>();

      strong_update_simplify(stmt.getDst(), pegraph, vertices_changed, grammar, vertices_affected, singletons);
      // the GEN set
      peg_compute_add(pegraph, stmt, grammar);
  }

  private static void transfer_store(Pegraph out, StoreAStmt stmt, Grammar grammar, Singletons singletons) {
    // the KILL set
    Set<Integer> vertices_changed = new HashSet<>();
    Set<Integer> vertices_affected = new HashSet<>();

    if (out.getGraph().containsKey(stmt.getDst())) {
        if(is_strong_update_dst(stmt.getDst(), out, grammar, singletons)) {
            strong_update_store_dst_simplify(stmt.getDst(), out, vertices_changed, grammar, vertices_affected, singletons);
        }
    }
    else {
        if(is_strong_update_aux(stmt.getAuxiliary(), out, grammar, singletons)) {
            strong_update_store_aux_simplify(stmt.getAuxiliary(), stmt.getDst(), out, vertices_changed, grammar, vertices_affected, singletons);
        }
    }
    // the GEN set
    peg_compute_add(out, stmt, grammar);
  }

  private static void strong_update_simplify(int x, Pegraph out, Set<Integer> vertices_changed, Grammar grammar, Set<Integer> vertices_affected, Singletons singletons) {
    if (!out.getGraph().containsKey(x)) return;

    // vertices <- must_alias(x); put *x into this set as well
    must_alias(x, out, vertices_changed, grammar, vertices_affected, singletons);

    /* remove edges */
    Map<Integer, EdgeArray> mapWritable = out.getGraph();
    Iterator<Map.Entry<Integer, EdgeArray>> it = mapWritable.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Integer, EdgeArray> entry = it.next();
        if (entry.getValue().isEmpty()) {
            continue;
        }
        int src = entry.getKey();
        /* delete all the ('a', '-a', 'V', 'M', and other temp labels) edges associated with a vertex within vertices_changed, and
         * all the ('V', 'M', and other temp labels) edges associated with that within vertices_affected
         * */
        EdgeArray deletedArray = new EdgeArray();
        findDeletedEdges(entry.getValue(), src, vertices_changed, vertices_affected, grammar, deletedArray);

        if (deletedArray.getSize() != 0) {
            int n1 = out.getNumEdges(src);
            int n2 = deletedArray.getSize();
            int[] edges = new int[n1];
            byte[] labels = new byte[n1];
            int len = minusTwoArray(edges, labels, n1, out.getEdges(src), out.getLabels(src), n2, deletedArray.getEdges(), deletedArray.getLabels());
            if (len != 0) {
                out.setEdgeArray(src, len, edges, labels);
            }
            else {
                it.remove();
            }
        }
    }
  }

  private static void must_alias(int x, Pegraph out, Set<Integer> vertices_changed, Grammar grammar, Set<Integer> vertices_affected, Singletons singletons) {
    /* if there exists one and only one variable o,which
     * refers to a singleton memory location,such that x and
     * y are both memory aliases of o,then x and y are Must-alias
     */
    HashSet<Integer> set1 = new HashSet<>();
    set1.add(x);
    //compute all the must-alias expressions
    int numEdges = out.getNumEdges(x);
    int[] edges = out.getEdges(x);
    byte[] labels = out.getLabels(x);

    for (int i = 0; i < numEdges; ++i) {
        if (grammar.isMemoryAlias(labels[i])) {
            HashSet<Integer> set2 = new HashSet<>();

            int candidate = edges[i];
            int numEdgess = out.getNumEdges(candidate);
            int[] edgess = out.getEdges(candidate);
            byte[] labelss = out.getLabels(candidate);

            for (int k = 0; k < numEdgess; ++k) {
                if (grammar.isMemoryAlias(labelss[k]) && singletons.isSingleton(edgess[k])) {
                    set2.add(edgess[k]);
                }
            }

            if(set2.size() == 1 && set1.containsAll(set2)){
                vertices_changed.add(candidate);
            }
        }
    }

    vertices_changed.add(x);

    //add *x into vertices as well
    for (int xx : vertices_changed) {
        int numEdgess = out.getNumEdges(xx);
        int[] edgess = out.getEdges(xx);
        byte[] labelss = out.getLabels(xx);

        for (int i = 0; i < numEdgess; ++i) {
            if (grammar.isDereference(labelss[i])) {
                vertices_changed.add(edgess[i]);
            }
            if (grammar.isDereference_reverse(labelss[i])) {
                vertices_affected.add(edgess[i]);
            }
        }
    }
  }

  private static void findDeletedEdges(EdgeArray edgesToDelete, int src, Set<Integer> vertices_changed, Set<Integer> vertices_affected, Grammar grammar, EdgeArray deleted) {
    for (int i = 0; i < edgesToDelete.getSize(); ++i) {
        int dst = edgesToDelete.getEdges()[i];
        byte label = edgesToDelete.getLabels()[i];
        if(isDeletable(src, dst, label, vertices_changed, vertices_affected, grammar)) {
            deleted.addOneEdge(dst, label);
        }
    }
  }

  private static boolean isDeletable(int src, int dst, byte label, Set<Integer> vertices_changed, Set<Integer> vertices_affected, Grammar grammar) {
    //don't delete self-loop edges
    if(src == dst && grammar.isEruleLabel(label)) {
        return false;
    }

    //delete all the ('a', '-a', 'V', 'M', and other temp labels) edges associated with that within vertices_changed
    if(vertices_changed.contains(src) || vertices_changed.contains(dst)) {
        return !grammar.isDereference_bidirect(label);
    }

    return false;
  }

  private static int minusTwoArray(int[] dstA, byte[] dstB, int len1, int[] A1, byte[] B1, int len2, int[] A2, byte[] B2) {
    // (A1,B1),(A2,B2) is sorted
    int len = 0;
    if(len1 != 0) {
        if(len2 != 0) {
            int p1 = 0; int p2 = 0;
            while(p1 < len1 && p2 < len2) {
                long value = myCompare(A1[p1],B1[p1],A2[p2],B2[p2]);
                if(value > 0) {
                    ++p2;
                }
                else if(value < 0) {
                    dstA[len] = A1[p1]; dstB[len] = B1[p1];
                    ++p1; ++len;
                }
                else {
                    ++p1; ++p2;
                }
            }
            while(p1 < len1) {
                dstA[len] = A1[p1]; dstB[len] = B1[p1];
                ++p1; ++len;
            }
        }
        else {
            len = len1;
            System.arraycopy(A1, 0, dstA, 0, len);
            System.arraycopy(B1, 0, dstB, 0, len);
        }
    }

    return len;
  }

  private static void peg_compute_add(Pegraph out, AStmt stmt, Grammar grammar) {
    boolean isConservative = true;

    Map<Integer, EdgeArray> m = new HashMap<>();

    if(stmt.getStmt() == TYPE.Phi) {
        getDirectAddedEdges_phi(out, stmt, grammar, m);
    }
    else {
        getDirectAddedEdges(out, stmt, grammar, m);
    }

    // add assign edge based on stmt, (out,assign edge) -> compset
    ComputationSet compset = new ComputationSet();
    compset.init_add(out, m, isConservative);

    startCompute_add(compset, grammar);

    // GEN finished, compset -> out
    Map<Integer, EdgeArray> olds = compset.getOlds();
    for (Map.Entry<Integer, EdgeArray> entry : olds.entrySet()) {
        int id = entry.getKey();
        if (!out.getGraph().containsKey(id)) {
            out.setEdgeArray(id, entry.getValue().getSize(), entry.getValue().getEdges(), entry.getValue().getLabels());
        }
        else if (out.getNumEdges(id) != entry.getValue().getSize()) {
            out.setEdgeArray(id, entry.getValue().getSize(), entry.getValue().getEdges(), entry.getValue().getLabels());
        }
    }
  }

  private static void getDirectAddedEdges_phi(Pegraph out, AStmt stmt, Grammar grammar, Map<Integer, EdgeArray> m) {
    PhiAStmt stmt_phi = (PhiAStmt)stmt;
    //'a', '-a', 'd', '-d', and self-loop edges
    int length = stmt_phi.getLength();
    int[] srcs = stmt_phi.getSrcs();
    int dst = stmt_phi.getDst();
    EdgeArray edges_dst = new EdgeArray();

    for(int i = 0; i < length; i++){
        int src = srcs[i];
        EdgeArray edges_src = new EdgeArray();

        //'a', '-a'
        edges_src.addOneEdge(dst, grammar.getLabelValue("a"));
        edges_dst.addOneEdge(src, grammar.getLabelValue("-a"));

        //merge and sort
        edges_src.merge();

        //remove the existing edges
        removeExistingEdges(edges_src, src, out, m);
    }

    //self-loop edges
    for(int i = 0; i < grammar.getNumErules(); ++i){
        byte label = grammar.getErule(i);
        edges_dst.addOneEdge(dst, label);
    }

    edges_dst.merge();
    removeExistingEdges(edges_dst, dst, out, m);
  }

  private static void removeExistingEdges(EdgeArray edges_src, int src, Pegraph out, Map<Integer, EdgeArray> m) {
    //remove the existing edges
    int n1 = edges_src.getSize();
    int[] edges = new int[n1];
    byte[] labels = new byte[n1];
    int len = AliasTool.minusTwoArray(edges, labels, edges_src.getSize(),
            edges_src.getEdges(), edges_src.getLabels(), out.getNumEdges(src),
            out.getEdges(src), out.getLabels(src));
    if (len != 0) {
        m.put(src, new EdgeArray());
        m.get(src).set(len, edges, labels);
    }
  }

  private static void getDirectAddedEdges(Pegraph out, AStmt stmt, Grammar grammar, Map<Integer, EdgeArray> m) {
    TYPE t = stmt.getStmt();
    if(t == TYPE.Alloca){
        AllocAStmt stmt_alloc = (AllocAStmt)stmt;
        getDirectAddedEdges_alloc(out, stmt_alloc, grammar, m);
    }
    else if(t == TYPE.Store){
        StoreAStmt stmt_store = (StoreAStmt)stmt;
        getDirectAddedEdges_store(out, stmt_store, grammar, m);
    }
    else if(t == TYPE.Load){
        LoadAStmt stmt_load = (LoadAStmt)stmt;
        getDirectAddedEdges_load(out, stmt_load, grammar, m);
    }
    else if(t == TYPE.Assign){
        AssignAStmt stmt_assign = (AssignAStmt)stmt;
        getDirectAddedEdges_assign(out, stmt_assign, grammar, m);
    }
    else{
        System.out.println("wrong stmt type!!!");
        System.exit(5);
    }
  }

  private static void getDirectAddedEdges_alloc(Pegraph out, AllocAStmt stmt, Grammar grammar, Map<Integer, EdgeArray> m) {
    //'a', '-a', 'd', '-d', and self-loop edges
    int src = stmt.getSrc();
    EdgeArray edges_src = new EdgeArray();

    int dst = stmt.getDst();
    EdgeArray edges_dst = new EdgeArray();

    int aux = stmt.getAuxiliary();
    EdgeArray edges_aux = new EdgeArray();

    //'a', '-a'
    edges_src.addOneEdge(dst, grammar.getLabelValue("a"));
    edges_dst.addOneEdge(src, grammar.getLabelValue("-a"));

    //'d', '-d'
    edges_src.addOneEdge(aux, grammar.getLabelValue("d"));
    edges_aux.addOneEdge(src, grammar.getLabelValue("-d"));

    for(int i = 0; i < grammar.getNumErules(); ++i) {
        byte label = grammar.getErule(i);
        edges_src.addOneEdge(src, label);
        edges_dst.addOneEdge(dst, label);
        edges_aux.addOneEdge(aux, label);
    }

    //merge and sort
    edges_src.merge();
    edges_dst.merge();
    edges_aux.merge();

    //remove the existing edges
    removeExistingEdges(edges_src, src, out, m);
    removeExistingEdges(edges_dst, dst, out, m);
    removeExistingEdges(edges_aux, aux, out, m);
  }

  private static void getDirectAddedEdges_store(Pegraph out, StoreAStmt stmt, Grammar grammar, Map<Integer, EdgeArray> m) {
    //'a', '-a', 'd', '-d', and self-loop edges
    int src = stmt.getSrc();
    EdgeArray edges_src = new EdgeArray();

    int dst = stmt.getDst();
    EdgeArray edges_dst = new EdgeArray();

    int aux = stmt.getAuxiliary();
    EdgeArray edges_aux = new EdgeArray();

    //'a', '-a'
    edges_src.addOneEdge(dst, grammar.getLabelValue("a"));
    edges_dst.addOneEdge(src, grammar.getLabelValue("-a"));

    //'d', '-d'
    edges_aux.addOneEdge(dst, grammar.getLabelValue("d"));
    edges_dst.addOneEdge(aux, grammar.getLabelValue("-d"));

    //self-loop edges
    for(int i = 0; i < grammar.getNumErules(); ++i) {
        byte label = grammar.getErule(i);
        edges_src.addOneEdge(src, label);
        edges_dst.addOneEdge(dst, label);
        edges_aux.addOneEdge(aux, label);
    }


    //merge and sort
    edges_src.merge();
    edges_dst.merge();
    edges_aux.merge();

    //remove the existing edges
    removeExistingEdges(edges_src, src, out, m);
    removeExistingEdges(edges_dst, dst, out, m);
    removeExistingEdges(edges_aux, aux, out, m);
  }

  private static void getDirectAddedEdges_load(Pegraph out, LoadAStmt stmt, Grammar grammar, Map<Integer, EdgeArray> m) {
    //'a', '-a', 'd', '-d', and self-loop edges
    int src = stmt.getSrc();
    EdgeArray edges_src = new EdgeArray();

    int dst = stmt.getDst();
    EdgeArray edges_dst = new EdgeArray();

    int aux = stmt.getAuxiliary();
    EdgeArray edges_aux = new EdgeArray();

    //'a', '-a'
    edges_src.addOneEdge(dst, grammar.getLabelValue("a"));
    edges_dst.addOneEdge(src, grammar.getLabelValue("-a"));

    //'d', '-d'
    edges_aux.addOneEdge(src, grammar.getLabelValue("d"));
    edges_src.addOneEdge(aux, grammar.getLabelValue("-d"));

    //self-loop edges
    for(int i = 0; i < grammar.getNumErules(); ++i){
        byte label = grammar.getErule(i);
        edges_src.addOneEdge(src, label);
        edges_dst.addOneEdge(dst, label);
        edges_aux.addOneEdge(aux, label);
    }

    //merge and sort
    edges_src.merge();
    edges_dst.merge();
    edges_aux.merge();

    //remove the existing edges
    removeExistingEdges(edges_src, src, out, m);
    removeExistingEdges(edges_dst, dst, out, m);
    removeExistingEdges(edges_aux, aux, out, m);
  }

  private static void getDirectAddedEdges_assign(Pegraph out, AssignAStmt stmt, Grammar grammar, Map<Integer, EdgeArray> m) {
    //'a', '-a', and self-loop edges
    int src = stmt.getSrc();
    EdgeArray edges_src = new EdgeArray();

    int dst = stmt.getDst();
    EdgeArray edges_dst = new EdgeArray();

    //'a', '-a'
    edges_src.addOneEdge(dst, grammar.getLabelValue("a"));
    edges_dst.addOneEdge(src, grammar.getLabelValue("-a"));

    //self-loop edges
    for(int i = 0; i < grammar.getNumErules(); ++i){
        byte label = grammar.getErule(i);
        edges_src.addOneEdge(src, label);
        edges_dst.addOneEdge(dst, label);
    }

    //merge and sort
    edges_src.merge();
    edges_dst.merge();

    //remove the existing edges
    removeExistingEdges(edges_src, src, out, m);
    removeExistingEdges(edges_dst, dst, out, m);
  }

  private static long startCompute_add(ComputationSet compset, Grammar grammar) {
    long totalAddedEdges = 0;
    while (true) {
        computeOneIteration(compset, grammar);
        postProcessOneIteration(compset, false);
        long realAddedEdgesPerIter = compset.getDeltasTotalNumEdges();
        totalAddedEdges += realAddedEdgesPerIter;
        if (realAddedEdgesPerIter == 0)
            break;
    }
    return totalAddedEdges;
  }

  private static void computeOneIteration(ComputationSet compset, Grammar grammar) {
    Set<Integer> vertexSet = compset.getVertices();
    for (int num : vertexSet) {
        computeOneVertex(num, compset, grammar);
    }
  }

  private static void computeOneVertex(int index, ComputationSet compset, Grammar grammar) {
    boolean oldEmpty = compset.oldEmpty(index) || compset.getOlds().get(index).isEmpty();
    boolean deltaEmpty = compset.deltaEmpty(index) || compset.getDeltas().get(index).isEmpty();

    if (oldEmpty && deltaEmpty){
        return;
    }
    ArraysToMerge containers = new ArraysToMerge();
    getEdgesToMerge(index, compset, oldEmpty, deltaEmpty, containers, grammar);
    // merge and sort edges,remove duplicate edges.
    containers.merge();
    int newEdgesNum = containers.getNumEdges();
    if (newEdgesNum != 0){
        compset.setNews(index, newEdgesNum, containers.getEdgesFirstAddr(), containers.getLabelsFirstAddr());
    }
    containers.clear();
  }

  private static void getEdgesToMerge(int index, ComputationSet compset, boolean oldEmpty, boolean deltaEmpty, ArraysToMerge containers, Grammar grammar) {
    // add s-rule edges
    if (!deltaEmpty){
        genS_RuleEdges_delta(index, compset, containers, grammar);
        genD_RuleEdges_delta(index, compset, containers, grammar);
    }
    if (!oldEmpty)
        genD_RuleEdges_old(index, compset, containers, grammar);
  }

  private static void genD_RuleEdges_old(int index, ComputationSet compset, ArraysToMerge containers, Grammar grammar) {
    int numEdges_src_old = compset.getOldsNumEdges(index);
    int[] edges_src_old = compset.getOldsEdges(index);
    byte[] labels_src_old = compset.getOldsLabels(index);

    for (int i_src = 0; i_src < numEdges_src_old; ++i_src) {
        int dstId = edges_src_old[i_src];
        byte dstVal = labels_src_old[i_src];

        if(!compset.getDeltas().containsKey(dstId)) {
            continue;
        }

        int numEdges_delta = compset.getDeltasNumEdges(dstId);
        int[] edges_delta = compset.getDeltasEdges(dstId);
        byte[] labels_delta = compset.getDeltasLabels(dstId);

        byte newVal;
        boolean added = false;
        for (int i = 0; i < numEdges_delta; ++i) {
            newVal = grammar.checkRules(dstVal, labels_delta[i]);
            if (newVal != 127) {
                if (!added) {
                    containers.addOneContainer();
                    added = true;
                }
                containers.addOneEdge(edges_delta[i], newVal);
            }
        }
    }
  }

  private static void genD_RuleEdges_delta(int index, ComputationSet compset, ArraysToMerge containers, Grammar grammar) {
    int numEdges_src_delta = compset.getDeltasNumEdges(index);
    int[] edges_src_delta = compset.getDeltasEdges(index);
    byte[] labels_src_delta = compset.getDeltasLabels(index);

    for (int i_src = 0; i_src < numEdges_src_delta; ++i_src) {
        int dstId = edges_src_delta[i_src];
        byte dstVal = labels_src_delta[i_src];

        //delta * delta
        if(compset.getDeltas().containsKey(dstId)){
            int numEdges_delta = compset.getDeltasNumEdges(dstId);
            int[] edges_delta = compset.getDeltasEdges(dstId);
            byte[] labels_delta = compset.getDeltasLabels(dstId);

            byte newVal;
            boolean added = false;
            for (int i = 0; i < numEdges_delta; ++i) {
                newVal = grammar.checkRules(dstVal, labels_delta[i]);
                if (newVal != 127) {
                    if (!added) {
                        containers.addOneContainer();
                        added = true;
                    }
                    containers.addOneEdge(edges_delta[i], newVal);
                }
            }
        }

        //delta * old
        if(compset.getOlds().containsKey(dstId)){
            int numEdges_old = compset.getOldsNumEdges(dstId);
            int[] edges_old = compset.getOldsEdges(dstId);
            byte[] labels_old = compset.getOldsLabels(dstId);
            byte newVal;
            boolean added = false;
            for (int i = 0; i < numEdges_old; ++i) {
                newVal = grammar.checkRules(dstVal, labels_old[i]);
                if (newVal != 127) {
                    if (!added) {
                        containers.addOneContainer();
                        added = true;
                    }
                    containers.addOneEdge(edges_old[i], newVal);
                }
            }
        }
    }
  }

  private static void genS_RuleEdges_delta(int index, ComputationSet compset, ArraysToMerge containers, Grammar grammar) {
    int numEdges = compset.getDeltasNumEdges(index); //## can we make sure that the deltas is uniqueness
    int[] edges = compset.getDeltasEdges(index);
    byte[] labels = compset.getDeltasLabels(index);

    byte newLabel;
    boolean added = false;
    for (int i = 0; i < numEdges; ++i) {
        newLabel = grammar.checkRules(labels[i]);
        if (newLabel != 127) {
            if (!added) {
                containers.addOneContainer();
                added = true;
            }
            containers.addOneEdge(edges[i], newLabel);
        }
    }
  }

  private static void postProcessOneIteration(ComputationSet compset, boolean isDelete) {
    // oldsV <- {oldsV,deltasV}
    for (Map.Entry<Integer, EdgeArray> entry : compset.getOlds().entrySet()) {
        int id_old = entry.getKey();
        if (compset.getDeltas().containsKey(id_old)) {
            int n1 = compset.getOldsNumEdges(id_old);
            int n2 = compset.getDeltasNumEdges(id_old);
            int[] edges = new int[n1 + n2];
            byte[] labels = new byte[n1 + n2];
            int len = AliasTool.unionTwoArray(edges, labels, n1,
                    compset.getOldsEdges(id_old), compset.getOldsLabels(id_old), n2,
                    compset.getDeltasEdges(id_old), compset.getDeltasLabels(id_old));
            compset.setOlds(id_old, len, edges, labels);
            compset.getDeltas().remove(id_old);
        }
    }

    Iterator<Map.Entry<Integer, EdgeArray>> itDeltas = compset.getDeltas().entrySet().iterator();
    while (itDeltas.hasNext()) {
        Map.Entry<Integer, EdgeArray> entry = itDeltas.next();
        int id_delta = entry.getKey();
        assert(!compset.getOlds().containsKey(id_delta));
        compset.setOlds(id_delta, compset.getDeltasNumEdges(id_delta), compset.getDeltasEdges(id_delta), compset.getDeltasLabels(id_delta));
        itDeltas.remove();
    }
    assert(compset.getDeltas().isEmpty());

    // deltasV <- newsV - oldsV, newsV <= empty set
    Iterator<Map.Entry<Integer, EdgeArray>> itNews = compset.getNews().entrySet().iterator();
    while (itNews.hasNext()) {
        Map.Entry<Integer, EdgeArray> entry = itNews.next();
        int i_new = entry.getKey();
        if (isDelete) {
            System.out.println("isDelete is true!!!");
            System.exit(6);
            //mergeToDeletedGraph(i_new, m, compset);
        }

        int n1 = compset.getNewsNumEdges(i_new);
        int n2 = compset.getOldsNumEdges(i_new);
        int[] edges = new int[n1];
        byte[] labels = new byte[n1];
        int len = AliasTool.minusTwoArray(edges, labels,
                                        n1, compset.getNewsEdges(i_new), compset.getNewsLabels(i_new),
                n2, compset.getOldsEdges(i_new), compset.getOldsLabels(i_new));

        if (len != 0){
            compset.setDeltas(i_new, len, edges, labels);
        }

        itNews.remove();

    }
  }

  private static boolean is_strong_update_dst(int x, Pegraph out, Grammar grammar, Singletons singletons) {
    /* If there exists one and only one variable o,which
     * refers to a singleton memory location,such that x and o are memory alias
     */
    assert(out.getGraph().containsKey(x));

    int numOfSingleTon = 0;
    int numEdges = out.getNumEdges(x);
    int[] edges = out.getEdges(x);
    byte[] labels = out.getLabels(x);

    for(int i = 0;i < numEdges;++i) {
        if(grammar.isMemoryAlias(labels[i]) && singletons.isSingleton(edges[i]))
            ++numOfSingleTon;
    }

    return (numOfSingleTon == 1);
  }

  private static void strong_update_store_dst_simplify(int x, Pegraph out, Set<Integer> vertices_changed, Grammar grammar, Set<Integer> vertices_affected, Singletons singletons) {
    // vertices <- must_alias(x); put *x into this set as well
    must_alias_store_dst(x, out, vertices_changed, grammar, vertices_affected, singletons);


    /* remove edges */
    Iterator<Map.Entry<Integer, EdgeArray>> it = out.getGraph().entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Integer, EdgeArray> entry = it.next();
        if (entry.getValue().isEmpty()) {
            continue;
        }

        int src = entry.getKey();

        /* delete all the ('a', '-a', 'V', 'M', and other temp labels) edges associated with a vertex within vertices_changed, and
         * all the ('V', 'M', and other temp labels) edges associated with that within vertices_affected
         * */
        EdgeArray deletedArray = new EdgeArray();
        findDeletedEdges(entry.getValue(), src, vertices_changed, vertices_affected, grammar, deletedArray);

        if(deletedArray.getSize() != 0){
            int n1 = out.getNumEdges(src);
            int n2 = deletedArray.getSize();
            int[] edges = new int[n1];
            byte[] labels = new byte[n1];
            int len = AliasTool.minusTwoArray(edges, labels, n1, out.getEdges(src), out.getLabels(src), n2, deletedArray.getEdges(), deletedArray.getLabels());
            if(len != 0){
                out.setEdgeArray(src,len,edges,labels);
            }
            else{
                it.remove();
            }
        }
    }
  }

  private static void must_alias_store_dst(int x, Pegraph out, Set<Integer> vertices_changed, Grammar grammar, Set<Integer> vertices_affected, Singletons singletons) {
    /* if there exists one and only one variable o,which
     * refers to a singleton memory location,such that x and
     * y are both memory aliases of o,then x and y are Must-alias
     */
    Set<Integer> set1 = new HashSet<>();

    assert(!singletons.isSingleton(x));
    {
        int numEdges = out.getNumEdges(x);
        int[] edges = out.getEdges(x);
        byte[] labels = out.getLabels(x);

        for (int i = 0; i < numEdges; ++i) {
            if (grammar.isMemoryAlias(labels[i])
                    && singletons.isSingleton(edges[i])) {
                set1.add(edges[i]);
            }
        }
    }
    assert(set1.size() == 1);

    //compute all the must-alias expressions
    int numEdges = out.getNumEdges(x);
    int[] edges = out.getEdges(x);
    byte[] labels = out.getLabels(x);

    for (int i = 0; i < numEdges; ++i) {
        if (grammar.isMemoryAlias(labels[i])) {
            Set<Integer> set2 = new HashSet<>();

            int candidate = edges[i];
            int numEdgess = out.getNumEdges(candidate);
            int[] edgess = out.getEdges(candidate);
            byte[] labelss = out.getLabels(candidate);

            for (int k = 0; k < numEdgess; ++k) {
                if (grammar.isMemoryAlias(labelss[k]) && singletons.isSingleton(edgess[k])) {
                    set2.add(edgess[k]);
                }
            }

            if(set2.size() == 1 && set1.containsAll(set2)){
                vertices_changed.add(candidate);
            }
        }
    }

    vertices_changed.add(x);

    //add *x into vertices as well
    for (int xx : vertices_changed) {
        int numEdgess = out.getNumEdges(xx);
        int[] edgess = out.getEdges(xx);
        byte[] labelss = out.getLabels(xx);

        for (int i = 0; i < numEdgess; ++i) {
            if (grammar.isDereference(labelss[i])) {
                vertices_changed.add(edgess[i]);
            }

            if (grammar.isDereference_reverse(labelss[i])) {
                vertices_affected.add(edgess[i]);
            }
        }
    }
  }

  private static boolean is_strong_update_aux(int aux, Pegraph out, Grammar grammar, Singletons singletons) {
    /* If there exists one and only one variable o,which
     * refers to a singleton memory location,such that x points to o
     */
    if(!out.getGraph().containsKey(aux)){
        return false;
    }

    int numOfSingleTon = 0;
    int numEdges = out.getNumEdges(aux);
    int[] edges = out.getEdges(aux);
    byte[] labels = out.getLabels(aux);

    for(int i = 0; i < numEdges; ++i) {
        if(grammar.isPointsTo(labels[i]) && singletons.isSingleton(edges[i])){
            ++numOfSingleTon;
        }
    }

    return (numOfSingleTon == 1);
  }

  private static void strong_update_store_aux_simplify(int aux, int x, Pegraph out, Set<Integer> vertices_changed, Grammar grammar, Set<Integer> vertices_affected, Singletons singletons) {
    assert(!out.getGraph().containsKey(x));
    assert(out.getGraph().containsKey(aux));

    // vertices <- must_alias(x); put *x into this set as well
    must_alias_store_aux(aux, x, out, vertices_changed, grammar, vertices_affected, singletons);

    /* remove edges */
    Iterator<Map.Entry<Integer, EdgeArray>> it = out.getGraph().entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Integer, EdgeArray> entry = it.next();
        if (entry.getValue().isEmpty()) continue;

        int src = entry.getKey();

        /* delete all the ('a', '-a', 'V', 'M', and other temp labels) edges associated with a vertex within vertices_changed, and
         * all the ('V', 'M', and other temp labels) edges associated with that within vertices_affected
         * */
        EdgeArray deletedArray = new EdgeArray();
        findDeletedEdges(entry.getValue(), src, vertices_changed, vertices_affected, grammar, deletedArray);

        if(deletedArray.getSize() != 0){
            int n1 = out.getNumEdges(src);
            int n2 = deletedArray.getSize();
            int[] edges = new int[n1];
            byte[] labels = new byte[n1];
            int len = AliasTool.minusTwoArray(edges, labels, n1, out.getEdges(src), out.getLabels(src), n2, deletedArray.getEdges(), deletedArray.getLabels());
            if(len != 0){
                out.setEdgeArray(src,len,edges,labels);
            }
            else{
                it.remove();
            }
        }
    }
  }

  private static void must_alias_store_aux(int aux, int x, Pegraph out, Set<Integer> vertices_changed, Grammar grammar, Set<Integer> vertices_affected, Singletons singletons) {
    assert(!out.getGraph().containsKey(x));
    assert(out.getGraph().containsKey(aux));

    /* if there exists one and only one variable o,which
     * refers to a singleton memory location,such that x and
     * y are both memory aliases of o,then x and y are Must-alias
     */
    Set<Integer> set1 = new HashSet<>();

    {
        int numEdges = out.getNumEdges(aux);
        int[] edges = out.getEdges(aux);
        byte[] labels = out.getLabels(aux);

        for (int i = 0; i < numEdges; ++i) {
            if (grammar.isPointsTo(labels[i])
                    && singletons.isSingleton(edges[i])) {
                set1.add(edges[i]);
            }
        }
    }
    assert(set1.size() == 1);

    //compute all the must-alias expressions
    int numEdges = out.getNumEdges(aux);
    int[] edges = out.getEdges(aux);
    byte[] labels = out.getLabels(aux);

    for (int i = 0; i < numEdges; ++i) {
        if (grammar.isPointsTo(labels[i])) {
            Set<Integer> set2 = new HashSet<>();

            int candidate = edges[i];
            int numEdgess = out.getNumEdges(candidate);
            int[] edgess = out.getEdges(candidate);
            byte[] labelss = out.getLabels(candidate);

            for (int k = 0; k < numEdgess; ++k) {
                if (grammar.isMemoryAlias(labelss[k]) && singletons.isSingleton(edgess[k])) {
                    set2.add(edgess[k]);
                }
            }

            if(set2.size() == 1 && set1.containsAll(set2)){
                vertices_changed.add(candidate);
            }
        }
    }

    vertices_changed.add(x);

    //add *x into vertices as well
    for (int xx : vertices_changed) {
        int numEdgess = out.getNumEdges(xx);
        int[] edgess = out.getEdges(xx);
        byte[] labelss = out.getLabels(xx);

        for (int i = 0; i < numEdgess; ++i) {
            if (grammar.isDereference(labelss[i])) {
                vertices_changed.add(edgess[i]);
            }
            if (grammar.isDereference_reverse(labelss[i])) {
                vertices_affected.add(edgess[i]);
            }
        }
    }
  }

  private static void transfer_calleefptr(Pegraph pegraph, CalleefptrAStmt stmt, Grammar grammar, Singletons singletons) {
  }

  private static void transfer_callfptr(Pegraph pegraph, CallfptrAStmt stmt, Grammar grammar, Singletons singletons) {
  }

  private static void transfer_skip(Pegraph pegraph, SkipAStmt stmt, Grammar grammar, Singletons singletons) {
  }

  private static void transfer_ret(Pegraph pegraph, RetAStmt stmt, Grammar grammar, Singletons singletons) {
  }

  private static void transfer_return(Pegraph pegraph, ReturnAStmt stmt, Grammar grammar, Singletons singletons) {
  }

  private static void transfer_call(Pegraph pegraph, CallAStmt stmt, Grammar grammar, Singletons singletons) {
  }

  public boolean propagate(Fact oldFact, Fact newFact){
    if(oldFact == null) return true; // 节点值没有被更新过

    Pegraph newPEG = (Pegraph)newFact;
    Pegraph oldPEG = (Pegraph)oldFact;
    return !newPEG.consistent(oldPEG);
  }

  public static long myCompare(int v1, byte l1, int v2, byte l2) {
    return (v1 == v2) ? (l1 - l2) : (v1 - v2);
  }

  public static int[] myrealloc(int[] arr, int size, int Tosize) {
    int[] tmpArr = new int[Tosize];
    if (size >= 0) System.arraycopy(arr, 0, tmpArr, 0, size);
    return tmpArr;
  }

  public static byte[] myrealloc(byte[] arr, int size, int Tosize) {
    byte[] tmpArr = new byte[Tosize];
    if (size >= 0) System.arraycopy(arr, 0, tmpArr, 0, size);
    return tmpArr;
  }

  public static int removeDuple(int len, int[] dstA, byte[] dstB, int srclen, int[] srcA, byte[] srcB) {
    int tmp = len;
    if(srclen != 0) {
        dstA[0] = srcA[0]; 
        dstB[0] = srcB[0];
        tmp = 1;
        for(int i = 1; i < srclen; ++i) {
            if(srcA[i] == srcA[i-1] && srcB[i] == srcB[i-1]) {
                continue;
            }
            else {
                dstA[tmp] = srcA[i];
                dstB[tmp] = srcB[i];
                ++tmp;
            }
        }
    }
    else {
        tmp = 0;
    }
    return tmp;
  }

  public static int unionTwoArray(int[] dstA, byte[] dstB, int len1, int[] A1, byte[] B1, int len2, int[] A2, byte[] B2) {
    // (A1,B1),(A2,B2) is sorted
    int len = 0;
    if(len1 != 0) {
        if(len2 != 0) {
            int p1 = 0; int p2 = 0;
            while(p1 < len1 && p2 < len2) {
                long value = myCompare(A1[p1],B1[p1],A2[p2],B2[p2]);
                if(value > 0) {
                    dstA[len] = A2[p2]; dstB[len] = B2[p2];
                    ++p2; ++len;
                }
                else if(value < 0) {
                    dstA[len] = A1[p1]; dstB[len] = B1[p1];
                    ++p1; ++len;
                }
                else {
                    dstA[len] = A1[p1]; dstB[len] = B1[p1];
                    ++p1; ++p2; ++len;
                }
            }
            while(p1 < len1) {
                dstA[len] = A1[p1]; dstB[len] = B1[p1];
                ++p1; ++len;
            }
            while(p2 < len2) {
                dstA[len] = A2[p2]; dstB[len] = B2[p2];
                ++p2; ++len;
            }
        }
        else {
            len = len1;
            System.arraycopy(A1, 0, dstA, 0, len);
            System.arraycopy(B1, 0, dstB, 0, len);
        }
    }
    else {
        if(len2 != 0) {
            len = len2;
            System.arraycopy(A2, 0, dstA, 0, len);
            System.arraycopy(B2, 0, dstB, 0, len);
        }
    }

    return len;
  }

  public static void quickSort(int[] A, byte[] B, int l, int r) {
    if(l < r) {
        if(r - l + 1 <= 10)
            insertSort(A,B,l,r);
        else {
            int i = split(A,B,l,r);
            quickSort(A,B,l,i-1);
            quickSort(A,B,i+1,r);
        }
    }
  }

  public static void swap(int[] A, int k, int r) {
    int tmp = A[k];
    A[k] = A[r];
    A[r] = tmp;
  }

  private static void swap(byte[] A, int k, int r) {
    byte tmp = A[k];
    A[k] = A[r];
    A[r] = tmp;
  }

  private static void insertSort(int[] A, byte[] B, int l, int r) {
    for(int j = l+1;j <= r;++j) {
        int key_v = A[j];
        byte key_c = B[j];
        int i = j-1;
        while(i >= l && (key_v < A[i] || (key_v == A[i] && key_c < B[i]))) {
            A[i+1] = A[i];
            B[i+1] = B[i];
            --i;
        }
        A[i+1] = key_v;
        B[i+1] = key_c;
    }
  }

  private static int split(int[] A, byte[] B, int l, int r) {
    int mid = (l + r) / 2; int k = l;
    if(A[mid] < A[k]) k = mid;
    if(A[r] < A[k]) k = r;
    if(k != r) {
        swap(A, k, r);
        swap(B, k, r);
    }
    if(mid != l && A[mid] < A[l]) {
        swap(A, mid, l);
        swap(B, mid, l);
    }
    int val_v = A[l];
    byte val_c = B[l];

    int i = l;
    for(int j = l+1; j <= r; ++j) {
        if((A[j] < val_v) || (A[j] == val_v && B[j] < val_c)) {
            ++i;
            swap(A, i, j);
            swap(B, i, j);
        }
    }
    swap(A, i, l);
    swap(B, i, l);
    return i;
  }
}