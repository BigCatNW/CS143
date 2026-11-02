import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.Arrays;

public class TestingMain{
   public void main(String[] args){
      travel(1,2);
   }
   public int[] travel(int x, int y){
      int currentX = 0;
      int currentY = 0;
      // move E
      if(x > currentX){
         System.out.print("E ");
         int newX = currentX +1;
         travel(x - 1, y);
      }
      // move N
      if(y > currentY){
         System.out.print("N ");
         int newX = currentX +1;
         travel(x, y - 1);
      }
      // move NE
      if(x > currentX){
         System.out.print("NE ");
         int newX = currentX +1;
         travel(x - 1, y - 1);
      }
      int[] pos = {currentX, currentY};
      return pos;
   }

}
   
   

