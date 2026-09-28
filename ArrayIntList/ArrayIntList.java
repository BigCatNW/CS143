public class ArrayIntList implements IntList{
   private int[] data; // array of integers
   private int size;   // current number of elements in the list

   public static final int CAPACITY = 20;

   // Constructor
   ArrayIntList() {
      data = new int[CAPACITY];
      size = 0;
   }

   // mutators
   /// pre: size of list < length of data array (CAPACITY)
   // post: adds value to the next sequential index of data[]
   public void add(int value){
      checkCapacity(size + 1);
      data[size] = value;
      size++;
   }
   
   // pre: size of list < length of data array (CAPACITY) and 0 <= index < size
   // post: inserts value at index and moves values after index to the right
   public void add(int index, int value) {
      checkCapacity(size + 1);
      checkIndexAdd(index);
      for(int i = size; i > index; i--) {
         data[i] = data[i - 1];
      }
      data[index] = value;
      size++;
   }
   
   // pre: index is within the bounds of the list (0 <= index < size)
   // post: value at index is removed, trailing values are shifted to the left. 
   public void remove(int index) {
      checkIndexRemove(index);
      for(int i = index; i < size - 1; i++) {
         data[i] = data[i + 1];
      }
      size--;
   }
   
   // accessors
   // pre: none
   // post: returns a string representing the values in the ArrayIntList
   public String toString(){
      String str = "[";
      for (int i = 0; i < size; i++)  {
         str = str + data[i];
         if (i < size - 1){
            str = str + ",";
         }
      }
      str = str + "]";
      return str; 
   }
	
   // pre: none
   // post: returns the size(number of elements within) of the ArrayIntList
   public int size() {
      return size;
   }
   
   // pre: index must be within the bounds of the ArrayIntList
   // post: retuns the value at the specified index
   public int get(int index) {
      checkIndexAdd(index);
      return data[index];
   }
   
   // pre: value must be an integer
   // post: returns the index of the first instance of value or -1 if it not in the list 
   public int indexOf(int value) {
      for(int i = 0; i < size; i++) {
         if(data[i] == value) {
            return i;
         }
      }
      return -1;
   }

   // post: throws an IndexOutOfBoundsException if the given index is
   //       not a legal index of the current list plus 1
   private void checkIndexAdd(int index) {
      if (index < 0 || index > size) {
         System.out.println("MY Error");
         throw new IndexOutOfBoundsException("index: " + index);
      }
   }

   // post: throws an IndexOutOfBoundsException if the given index is
   //       not a legal index of the current list
   private void checkIndexRemove(int index) {
      if (index < 0 || index >= size) {
         System.out.println("MY Error");
         throw new IndexOutOfBoundsException("index: " + index);
      }
   }

   // post: checks that the underlying array has the given capacity,
   //       throwing an IllegalStateException if it does not
   private void checkCapacity(int capacity) {
      if (capacity > data.length) {
         System.out.println("MY Error");
         throw new IllegalStateException("would exceed list capacity");
      }
   }
}