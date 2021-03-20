public class MemoryBlock {

   public String blockName;
   public boolean blockStatus;
   public int blockSize;
   public int startAddr;
   public int endAddr;


   public MemoryBlock(){

   }

   public MemoryBlock(String blockName, boolean blockStatus, int blockSize,
                      int startAddr, int endAddr) {
      this.blockName = blockName;
      this.blockStatus = blockStatus;
      this.blockSize = blockSize;
      this.startAddr = startAddr;
      this.endAddr = endAddr;
   }

   public MemoryBlock(int blockSize, int startAddr, int endAddr) {
      blockStatus = false;
      this.blockSize = blockSize;
      this.startAddr = startAddr;
      this.endAddr = endAddr;
   }

}
