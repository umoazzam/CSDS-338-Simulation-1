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
      int blockIndex = 0;
      MemoryBlock b = new MemoryBlock();

//      // check if there already exists a block with the same name
//      if (checkReUse(bName) != -1) {
//         System.out.println("Memory could not be allocated: " +
//               bName + " block already being used");
//      }
      // find a block to fit the current request
      if ((blockIndex = checkFreeBlock(bSize)) == -1) {
         System.out.println("Error: no memory available to allocate request");
         return false;
      }

      // get block and update fields
      b = (MemoryBlock) MemoryList.elementAt(blockIndex);
      int tempBlockSize = b.blockSize - bSize;
      int tempEndAddr = b.endAddr;

      b.blockStatus = true;
      b.endAddr = b.startAddr + bSize;
      b.blockSize = bSize;
      b.blockName = bName;

      if (tempBlockSize != 0) {
         MemoryBlock newBlock = new MemoryBlock(
               tempBlockSize,
               b.startAddr + bSize,
               tempEndAddr);
      }

      return true;
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
