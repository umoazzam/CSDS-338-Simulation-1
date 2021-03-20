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

      // check if there already exists a block with the same name
      if (checkReUse(bName) != -1) {
         System.out.println("Memory could not be allocated: " +
               bName + " block already being used");
      }

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

   public boolean free(String bName) {
      int blockIndex = 0;
      int bSize = MemoryList.size();
      MemoryBlock block = new MemoryBlock();

      if ((blockIndex = checkReUse(bName)) == -1) {
         System.out.println("Error: no memory available to allocate request");
         return false;
      }

      block = (MemoryBlock) MemoryList.elementAt(blockIndex);
      block.blockStatus = false;
      block.blockName = "free";

      mergeBlocks(blockIndex, block);
      return true;
   }

   public boolean realloc(String bName, int bSize){
      int blockIndex = 0;

      // the block must be freed before reallocating
      if(free(bName) == false){
         System.out.println("Error: Freeing failed in realloc");
         return false;
      }

      // now allocate the block
      if(malloc(bName,bSize) == false){
         System.out.println("Error: malloc failed in realloc");
         return false;
      }

      return true;
  }

   private int checkReUse(String bName) {
      int bSize = MemoryList.size();
      MemoryBlock block = new MemoryBlock();

      // traverse list to find if any blocks of the same name are already being used
      for (int i = 0; i < bSize; i++) {
         block = (MemoryBlock) MemoryList.elementAt(i);
         if (block.blockStatus == true) {
            if (block.blockName.equals(bName)) {
               return i;
            }
         }
      }
      return -1;
   }

   private int checkFreeBlock(int blockSize) {
      int listSize = MemoryList.size();
      MemoryBlock Block = new MemoryBlock();

      for(int index = 0; index < listSize; index++){
         Block = (MemoryBlock) MemoryList.elementAt(index);

         if(Block.blockStatus == false){
            if(Block.blockSize >= blockSize) return index;
         }
      }
      return -1;
   }

   private void mergeBlocks(int blockIndex, MemoryBlock Block) {
      int bSize = MemoryList.size();

      MemoryBlock LeftBlock = new MemoryBlock();
      MemoryBlock RightBlock = new MemoryBlock();

      if (blockIndex == 0 && bSize == 1) {
         Block.blockStatus = false;
         nAvailable = nAvailable + Block.blockSize;
      }
   }

   
   if(blockIndex == 0 && bSize > 1){
    RightBlock = (MemoryBlock)MemoryList.elementAt(blockIndex+1);
        if(RightBlock.blockStatus == false){
            RightBlock.startAddr =  Block.startAddr;
            RightBlock.blockSize += Block.blockSize;
            MemoryList.removeElementAt(blockIndex);
        }
   }
   else{
        if(blockIndex == bSize-1){
         LeftBlock = (MemoryBlock)MemoryList.elementAt(blockIndex-1);
            if(LeftBlock.bBlockStatus == false){
              LeftBlock.endAddr   =  Block.endAddr;
              LeftBlock.blockSize += Block.blockSize;
              MemoryList.removeElementAt(blockIndex);
            }
        }
        else{
          RightBlock = (MemoryBlock)MemoryList.elementAt(blockIndex+1);
          LeftBlock  = (MemoryBlock)MemoryList.elementAt(blockIndex-1);
          if(LeftBlock.blockStatus  == false &&
             RightBlock.blockStatus == false){
             LeftBlock.endAddr = RightBlock.endAddr;
             LeftBlock.blockSize += (RightBlock.blockSize+Block.blockSize);
             MemoryList.removeElementAt(blockIndex+1);
             MemoryList.removeElementAt(blockIndex);
          }
          if(LeftBlock.blockStatus  == false &&
             RightBlock.blockStatus == true){
             LeftBlock.endAddr   =  Block.endAddr;
             LeftBlock.blockSize += Block.blockSize;
             MemoryList.removeElementAt(blockIndex);
          }
          if(RightBlock.blockStatus  == false &&
             LeftBlock.blockStatus == true){
             RightBlock.startAddr   =  Block.startAddr;
             RightBlock.blockSize += Block.blockSize;
             MemoryList.removeElementAt(blockIndex);
          }
        }
   }
   nAvailable = nAvailable + Block.blockSize;
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
