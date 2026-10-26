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
      String file = "boards/valid-complete.sdk";
      SudokuBoard board = new SudokuBoard(file);
      System.out.println(board.toString());
      System.out.println();
      for(int i = 0; i < 9; i++){
         for(int j = 0; j < 9; j++){
            System.out.printf("%s", board.board[i][j]);
         }
         System.out.println();
      }
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
   }
}
/* jGrasp Output:

  ----jGRASP exec: java PlaySudoku
 2 . .|1 . 5|. . 3
 . 5 4|. . .|7 1 .
 . 1 .|2 . 3|. 8 .
 -----------------
 6 . 2|8 . 7|3 . 4
 . . .|. . .|. . .
 1 . 5|3 . 9|8 . 6
 -----------------
 . 2 .|7 . 1|. 6 .
 . 8 1|. . .|2 4 .
 7 . .|4 . 2|. . 1
 
  ----jGRASP: Operation complete.
*/