// Matthew Cook aka Big Cat
// CS 143
// Sudoku HW Parts 1-4
// github.com/BigCatNW/CS143/tree/main/Sudoku
//
// This file creates an prints a Sudoku board from a file.
import java.io.File;
public class PlaySudoku{
   // pre: none, this is a constructor
   // post: reads a sudoku board file and prints it
   public void main(String[] args){
      String file = "boards/fast-solve.sdk";
      SudokuBoard board = new SudokuBoard(file);
      System.out.println("Processing File: " + file);
      System.out.println(board.toString());
      System.out.println();
      if(board.isValid()){
         System.out.println("Is valid");
      } else {
         System.out.println("Is NOT valid");
      }
      
      if(board.isSolved()){
         System.out.println("Is solved");
      } else {
         System.out.println("Is NOT solved");
      }
      System.out.println();
      System.out.print("Solving board...");
      long start = System.currentTimeMillis();
      board.solve();
      long stop = System.currentTimeMillis();
      System.out.printf("SOLVED in %.3f seconds.\n", ((stop-start)/1000.0));
      System.out.println();
      System.out.println(board);
   }
}
/* jGrasp Output:
  ----jGRASP exec: java -ea PlaySudoku
 Processing File: boards/fast-solve.sdk
 827|154|396|
 965|.27|148|
 341|6.9|752|
 ------------
 ...|...|...|
 ...|...|...|
 618|97.|435|
 ------------
 786|235|.14|
 154|796|8.3|
 239|84.|...|
 
 
 Is valid
 Is NOT solved
 
 Solving board...SOLVED in 2.613 seconds.
 
 827|154|396|
 965|327|148|
 341|689|752|
 ------------
 472|513|689|
 593|468|271|
 618|972|435|
 ------------
 786|235|914|
 154|796|823|
 239|841|567|
 
 
  ----jGRASP: Operation complete.
*/