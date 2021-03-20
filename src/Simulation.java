import java.util.*;

public class Simulation {

   private Vector MemoryList;
   private int nBlocks;
   private int maxHeapSize;
   private int nAvailable;
   private long start;

   public Simulation(int maxHeapSize, long start) {
      this.maxHeapSize = maxHeapSize;
      this.start = start;
      MemoryList = new Vector();

      MemoryBlock block = new MemoryBlock(maxHeapSize, 0, maxHeapSize);
      MemoryList.addElement(block);
      nAvailable = maxHeapSize;
   }

   public static void main(String[] args) {
      Random randPages = new Random();
      Queue<Request> reqs = new LinkedList<>();
      for (int i = 0; i < 500; i++) {
         reqs.add(new Request("req" + i, randPages.nextInt(20)));
      }

      Simulation sim = new Simulation(10000, System.nanoTime());
      while(!reqs.isEmpty()) {
         if(!sim.malloc(reqs.remove())) {
            System.out.println("Simulation ended due to previous error");
            break;
         }
         sim.compact();
      }
   }

   public boolean malloc(Request req) {
      return malloc(req.name, req.size);
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
      int tempBlockSize = b.size - bSize;
      int tempEndAddr = b.endAddr;

      b.status = true;
      b.endAddr = b.startAddr + bSize;
      b.size = bSize;
      b.name = bName;

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
      block.status = false;
      block.name = "free";

      mergeBlocks(blockIndex, block);
      return true;
   }

   public boolean realloc(String bName, int bSize) {
      int blockIndex = 0;

      // the block must be freed before reallocating
      if (free(bName) == false) {
         System.out.println("Error: Freeing failed in realloc");
         return false;
      }

      // now allocate the block
      if (malloc(bName, bSize) == false) {
         System.out.println("Error: malloc failed in realloc");
         return false;
      }

      return true;
   }

   public void compact(){
      int nSize = MemoryList.size();
      String blockNames[] = new String[nSize];
      int blockSizes[] = new int[nSize];

      MemoryBlock Block = new MemoryBlock();
      int index = 0;
      int indexF = 0;

      while(index < nSize){
         Block = (MemoryBlock)MemoryList.elementAt(index);
         if(Block.status == true && index != 0){
            blockNames[indexF] = Block.name;
            blockSizes[indexF] = Block.size;
            indexF++;
         }
         index++;
      }

      index=0;

      while(index < indexF){
         free(blockNames[index]);
         malloc(blockNames[index],blockSizes[index]);
         index++;
      }
   }

   private int checkReUse(String bName) {
      int bSize = MemoryList.size();
      MemoryBlock block = new MemoryBlock();

      // traverse list to find if any blocks of the same name are already being used
      for (int i = 0; i < bSize; i++) {
         block = (MemoryBlock) MemoryList.elementAt(i);
         if (block.status == true) {
            if (block.name.equals(bName)) {
               return i;
            }
         }
      }
      return -1;
   }

   private int checkFreeBlock(int blockSize) {
      int listSize = MemoryList.size();
      MemoryBlock Block = new MemoryBlock();

      for (int index = 0; index < listSize; index++) {
         Block = (MemoryBlock) MemoryList.elementAt(index);

         if (Block.status == false) {
            if (Block.size >= blockSize) return index;
         }
      }
      return -1;
   }

   private void mergeBlocks(int blockIndex, MemoryBlock Block) {
      int bSize = MemoryList.size();

      MemoryBlock LeftBlock = new MemoryBlock();
      MemoryBlock RightBlock = new MemoryBlock();

      if (blockIndex == 0 && bSize == 1) {
         Block.status = false;
         nAvailable = nAvailable + Block.size;
      }

      if (blockIndex == 0 && bSize > 1) {
         RightBlock = (MemoryBlock) MemoryList.elementAt(blockIndex + 1);
         if (RightBlock.status == false) {
            RightBlock.startAddr = Block.startAddr;
            RightBlock.size += Block.size;
            MemoryList.removeElementAt(blockIndex);
         }
      } else {
         if (blockIndex == bSize - 1) {
            LeftBlock = (MemoryBlock) MemoryList.elementAt(blockIndex - 1);
            if (LeftBlock.status == false) {
               LeftBlock.endAddr = Block.endAddr;
               LeftBlock.size += Block.size;
               MemoryList.removeElementAt(blockIndex);
            }
         } else {
            RightBlock = (MemoryBlock) MemoryList.elementAt(blockIndex + 1);
            LeftBlock = (MemoryBlock) MemoryList.elementAt(blockIndex - 1);
            if (LeftBlock.status == false && RightBlock.status == false) {
               LeftBlock.endAddr = RightBlock.endAddr;
               LeftBlock.size += (RightBlock.size + Block.size);
               MemoryList.removeElementAt(blockIndex + 1);
               MemoryList.removeElementAt(blockIndex);
            }
            if (LeftBlock.status == false && RightBlock.status == true) {
               LeftBlock.endAddr = Block.endAddr;
               LeftBlock.size += Block.size;
               MemoryList.removeElementAt(blockIndex);
            }
            if (RightBlock.status == false && LeftBlock.status == true) {
               RightBlock.startAddr = Block.startAddr;
               RightBlock.size += Block.size;
               MemoryList.removeElementAt(blockIndex);
            }
         }
      }
      nAvailable = nAvailable + Block.size;
   }

   private static class Request {

      public String name;
      public int size;

      public Request(String name, int size) {
         this.name = name;
         this.size = size;
      }



   }

}
