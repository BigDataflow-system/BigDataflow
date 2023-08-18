package alias_data;

public class MinHeapNode {
  int key_v;
  byte key_c;
  int i;	// index of array
  int j;	// next element's index
}

class MinHeap {

  private final MinHeapNode[] harr;
  private final int size;
  public MinHeap(MinHeapNode[] a, int size) {
      this.size = size;
      harr = a;
      int i = (size-1)/2;
      while(i >= 0) {
          MinHeapify(i);
          --i;
      }
  }

  private void MinHeapify(int i) {
      int l = left(i);
      int r = right(i);
      int smallest = i;
      if(l < size && AliasTool.myCompare(harr[l].key_v,harr[l].key_c,harr[i].key_v,harr[i].key_c) < 0)
      smallest = l;
      if(r < size && AliasTool.myCompare(harr[r].key_v,harr[r].key_c,harr[smallest].key_v,harr[smallest].key_c) < 0)
      smallest = r;
      if(smallest != i) {
          MinHeapNode tmp = harr[i];
          harr[i] = harr[smallest];
          harr[smallest] = tmp;
          MinHeapify(smallest);
      }
  }

  private int right(int i) {
      return 2*i+2;
  }

  private int left(int i) {
      return 2*i+1;
  }

  public MinHeapNode getMin() {
      return harr[0];
  }

  public void replaceMin(MinHeapNode p) {
      harr[0] = p;
      MinHeapify(0);
  }
}