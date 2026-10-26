import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Scanner;
public class TestingMain{
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        String name = "";
        Map<String, Integer> map= new TreeMap<>();
        do{
            System.out.printf("Enter name: ");
            name = scan.nextLine();
            if (name == "" || name == " "){
               break;
                           }
            if(map.containsKey(name)){
                map.put(name, map.get(name) + 1);
            } else {
                map.put(name, 1);
            }
        } while (scan.hasNextLine());
        for(Map.Entry entry: map.entrySet()){
            System.out.println("Entry [" + entry.getKey() + "] has count " + entry.getValue());
        }
    }
}