import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

class TestingMain {
   public void main(){
      System.out.println("Hello World");
      Map<String, Integer> mapByName = new HashMap<>(Map.ofEntries(
      Map.entry("Allison", 18),
      Map.entry("Benson", 48),
      Map.entry("David", 20), 
      Map.entry("Erik", 20),
      Map.entry("Galen", 15),
      Map.entry("Grace", 25),
      Map.entry("Helene", 40),
      Map.entry("Janette", 18),
      Map.entry("Jessica", 35),
      Map.entry("Marty", 35),
      Map.entry("Paul", 28),
      Map.entry("Sara", 15),
      Map.entry("Stuart", 98),
      Map.entry("Tyler", 6),
      Map.entry("Zack", 20)));
      int maxAge = 100;
      int minAge = 18;
      Age age = new Age();
      TreeMap mapByAge = age.byAge(mapByName, minAge, maxAge);
      System.out.println(mapByAge);
   }   
}