// Matthew Cook aka Big Cat
// CS 143
// Sudoku HW 2
// github.com/BigCatNW/CS143/tree/main/Sudoku
//
// This file contains the SodokuBoard class which takes a
// Sodoku text file and formats it a string for printing,
// validates a board, and checks for a win
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class SudokuBoard{
   String fileRow = "";
   public char[][] board = new char[9][9];
   // pre: A constructor, needs a valid sudoku text file as input
   // post: reads the Sudoku text file and stores it in an unformatted string
   public SudokuBoard(String file){
      try(Scanner scanner = new Scanner(Path.of(file))){
         for(int row = 0; row < 9; row++){
            fileRow = scanner.nextLine();
            for(int col = 0; col < 9; col++){
               board[row][col] = fileRow.charAt(col);
            }
         }
      } catch (IOException e) {
         System.err.println("Error: The file could not be found.");
         e.printStackTrace();
      }   
   }
   
   
   // pre: a valid SudokuBoard object containing a valid boardStr property
   // post: returns a pretty formatted Sudoku board object
   public String toString(){
      String boardStr = "";
      for(int row = 0; row < 9; row++){
         for(int col = 0; col < 9; col++){
            boardStr += this.board[row][col];
            if((col + 1) % 3 == 0) {
               boardStr += '|';
            }
         }
         boardStr += "\n";
         if((row + 1) % 3 == 0 && row < 8) {
            boardStr += "------------\n";
         }      
      }
      return boardStr;
   }
   // pre: a SudokuBoard object
   // post: returns true if there are no duplicate number in any row, column, and mini-square
   //       and all values are the integer 1-9 or a period
   public boolean isValid(){
      return validateCharacters() && validateRows() && validateColumns() && validateMiniBoxes();
   }
   
   // verify every char in boars is '.' or 0-9
   // pre: a 9x9 character board
   // post: returns true if all characters are '.' or 1-9, else false
   public boolean validateCharacters(){
      char c;
      Set<Character> charSet = new HashSet<>();
      for(int i = 0; i < 9; i++){
         charSet.clear();
         for(int j = 0; j < 9; j++){
            c = this.board[i][j];
            if((c < '0' || c > '9') && c != '.'){
               return false;
            }
         }
      }
      return true;
   }
   
   // verify rows contain no duplicates and only valid chars
   // pre: a 9x9 character board
   // post: returns true if there are no duplicate digits in each row, else false
   public boolean validateRows() {
      Set<Character> charSet = new HashSet<>();
      for(int i = 0; i < this.board.length; i++){
         charSet.clear();
         for(int j = 0; j < this.board[0].length; j++){
            if(Character.isDigit(this.board[i][j]) && !charSet.add(this.board[i][j])){
               return false;
            }
         }
      }
      return true;
   }
   
   // verify columns contain no duplicates and only valid chars
   // pre: a 9x9 character board
   // post: returns true if there are no duplicate digits in each column, else false
   public boolean validateColumns() {
      Set<Character> charSet = new HashSet<>();
      for(int i = 0; i < this.board[0].length; i++){
         charSet.clear();
         for(int j = 0; j < this.board.length; j++){
            if(Character.isDigit(this.board[j][i]) && !charSet.add(this.board[j][i])){
               return false;
            }
         }
      }
      return true;
   }
   // verify miniboxes contain no duplicates
   // pre: a 9x9 character board
   // post: returns true if there are no duplicate digits in each 3x3 mini box, else false
   public boolean validateMiniBoxes(){
      Set<Character> charSet = new HashSet<>();
      for(int boxRow = 0; boxRow < 9; boxRow += 3) {
         for(int colRow = 0; colRow < 9; colRow += 3) {
            charSet.clear();
            for(int i = 0; i < 3; i++){
               for(int j = 0; j < 3; j++){
                  if(Character.isDigit(this.board[j + boxRow][i + colRow]) && !charSet.add(this.board[j + boxRow][i + colRow])){
                     return false;
                  }
               }
            }        
         }
      }
      return true;
   }
   
   // pre: a valid SudokuBoard object
   // post: returns true if the puzzle is solved
   //       checks if isValid() is true AND each digit 0-9 appears 9 times
   public boolean isSolved(){
      if(!this.isValid()){
         return false;
      }
      Map<Integer, Integer> map = new HashMap<>();
      for(int i = 1; i <= 9; i++){
         map.put(i, 0);
      }
      int intChar;
      for(int i = 0; i < this.board.length; i++){
         for(int j = 0; j < this.board[0].length; j++){
            if(this.board[i][j] != '.'){
               intChar = Character.getNumericValue(this.board[i][j]);
               if(map.containsKey(intChar)){
                  map.put(intChar, map.get(intChar) + 1);
               } else {
                  map.put(intChar, 1);
               }
            }
         }
      }
      boolean solved = true;
      for(int i = 1; i <=9; i++) {
         if(map.containsKey(i) && map.get(i) != null && map.get(i) == 9){
            solved = solved;
         } else {
            solved = false;
         }
      }
      return solved;
   }
   
   // pre: a valid SudokuBoard object
   // post: returns true if the board is valid and can be solved, else returns false
   //       also modifies the this.board array into a solved Sudoku board if possible.
   public boolean solve() {
      boolean boardSolved = false;
      if(!isValid()){
         return false;
      } else if (isSolved()) {
         return true;
      } else {
         for(int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++){
               if(this.board[row][col] == '.'){
                  for(int c = 1; c <= 9 && !boardSolved; c++){
                     board[row][col] = (char) ('0' + c);
                     boardSolved = solve();
                  }
                  if(!boardSolved){
                     board[row][col] = '.';
                  } 
               }
            }
         }
      
      }
      if(this.isSolved()){
         return  true;
      } else {
         return false;
      }
   }
  
   
}