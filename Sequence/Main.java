import java.io.FileNotFoundException;


public class Main {
   
   public static void main(String[] args) {
      PlainSequences sequence = new PlainSequences();
      String fileName = "peptide_mRNA.text";
      try {
         sequence.readSequences(fileName);
      } catch (FileNotFoundException e) { 
      }
      sequence.formatInGroups(0,10, 6);
      System.out.println("Description = " + sequence.getDescriptions());
   }

}