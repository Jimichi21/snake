package es.codeurjc.em.snake;

import static org.junit.Assert.assertTrue;
import java.util.concurrent.CyclicBarrier;

import java.util.concurrent.atomic.AtomicReferenceArray;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.*;
public class SnakeTest {
  volatile int i;
 @BeforeClass
 public static void startServer(){
  Application.main(new String[]{ "--server.port=8080" });
 }
  
 @Test
 public void testConnection() throws Exception {
  
  WebSocketClient wsc = new WebSocketClient();
  wsc.connect("ws://127.0.0.1:8080/snake");
        wsc.disconnect();  
 }

 @Test
 public void testJoin() throws Exception {
  i=0;
  CyclicBarrier c=new CyclicBarrier(5);
    Executor executor = Executors.newFixedThreadPool(4);
  
  AtomicReferenceArray<String> firstMsg = new AtomicReferenceArray<String>(4);
  
  
  Runnable tarea=()->{

int id=Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length()-1)-1);
   
   
WebSocketClient wsc = new WebSocketClient();
   
wsc.onMessage((session, msg) -> {
 
 System.out.println("TestMessage: "+msg);
if(msg.contains("update")){
 firstMsg.compareAndSet(id,null, msg);
}
});

   try{
   wsc.connect("ws://127.0.0.1:8080/snake");
   
          
   System.out.println("Connected");
   
   String msg;
   System.out.println("Nombre "+Thread.currentThread().getName());
   if(i==0){
       msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Crear\",\"Sala\":\"1\"}", "Creador");
          i++;
         }else{
       
         msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Unir\",\"Sala\":\"1\"}", "Union");
         }
   wsc.sendMessage(msg);
   
   Thread.sleep(1500);
   wsc.disconnect(); 
   c.await();
   
   }catch(Exception e){
    e.printStackTrace();
   }
  
  };
  
  
  
  
  
  
  executor.execute(tarea);
 Thread.sleep(500);
  executor.execute(tarea);
  //Thread.sleep(1000);
  executor.execute(tarea);
 //Thread.sleep(1000);
  executor.execute(tarea);
  
  
  Thread.sleep(2500);
  c.await();
  for(int h=0;h<4;h++){
   
   String msg=firstMsg.get(h);
   assertTrue("The first message should contain 'update', but it is "+msg, msg.contains("update"));
  }
  
 }
 
 @Test
 public void testIniciar() throws Exception {
 
  i=0;
  CyclicBarrier c=new CyclicBarrier(3);
     Executor executor = Executors.newFixedThreadPool(2);
     
     AtomicReferenceArray<String> firstMsg = new AtomicReferenceArray<String>(3);
     
     
     Runnable tarea=()->{

   int id=Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length()-1)-1);
      
      
   WebSocketClient wsc = new WebSocketClient();
      
   wsc.onMessage((session, msg) -> {
    System.out.println("TestMessage: "+msg);
   
    if(msg.contains("iniciar")){
     
     firstMsg.compareAndSet(2,null, msg);
     
     try {
   wsc.sendMessage("{\"type\":\"Init\"}");
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
    }
    if(msg.contains("update")){
     
     firstMsg.compareAndSet(id,null, msg);
     
    }

  
   });

      try{
      wsc.connect("ws://127.0.0.1:8080/snake");
      
             
             System.out.println("Connected");
      
      String msg;
      System.out.println("Nombre "+Thread.currentThread().getName());
      if(i==0){
          msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Crear\",\"Sala\":\"1\"}", "Creador");
             i++;

            }else{
          
            msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Unir\",\"Sala\":\"1\"}", "Union");
            }
      wsc.sendMessage(msg);
      
      Thread.sleep(5000);
      wsc.disconnect(); 
      c.await();
      }catch(Exception e){
       e.printStackTrace();
      }
     
     };
     
     
     
     
     
     
     executor.execute(tarea);
     Thread.sleep(500);
     executor.execute(tarea);
    
     
     
     Thread.sleep(7000);
     c.await();
     
      
      String msg=firstMsg.get(0);
      assertTrue("The first message should contain 'update', but it is "+msg, msg.contains("update"));
      msg=firstMsg.get(1);
      assertTrue("The first message should contain 'update', but it is "+msg, msg.contains("update"));
      msg=firstMsg.get(2);
      assertTrue("The first message should contain 'update', but it is "+msg, msg.contains("iniciar"));
  
  
  
  
  
  
 
}
 @Test
 public void testFin() throws Exception{
	 i=0;
	 
	 CyclicBarrier c=new CyclicBarrier(3);
	  Executor executor = Executors.newFixedThreadPool(2);
	  
	  AtomicReferenceArray<String> firstMsg = new AtomicReferenceArray<String>(3);
	  
	  
	  Runnable tareaCreador=()->{

	int id=Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length()-1)-1);
	   
	   
	WebSocketClient wsc = new WebSocketClient();
	   
	wsc.onMessage((session, msg) -> {
	 
	 System.out.println("TestMessage: "+msg);
	if(msg.contains("leave")){
	 firstMsg.compareAndSet(2,null, msg);
	}
	if(msg.contains("update")){
		 firstMsg.compareAndSet(id,null, msg);
		}
	
	
	if(msg.contains("iniciar")){

	     try {
	   wsc.sendMessage("{\"type\":\"Init\"}");
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	}
	
	});

	   try{
	   wsc.connect("ws://127.0.0.1:8080/snake");
	   
	          
	   System.out.println("Connected");
	   
	   String msg;
	   System.out.println("Nombre "+Thread.currentThread().getName());
	   
	       msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Crear\",\"Sala\":\"1\"}", "Creador");
	         
	        
	   wsc.sendMessage(msg);
	   
	   Thread.sleep(6500);
	   wsc.disconnect(); 
	   c.await();
	   }catch(Exception e){
	    e.printStackTrace();
	   }
	  
	  };
	  
	  Runnable tareaUnir=()->{

			int id=Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length()-1)-1);
			   
			   
			WebSocketClient wsc = new WebSocketClient();
			   
			wsc.onMessage((session, msg) -> {
			 
			 System.out.println("TestMessage: "+msg);
			if(msg.contains("update")){
			 firstMsg.compareAndSet(id,null, msg);
			}
			});

			   try{
			   wsc.connect("ws://127.0.0.1:8080/snake");
			   
			          
			   System.out.println("Connected");
			   
			   String msg;
			   System.out.println("Nombre "+Thread.currentThread().getName());
			  
			       
			         msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Unir\",\"Sala\":\"1\"}", "Union");
			         
			   wsc.sendMessage(msg);
			   
			   Thread.sleep(4000);
			   wsc.disconnect(); 
			   c.await();
			   }catch(Exception e){
			    e.printStackTrace();
			   }
			  
			  };
	  
	  
	  
	  
	  executor.execute(tareaCreador);
	  Thread.sleep(500);
	  executor.execute(tareaUnir);
	

	  
	  
	  Thread.sleep(8000);
	  c.await();
	  String msg=firstMsg.get(0);
	   assertTrue("The first message should contain 'update', but it is "+msg, msg.contains("update"));
	   msg=firstMsg.get(1);
	   assertTrue("The first message should contain 'update', but it is "+msg, msg.contains("update"));
	   msg=firstMsg.get(2);
	   assertTrue("The first message should contain 'leave', but it is "+msg, msg.contains("leave"));
	 
	 
	 
	 
	 
	 
	 
	 
 }
 
}