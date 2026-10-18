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
      File file = new File("C:\\Users\\Matthew Cook\\Documents\\jGRASP\\Sudoku\\data1-1-1.sdk");
      SudokuBoard board = new SudokuBoard(file);
      System.out.println(board.toString());
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