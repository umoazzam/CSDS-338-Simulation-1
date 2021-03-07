import java.util.Random;

public class Simulation {

   public static void main(String[] args) {
      Requests request = new Requests(500, 10);

   }

   private static class Requests {

      public int n;
      public double req;
      public int size;

      public Requests(int n, int size) {
         this.n = n;
         this.req = 1/n;
         this.size = size;
      }


   }



}
