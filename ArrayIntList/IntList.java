public interface IntList {
	// Mutators
   public void add(int value);
   public void add(int index, int value);
   public void remove(int index);
   
   // Accessors
   public String toString();
	
   public int size();
   public int get(int index);
   public int indexOf(int value);
	
   
	
}