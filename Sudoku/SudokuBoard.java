// Matthew Cook aka Big Cat
// CS 143
// Sudoku HW Parts 1-4
// github.com/BigCatNW/CS143/tree/main/Sudoku
//
// This file contains the SodokuBoard class which takes a
// Sodoku text file and formats it a string for printing
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SudokuBoard{
   String boardStr = "";
   int[][] board = new int[9][9];
   // pre: A constructor, needs a valid sudoku text file as input
   // post: reads the Sudoku text file and stores it in an unformatted string
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
   // pre: a valid SudokuBoard object containing a valid boardStr property
   // post: returns a pretty formatted Sudoku board object
   public String toString(){
      char[] arr = boardStr.toCharArray();
      int i = 0;
      String boardNewStr = "";
      for(char ch: boardStr.toCharArray()) {
         i++;
         // inserts spaces after 2 out of every 3 numbers
         if(i % 3 != 0) {
            boardNewStr += ch + " ";
         } else {
            boardNewStr += ch;
         } 
         // uses bars and dashes to seperate board into ninths
         if(i == boardStr.length()){
            break;
         } else if(i % 27 == 0){
            boardNewStr += "\n-----------------\n";
         } else if(i % 9 == 0){
            boardNewStr += "\n";
         } else if (i % 3 == 0) {
            boardNewStr += "|";
         }
      }
      return boardNewStr;
   }
}