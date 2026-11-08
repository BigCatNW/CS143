// Matthew Cook
// Week 7 HW #4: HTML Checker
// 11/7/2026

import java.util.*;

public class HTMLManager {
   private Queue<HTMLTag> tags;
   // pre: a valid queue of HTML: tags
   // post: creates the tags class property Queue<HTMLTag> variable
   public HTMLManager(Queue<HTMLTag> tags){
      if(tags == null){
         throw new IllegalArgumentException("tags parameter cannot be null");
      } else{
         this.tags = tags;
      }
   }
   
   // pre: none
   // post: return the tags class property Queue<HTMLTag> variable
   public Queue<HTMLTag> getTags(){
      return this.tags;
   }
   
   //pre: a tags class property Queue<HTMLTag> variable
   //post: returns a the queue in a string format
   public String toString(){
      String str = "";
      for(HTMLTag tag: this.tags){
         str = str + tag.toString().trim();
      }
      return str;
   }
   
   //pre: a tags class property Queue<HTMLTag> variable
   //post: corrects any missing opening or closing HTML tags in the tags class property Queue<HTMLTag> variable
   public void fixHTML(){
      Stack<HTMLTag> s = new Stack<>();
      Deque<HTMLTag> q = new ArrayDeque<>();
      for(HTMLTag tag: this.tags) {
         if(tag.isOpening()){
            q.add(tag);
            s.push(tag);
         } else if(tag.isSelfClosing()){
            q.add(tag);
         } else {
            if(s.isEmpty()){
            
            } else{
               if(tag.matches(s.peek())){
                  q.add(tag);
                  s.pop();
               } else {
                  q.add(s.peek().getMatching());
                  s.pop();
               }
            }
         }
      }
      for(HTMLTag tag: s){
         q.add(tag.getMatching());
      }
      this.tags.clear();
      this.tags.addAll(q);
   }
}

/*
  ----jGRASP exec: java -ea HTMLChecker
 ===============================
 Processing tests/test1.html...
 ===============================
 HTML: <b><i><br /></b></i>
 Checking HTML for errors...
 HTML after fix: <b><i><br /></i></b>
 ----> Result matches Expected Output!
 
 ===============================
 Processing tests/test2.html...
 ===============================
 HTML: <a><a><a></a>
 Checking HTML for errors...
 HTML after fix: <a><a><a></a></a></a>
 ----> Result matches Expected Output!
 
 ===============================
 Processing tests/test3.html...
 ===============================
 HTML: <br /></p></p>
 Checking HTML for errors...
 HTML after fix: <br />
 ----> Result matches Expected Output!
 
 ===============================
 Processing tests/test4.html...
 ===============================
 HTML: <div><div><ul><li></li><li></li><li></ul></div>
 Checking HTML for errors...
 HTML after fix: <div><div><ul><li></li><li></li><li></li></ul></div></div>
 ----> Result matches Expected Output!
 
 ===============================
 Processing tests/test5.html...
 ===============================
 HTML: <div><h1></h1><div><img /><p><br /><br /><br /></div></div></table>
 Checking HTML for errors...
 HTML after fix: <div><h1></h1><div><img /><p><br /><br /><br /></p></div></div>
 ----> Result matches Expected Output!
 
 ===============================
         All tests passed!
 ===============================
 
  ----jGRASP: Operation complete.
  */