import java.util.Random;

public class Simulation {

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
