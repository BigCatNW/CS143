// Matthew Cook
// CS143 - Exploring ArrayList and ArrayIntList
// 9/27/26


import java.util.*;

public class ExploringArrayList {
   public static void main(String[] args) {
      // Instansiate new ArrayIntList
      ArrayIntList mylist = new ArrayIntList();
      
      // Test add(value) fucntion
      for(int i = 0; i < 19; i++){
         mylist.add(i+1);
      }
      
      // Test toString() function
      System.out.println("mylist = " + mylist.toString());
      
      // Test add(value,index) function
      System.out.println("inserting 0 at index 0");
      mylist.add(0,0);
      System.out.println("mylist = " + mylist.toString());  
          
      // Test remove(index) function
      int remove = 19;
      System.out.println("removing index " + remove);
      mylist.remove(remove);
      System.out.println("mylist = " + mylist.toString());
            
      // Test size() function
      System.out.println("mylist size = " + mylist.size());
      
      // Test get() function
      System.out.println("value at index 4 = " + mylist.get(4));
      
      // Test indexOf() function
      System.out.println("value 9 found at index = " + mylist.indexOf(9));
      
      // Test equals() function
      ArrayIntList testlist = new ArrayIntList();
      testlist.add(1);
      ArrayIntList testlist2 = new ArrayIntList();
      testlist.add(1);
      boolean listequals = testlist.equals(testlist);
      System.out.println(listequals);
      
   }
}

/* Output from jGRASP

  ----jGRASP exec: java ExploringArrayList
 mylist = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19]
 inserting 0 at index 0
 mylist = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19]
 removing index 19
 mylist = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18]
 mylist size = 19
 value at index 4 = 4
 value 9 found at index = 9
 
  ----jGRASP: Operation complete.
  
*/