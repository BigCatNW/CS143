import java.util.Map;
import java.util.TreeMap;

public class Age{
   public TreeMap<Integer, String> byAge(Map<String, Integer> mapByName, int ageMin, int ageMax){
      TreeMap<Integer, String> mapByAge = new TreeMap<>();
      for(Map.Entry<String, Integer> entry : mapByName.entrySet()) {
         if(entry.getValue() >= ageMin && entry.getValue() <= ageMax){
            if(!mapByAge.containsKey(entry.getValue())){
               mapByAge.put(entry.getValue(),entry.getKey());
            } else {
               mapByAge.put(entry.getValue(),mapByAge.get(entry.getValue()) + " and " + entry.getKey());
            }
         }
      }
      return mapByAge; 
   }
}