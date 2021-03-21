import java.util.*;

public class Simulation {

   // linked list of blocks of memory
   private Vector MemoryList;
   // number of blocks
   private int nBlocks;
   // amount of memory currently available
   private int nAvailable;
   // max size of memory
   private int maxHeapSize;
   // start time of simulation
   private long start;

   // constructor, sets up memory list as contiguous block of memory
   public Simulation(int maxHeapSize, long start) {
      System.out.println("Simulation started");
      // instantiating private fields
      this.maxHeapSize = maxHeapSize;
      nAvailable = maxHeapSize;;
      this.start = start;
      MemoryList = new Vector();

      // instantiating contiguous block of memory
      MemoryBlock block = new MemoryBlock(maxHeapSize, 0, maxHeapSize);
      MemoryList.addElement(block);
   }

   // creates stream of requests and allocates in memory
   public static void main(String[] args) {
      // creates queue of randomly-generated requests
      Random randPages = new Random();
      Queue<Request> reqs = new LinkedList<>();
      for (int i = 0; i < 500; i++) {
         reqs.add(new Request("req" + i, randPages.nextInt(19) + 1));
      }

      // starts simulation, feeds requests to simulator for memory allocation
      Simulation sim = new Simulation(10000, System.nanoTime());
      while(!reqs.isEmpty()) {
         sim.nBlocks = 0;
         String reqName = reqs.peek().name;
         System.out.println("Allocating request: " + reqs.peek());
         if(!sim.malloc(reqs.remove())) {
            System.out.println("Simulation ended due to previous error");
            break;
         }
         System.out.println("Allocated memory for " + reqName +
               " at index " + sim.findBlock(reqName) + ", time = " + sim.getTime());
         System.out.println();
         sim.compact();
         System.out.println("Memory available: " + sim.nAvailable);
         System.out.println("Number of blocks used: " + sim.nBlocks);
         System.out.println();

      }
      System.out.println();
      System.out.println("Simulation ended. Time elapsed: " + sim.getTime());
   }

   // takes in request and sends to private method with request name and size
   public boolean malloc(Request req) {
      return malloc(req.name, req.size);
   }

   /* allocates memory for request, based on first-fit method
      returns false if memory allocation fails */
   private boolean malloc(String bName, int bSize) {
      int blockIndex;
      MemoryBlock b;

      // check if there already exists a block with the same name
      if (findBlock(bName) != -1) {
         System.out.println("Memory could not be allocated: " +
               bName + " block already being used");
      }

      // find a block to fit the current request
      if ((blockIndex = findFreeBlock(bSize)) == -1) {
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
         MemoryList.insertElementAt((Object) newBlock, blockIndex + 1);
      }

      nAvailable = nAvailable - bSize;
      return true;
   }

   /* frees allocated memory for other use
      returns false if specified block cannot be freed */
   public boolean free(String bName) {
      int blockIndex;
      int bSize = MemoryList.size();
      MemoryBlock block;

      // check if there already exists a block with the same name
      if ((blockIndex = findBlock(bName)) == -1) {
         System.out.println("Error in free(): no memory available to allocate request");
         return false;
      }

      block = (MemoryBlock) MemoryList.elementAt(blockIndex);
      block.status = false;
      block.name = "free";

      System.out.println("Fragmentation: " +
            (double) (block.size) / (double) (maxHeapSize - nAvailable));

      mergeBlocks(blockIndex, block);
      return true;
   }

   // frees empty space at higher memory addresses and reallocates memory to lower addresses
   public void compact(){
      long compactStart = System.nanoTime();
      System.out.println("Initiating optimal redistribution of memory");
      int nSize = MemoryList.size();
      String[] blockNames = new String[nSize];
      int[] blockSizes = new int[nSize];

      MemoryBlock Block;
      int index = 0;
      int indexF = 0;

      // collect allocated blocks in memory using two-pass memory compaction
      while(index < nSize){
         Block = (MemoryBlock) MemoryList.elementAt(index);
         if(Block.status && index != 0){
            blockNames[indexF] = Block.name;
            blockSizes[indexF] = Block.size;
            indexF++;
         }
         index++;
      }
      index = 0;

      // move collected memory blocks to lower memory location
      while(index < indexF){
         free(blockNames[index]);
         malloc(blockNames[index],blockSizes[index]);
         nBlocks++;
         index++;
      }

      long timeElapsed = System.nanoTime() - compactStart;
      System.out.println("Memory redistributed, runtime = " + timeElapsed);
   }

