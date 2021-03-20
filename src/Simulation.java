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

   public boolean malloc(String bName, int bSize) {
      MemoryBlock b = new MemoryBlock();

      // check if there already exists a block with the same name

      // find a block to fit the current request

      // get the block

      return true;
   }

   private int checkFreeBlock(int blockSize){
   int listSize = MemoryList.size();
   MemoryBlock Block = new MemoryBlock();

   for(int index=0; index < listSize; index++){
    Block = (MemoryBlock)MemoryList.elementAt(index);

    if(Block.blockStatus == false){
     if(Block.blockSize >= blockSize) return index;
    }
   }
   return -1;
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
