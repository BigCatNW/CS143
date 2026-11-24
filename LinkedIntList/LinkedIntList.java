// Matthew Cook
// CS 143
// HW 5: LinkedIntList

public class LinkedIntList implements IntList{
   private int size;
   private Node start;
   
   // pre: a valid value
   // post: adds value to the end of the list   
   public void add(int value){
      if(this.start == null){
         Node newNode = new Node();
         newNode.data = value;
         start = newNode;
         this.size++;
      } else{
         Node current = this.start;
         while(current.next != null){
            current = current.next;
         }
         Node newNode = new Node();
         newNode.data = value;
         current.next = newNode;
         this.size++;
      }
   }
   
   // pre: none
   // post: converts the list to a string
   public String toString(){
      if(this.start == null){
         return "[]";
      }
      String str = "[" + start.data;
      Node current = start;
      while(current.next != null){
         str = str + ", " + current.next.data;
         current = current.next;
      }
      str = str + "]";
      return str;
   }
	
   // pre: none
   // post: returns the size of the list
   public int size(){
      return size;
   }
   
   // pre: a valid index
   // post: returns the value at the index
   public int get(int index){
      checkIndex(index);
      Node temp = start;
      for(int i = 0; i < index; i++) {
         temp = temp.next;
      }
      return temp.data;
   }
   
   // pre: an integer value
   // post: returns the first index instance of the value or -1 if it is not in the list
   public int indexOf(int value){
      if(start == null){
         return -1;
      } 
      Node current = start;
      for(int i = 0; i < size; i++){
         if(current.data == value){
            return i;
         }
         current = current.next;
      }
      return -1;
   }
	
   // pre: a valid index within the list and an integer value
   // post: inserts the value at the specified index
   public void add(int index, int value){
      if(this.size == 0){
         add(value);
      } else if(index == 0){
         Node newNode = new Node();
         newNode.data = value;
         newNode.next = start;
         start = newNode;
      } else {
         checkIndexAddAtIndex(index);
         Node current = start;
         for(int i = 0; i < index - 1; i++){
            current = current.next;
         }
         Node newNode = new Node();
         newNode.data = value;
         newNode.next = current.next;
         current.next = newNode;
         size++;
      }
   }
   
   // pre: a valid index within the list
   // post: removes the value at the index
	public void remove(int index){
      checkIndexRemove(index);
      if(index == 0){
         if(size == 1){
            start = null;
            size--;
         } else {
            start = start.next;
            size--;
         }
      } else {
         Node current = start;
         for(int i = 0; i < index - 1; i++){
            current = current.next;
         }
         if(current.next.next == null){
            current.next = null;
            size--;
         } else {
            current.next = current.next.next;
            size--;
         }
      }
      
   }
   
   // pre: a valid LinkedIntList parameter
   // post: returns true if the lists are equal, otherwise false
   public boolean equals(LinkedIntList object){
      if(!(object instanceof LinkedIntList)){
         throw new IllegalArgumentException("Expected a LinkedIntList, but received a "+ (object == null ? "null" : object.getClass().getName()));
      }
      boolean match;
      if(object.size == this.size){
         match = true;
      } else {
         match = false;
      }
      Node thisCurrent = this.start;
      Node thatCurrent = object.start;
      for(int i = 0; i < this.size; i++){
         match = match && (thisCurrent.data == thatCurrent.data);
         thisCurrent = thisCurrent.next;
         thatCurrent = thatCurrent.next;
      }
      return match;
   }
   
   // pre: a valid LinkedIntList
   // post: inserts the list into this list at the specified index
   public void insertList(int index, LinkedIntList list){
      checkIndex(index);
      if(index == 0){
         if(this.size == 0){
            this.start = list.start;
            this.size += list.size;
         } else {
            Node end = this.start.next;
            this.start.next = list.start;
            Node current = this.start;
            while(current.next != null){
               current = current.next;
            }
            current.next = end;
            this.size += list.size;
         } 
      } else {
         Node current = this.start;
         for(int i = 0; i < index - 1; i++){
            current = current.next;
         }
         Node end = current.next;
         current.next = list.start;
         while(current.next != null){
            current = current.next;
         }
         current.next = end;
         this.size += list.size;
      }
      
   }
   
   // helper functions
   // pre: none
   // post: throws error if index out of bounds
   public void checkIndexRemove(int index){
      if (index < 0 || index >= size || size == 0) {
         throw new IndexOutOfBoundsException("index: " + index);
      }
   }
   // pre: none
   // post: throws error if index out of bounds
   public void checkIndex(int index){
      if (index < 0 || index > size) {
         throw new IndexOutOfBoundsException("index: " + index);
      }
   }

   // pre: none
   // post: throws error if index out of bounds   
   public void checkIndexAddAtIndex(int index){
      if (index < 0 || index > size + 1) {
         throw new IndexOutOfBoundsException("index: " + index);
      }
   }
}