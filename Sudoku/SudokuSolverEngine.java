
public class SudokuSolverEngine {

   public static void main(String[] args) {
      // Here I have called my class `MySudokuBoard` if you named your class
      // differently, modify the line below to use your own class name
      SudokuBoard board = new SudokuBoard("boards/very-fast-solve.sdk");
      System.out.println("Initial board");
      System.out.println(board);
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

/*
  ----jGRASP exec: java -ea SudokuSolverEngine
 Initial board
 .34|678|912|
 .72|195|348|
 198|342|567|
 ------------
 ..9|.61|423|
 .26|853|791|
 .13|924|.56|
 ------------
 .61|537|284|
 .8.|419|635|
 345|.86|179|
 
 
 Solving board...SOLVED in 0.725 seconds.
 
 534|678|912|
 672|195|348|
 198|342|567|
 ------------
 859|761|423|
 426|853|791|
 713|924|856|
 ------------
 961|537|284|
 287|419|635|
 345|286|179|
 
 
  ----jGRASP: Operation complete.
  
*/