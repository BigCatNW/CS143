// Matthew Cook aka Big Cat 
// CS 143
// HW #0: Setting up your Dev Environment
// github.com/theoriginalmattcook/CS143


import java.util.Scanner;

// A class to print "Hello World" 5 times and prompt the user to print an additional 5 times.
class HelloWorld {
   public static void main (String[] args) {
      printHelloWorld();// Method to print "Hello World" 5 times
      Scanner scan = new Scanner(System.in);
      // Do While loop remains true and is only broken when input is other than "y"or "yes"
      do{
         System.out.printf("%s\n", "Type \"y\" or \"yes\" to print \"Hello World\" 5 more times or anything else to exit!");
         String input = scan.nextLine();
         if("y".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input)){
            printHelloWorld();
         } else {
            break;
         }
       }while(true);
      
   }
   
   public static void printHelloWorld(){
    for (int i=1; i<=5; i++){
         System.out.printf("%s\n", "Hello World");
      }
   }
   
}
/* Output from jGRASP
  ----jGRASP exec: java HelloWorld
 Hello World
 Hello World
 Hello World
 Hello World
 Hello World
 Type "y" or "yes" to print "Hello World" 5 more times or anything else to exit!
 yes
 Hello World
 Hello World
 Hello World
 Hello World
 Hello World
 Type "y" or "yes" to print "Hello World" 5 more times or anything else to exit!
 y
 Hello World
 Hello World
 Hello World
 Hello World
 Hello World
 Type "y" or "yes" to print "Hello World" 5 more times or anything else to exit!
 n
 
  ----jGRASP: Operation complete.
*/

//   meow
//  |\---/|
//  | o_o |
//   \_^_/