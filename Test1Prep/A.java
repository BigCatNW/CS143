class A{
   public A(){
      System.out.println("I am A");
   }
}

class B extends A{
   public void B() {
      System.out.println("I am B");
   }

}

class C extends B{
   public C() {
      System.out.println("I am C");
   }
}