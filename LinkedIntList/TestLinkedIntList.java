public class TestLinkedIntList{
   public void main(String[] args){
      LinkedIntList list3 = new LinkedIntList();
      list3.add(0);
      list3.add(1);
      list3.add(2);
      list3.add(3);
      list3.add(4);
      list3.add(5);
      System.out.println(list3);
      list3.add(1,100);
      System.out.println(list3.get(1));
      System.out.println(list3.get(2));
      list3.add(1,200);
      System.out.println(list3.get(1));
      System.out.println(list3.get(2));
      System.out.println(list3.get(3));
      list3.add(0,300);
      System.out.println(list3.get(0));
      System.out.println(list3.get(1));
      System.out.println(list3.get(2));
   }
   
   

}