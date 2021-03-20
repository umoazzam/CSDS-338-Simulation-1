import java.util.Vector;

public class Simulation {

   private Vector MemoryList;
   private int nBlocks;
   private int maxHeapSize;
   private int nAvailable;

   public Simulation(int maxHeapSize) {
      this.maxHeapSize = maxHeapSize;
      MemoryList = new Vector();

      MemoryBlock block = new MemoryBlock(maxHeapSize, 0, maxHeapSize);
      MemoryList.addElement(block);
      nAvailable = maxHeapSize;
   }

   public static void main(String[] args) {
      Request request = new Request(500, 10);

   }

   private static class Request {

      public String requestName;
      public int n;
      public double req;
      public int size;

      public Request(int n, int size) {
         this.n = n;
         this.req = 1/n;
         this.size = size;
      }



   }



}
