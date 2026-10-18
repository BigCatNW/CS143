import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

public class Main {
   
   public static void main(String[] args) {
      /*
      PlainSequences sequence = new PlainSequences();
      String fileName = "peptide_mRNA.text";
      try {
         sequence.readSequences(fileName);
      } catch (FileNotFoundException e) { 
      }
      sequence.formatInGroups(0,10, 6);
      System.out.println("Description = " + sequence.getDescriptions());
      */
      Sequences tester = new PlainSequences();

      String fileNameSmall = "plainExampleSmall.text";
       try {
         tester.readSequences(fileNameSmall);
      } catch (FileNotFoundException e) { 
      }
      Map frequencies = tester.generateFrequencies(0); 
      System.out.println(frequencies);
   }

}