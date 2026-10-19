// Matthew Cook aka Big Cat
// CS 143 
// Read a DNA sequence file to a string
//    - format string for printing
//    - create hashmap for substrings and number of occurences
// 10/18/26

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.*;

public class PlainSequences implements Sequences{
   public List<String> sequences;
   public List<String> descriptions;
  
   // pre: none
   // post: constructor for PlainSequences object with two properties
   public PlainSequences() {
      descriptions = new ArrayList<String>();
      sequences = new ArrayList<String>();
   }
   
   // pre: an index within the bounds of the sequences string
   // post: returms a hasmap, the keys consist of every substring that can be made from the sequences string,
   //       the values consist of the number of occurences of each substring within the file.
   public Map<String, Integer> generateFrequencies(int index){
      String str = sequences.get(0);
      Map<String, Integer> map = new HashMap<>();
      // iterates over each possible starting index
      for(int i = index; i <= str.length(); i++){
         // iterates over each possible ending index
         for(int j = i + 1 ; j <= str.length(); j++){
            // Checks for presence of current substring.
            // If there, increment value 1
            if(map.containsKey(str.substring(i,j))){
               map.put(str.substring(i,j), map.get(str.substring(i,j)) + 1);
            // If not present, add substring as key and 1 as value
            } else {
               map.put(str.substring(i,j), 1);
            }
         }   
      }
      return map;
   }

   // pre: none
   // post: returns descriptions
   public List<String> getDescriptions(){
      return descriptions;
   }
  
   // pre: none
   // post: returns sequences Array List object
   public List<String> getSequences(){
      return sequences;
   }

   // pre: valid text file name
   // post: reads text file into an Array List object called sequences
   public void readSequences(String fileName) throws FileNotFoundException {
      Scanner input = new Scanner(new File(fileName));
      String sequence = "";
      int count = 0;
      while(input.hasNextLine()){
         sequence = sequence + input.nextLine();
      }
      descriptions.add(fileName);
      sequences.add(sequence);
   }
   
   // pre: valid index within the array list object
   // post: returns true if all characters in the sequences object are one of the following, else returns false
   /* I use Regular Expressions to check if the sequence consists of the following characters
   A - Adenine
   C - Cytosine
   G - Guanine
   T - Thymine
   new line (\\n)
   space
   */
   public boolean isValidSequence(int index){
        String allowedChars = "ACGT \\n";
        String regex = "[" + allowedChars + "]+"; 
        if(sequences.get(index).matches(regex)){
          return true;
        } else {
          return false;
        }
   }
   
   // pre: a valid starting index, number of characters per group, and groups per line
   // post: returns a formatted string per the parameters
   // I use the modulus to find indices divisible by the basesPerGroup and groupsPerLine!
   public String formatInGroups(int index, int basesPerGroup, int groupsPerLine){
      String str = sequences.get(0);
      String newstr ="";
      for (int i = 0; i < str.length(); i++) {
         newstr = newstr + str.charAt(i);
         if(i == str.length()) {
           break;
         } else if ((i + 1) % (groupsPerLine * basesPerGroup) == 0) {
           newstr = newstr + " \n";
         } else if ((i + 1) % basesPerGroup == 0) {
           newstr = newstr + " ";
         }
      }
      return newstr;
   }
}


