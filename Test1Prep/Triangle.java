class Triangle implements Shape{
   int sides = 3;
   double side1, side2, side3;
   public Triangle(double side1, double side2, double side3){
      this.side1 = side1;
      this.side2 = side2;
      this.side3 = side3;
   }
   public String getDescription(){
      return "A polygon with 3 sides.";
   }
   public double calcArea(){
      return (this.side1 + this.side2 + this.side3) / 2;
   }
}