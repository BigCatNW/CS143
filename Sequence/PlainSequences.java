// Matthew Cook aka Big Cat
// CS 143 
// Read a DNA sequence file to a string
//    - format string for printing
//    - create hashmap for substrings and number of occurence
//    - Parse sequence into all substrings
//    - Return a sorted list of substrings
// 10/26/26

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
   // post: returms a HashMap, the keys consist of every substring that can be made from the sequences string,
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
   
   // pre: a valid mRNA text file and a valid starting index
   // post: returns all of the substrings of the sequence as a sorted ArrayList
   public List<String> getSortedListOfSubstrings(int index){
      List<String> list = new ArrayList<>(this.generateFrequencies(index).keySet());
      list.sort(Comparator.naturalOrder());
      return list;
   }
   
   //pre: a valid mRNA text file and a valid starting index
   //post: returns the reverse compliment by reversing the sequence and replacing charaacters as follows:
   //       't' becomes 'a',
   //       'a' becomes 't',
   //       'g' becomes 'c', 
   //       'c' becomes 'g'.
   public String getReverseComplement(int index){
      String str = sequences.get(0);
      Stack<Character> s = new Stack<>();
      for(int i = 0; i < str.length(); i++){
         s.push(str.charAt(i));
      }
      str = "";
      while(!s.isEmpty()){
         switch(s.peek()) {
            case 'T':
               str = str + 'A';
               s.pop();
               break;
            case 'A':
               str = str + 'T';
               s.pop();
               break;
            case 'G':
               str = str + 'C';
               s.pop();
               break;
            case 'C':
               str = str + 'G'; 
               s.pop();
               break;
         }
        
      }
      return str;
   }

}

/*
 
  ----jGRASP exec: java junit_runner.JgrRunner 57738 PlainSequencesTest
 Running 7 JUnit tests.
 
  === PlainSequencesTest.testGetReverseComplement ===
 org.junit.ComparisonFailure: the reverse complement is not right expected:<[TCTGCC]> but was:<[]>
 	at PlainSequencesTest.testGetReverseComplement(PlainSequencesTest.java:163)
 
 Completed 7 tests  6 passed  1 failed
 
  ----jGRASP: Operation complete.
 
  ----jGRASP exec: java -Xint -Xrunjdwp:transport=dt_socket,suspend=y,server=y junit_runner.JgrRunner 57587 PlainSequencesTest
  ----jGRASP: Connected to debugger.
 Running 7 JUnit tests.
  ----jGRASP: Process ended by user.
 
  ----jGRASP exec: java -Xint -Xrunjdwp:transport=dt_socket,suspend=y,server=y junit_runner.JgrRunner 57602 PlainSequencesTest
  ----jGRASP: Connected to debugger.
 Running 7 JUnit tests.
  ----jGRASP: Process ended by user.
 
  ----jGRASP exec: java junit_runner.JgrRunner 58225 PlainSequencesTest
 Running 7 JUnit tests.
 
  === PlainSequencesTest.testGetReverseComplement ===
 org.junit.ComparisonFailure: the reverse complement is not right expected:<[TCTGCC]> but was:<[]>
 	at PlainSequencesTest.testGetReverseComplement(PlainSequencesTest.java:163)
 
 Completed 7 tests  6 passed  1 failed
 
  ----jGRASP: Operation complete.
 
  ----jGRASP exec: java -Xint -Xrunjdwp:transport=dt_socket,suspend=y,server=y junit_runner.JgrRunner 58227 PlainSequencesTest
  ----jGRASP: Connected to debugger.
 Running 7 JUnit tests.
  ----jGRASP: Process ended by user.
 
  ----jGRASP exec: java -Xint -Xrunjdwp:transport=dt_socket,suspend=y,server=y junit_runner.JgrRunner 58241 PlainSequencesTest
  ----jGRASP: Connected to debugger.
 Running 7 JUnit tests.
 
  === PlainSequencesTest.testGetReverseComplement ===
 org.junit.ComparisonFailure: the reverse complement is not right expected:<[TCTGCC]> but was:<[CCGTCT]>
 	at PlainSequencesTest.testGetReverseComplement(PlainSequencesTest.java:163)
  ----jGRASP: Process ended by user.
 
  ----jGRASP exec: java junit_runner.JgrRunner 49726 PlainSequencesTest
 Running 7 JUnit tests.
 
 Completed 7 tests  7 passed
 
  ----jGRASP: Operation complete.
 
  ----jGRASP exec: java -Xint -Xrunjdwp:transport=dt_socket,suspend=y,server=y junit_runner.JgrRunner 49728 PlainSequencesTest
  ----jGRASP: Connected to debugger.
 Running 7 JUnit tests.
  ----jGRASP: Process ended by user.
 
  ----jGRASP exec: java junit_runner.JgrRunner 65334 PlainSequencesTest
 Running 7 JUnit tests.
 
 Completed 7 tests  7 passed
 
  ----jGRASP: Operation complete.
 
 */
