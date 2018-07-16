package com.mochat.netty.constant;

public class Test {  
    public static void main(String[] args) {  
        java.util.Timer t = new java.util.Timer();  
   
        t.schedule(new Task(),0);  
        
        System.out.println("---------------");
    }  
}  
   
class Task extends java.util.TimerTask {  
  
    @Override  
    public void run() {  
            System.out.println(System.nanoTime());  
    }  
}