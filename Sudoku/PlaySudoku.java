import java.io.File;
public class PlaySudoku{
   public void main(String[] args){
      File file = new File("C:\\Users\\Matthew Cook\\Documents\\jGRASP\\Sudoku\\data1-1-1.sdk");
      SudokuBoard board = new SudokuBoard(file);
      System.out.println(board.toString());
   }
}