   /* searches for block in memory with a given name
      returns index if block is present, -1 if block isn't present */
   private int findBlock(String bName) {
      int bSize = MemoryList.size();
      MemoryBlock block;

      // traverse list to find if any blocks of the same name are already being used
      for (int i = 0; i < bSize; i++) {
         block = (MemoryBlock) MemoryList.elementAt(i);

         // checks if the block is being used currently to hold memory
         if (block.status) {
            if (block.name.equals(bName)) {
               return i;
            }
         }
      }
      return -1;
   }

   /* searches memory for free block pf memory of specified size
      returns index if block is found, -1 if block isn't present */
   private int findFreeBlock(int blockSize) {
      int listSize = MemoryList.size();
      MemoryBlock Block;

      for (int index = 0; index < listSize; index++) {
         Block = (MemoryBlock) MemoryList.elementAt(index);

         if (!Block.status) {
            if (Block.size >= blockSize) {
               return index;
            }
         }
      }
      return -1;
   }

   // merges adjacent blocks of free memory
   private void mergeBlocks(int blockIndex, MemoryBlock Block) {
      int bSize = MemoryList.size();

      MemoryBlock LeftBlock;
      MemoryBlock RightBlock;

      // if only one block is available, mark it empty
      if (blockIndex == 0 && bSize == 1) {
         Block.status = false;
         nAvailable = nAvailable + Block.size;
      }

      // check if adjacent blocks are free and expand
      if (blockIndex == 0 && bSize > 1) {
         RightBlock = (MemoryBlock) MemoryList.elementAt(blockIndex + 1);
         if (!RightBlock.status) {
            // manipulate address field
            RightBlock.startAddr = Block.startAddr;
            RightBlock.size += Block.size;
            MemoryList.removeElementAt(blockIndex);
         }
      } else {
         // check if final block is the end of free memory
         if (blockIndex == bSize - 1) {
            LeftBlock = (MemoryBlock) MemoryList.elementAt(blockIndex - 1);
            if (!LeftBlock.status) {
               // manipulate address field
               LeftBlock.endAddr = Block.endAddr;
               LeftBlock.size += Block.size;
               MemoryList.removeElementAt(blockIndex);
            }
         } else {
            // merge left and right block
            RightBlock = (MemoryBlock) MemoryList.elementAt(blockIndex + 1);
            LeftBlock = (MemoryBlock) MemoryList.elementAt(blockIndex - 1);
            if (!LeftBlock.status && !RightBlock.status) {
               LeftBlock.endAddr = RightBlock.endAddr;
               LeftBlock.size += (RightBlock.size + Block.size);
               MemoryList.removeElementAt(blockIndex + 1);
               MemoryList.removeElementAt(blockIndex);
            }
            if (!LeftBlock.status && RightBlock.status) {
               // manipulate address field
               LeftBlock.endAddr = Block.endAddr;
               LeftBlock.size += Block.size;
               MemoryList.removeElementAt(blockIndex);
            }
            if (!RightBlock.status && LeftBlock.status) {
               // manipulate address field
               RightBlock.startAddr = Block.startAddr;
               RightBlock.size += Block.size;
               MemoryList.removeElementAt(blockIndex);
            }
         }
      }

      nAvailable = nAvailable + Block.size;
   }

   // returns time since sim start
   private long getTime() {
      return System.nanoTime() - start;
   }

   // generates and stores information about request
   private static class Request {

      // name of request
      public String name;
      // size of memory needed for request allocation
      public int size;
      // probability that method will be freed

      // instantiates request fields
      public Request(String name, int size) {
         this.name = name;
         this.size = size;
      }

      @Override
      public String toString() {
         return "(" + name + ", size = " + size + ")";
      }
   }

}
