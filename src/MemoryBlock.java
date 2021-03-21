public class MemoryBlock {

   public String name;
   public boolean status;
   public int size;
   public int startAddr;
   public int endAddr;

   public MemoryBlock(String name, boolean status, int size,
                      int startAddr, int endAddr) {
      this.name = name;
      this.status = status;
      this.size = size;
      this.startAddr = startAddr;
      this.endAddr = endAddr;
   }

   public MemoryBlock(int size, int startAddr, int endAddr) {
      status = false;
      this.size = size;
      this.startAddr = startAddr;
      this.endAddr = endAddr;
   }

}
