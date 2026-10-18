package pkgSudoku;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SudokuBoard{
   String boardStr = "";
   int[][] board = new int[9][9];
   
   public SudokuBoard(File file){
      try(Scanner scanner = new Scanner(file)){
         while (scanner.hasNextLine()) {
            boardStr += scanner.nextLine();
         }
      } catch (FileNotFoundException e) {
         System.err.println("Error: The file could not be found.");
         e.printStackTrace();
      }   
   }

   public String toString(){
      char[] arr = boardStr.toCharArray();
      int i = 0;
      String boardNewStr = "";
      for(char ch: boardStr.toCharArray()) {
         i++;
         boardNewStr += ch;
         if(i == boardStr.length()){
            break;
         } else if(i % 9 == 0){
            boardNewStr += "\n";
         } else if (i % 3 == 0) {
            boardNewStr += "\u2551";
         }
      }
      return boardNewStr;
   }
   
}
