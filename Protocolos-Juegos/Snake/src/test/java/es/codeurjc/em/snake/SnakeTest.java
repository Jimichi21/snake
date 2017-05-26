package es.codeurjc.em.snake;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.BeforeClass;
import org.junit.Test;
import java.util.concurrent.*;
public class SnakeTest {

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
	volatile int i=0;
	@Test
	public void testJoin() throws Exception {
		
		
		  Executor executor = Executors.newFixedThreadPool(4);
		
		AtomicReference<String> firstMsg = new AtomicReference<String>();
		
		
		Runnable tarea=()->{
WebSocketClient wsc = new WebSocketClient();
			
wsc.onMessage((session, msg) -> {
	System.out.println("TestMessage: "+msg);
	firstMsg.compareAndSet(null, msg);
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
	       Thread.sleep(500);
	        msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Unir\",\"Sala\":\"1\"}", "Union");
	        }
			wsc.sendMessage(msg);
			
			Thread.sleep(10000);
			wsc.disconnect();	
			
			}catch(Exception e){
				e.printStackTrace();
			}
		
		};
		
		
		
		executor.execute(tarea);
	//Thread.sleep(500);
		executor.execute(tarea);
		//Thread.sleep(1000);
		executor.execute(tarea);
	//Thread.sleep(1000);
		executor.execute(tarea);
		
		
		Thread.sleep(15000);
		
		
		
		
		
		/*
		if(nombre.equals("Bruce Lee")){
	        
	        msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Crear\",\"Sala\":\"1\"}", nombre);
	        
	        }else{
	       Thread.sleep(500);
	        msg=String.format("{\"type\": \"user\", \"user\": \"%s\", \"ComandoSala\":\"Unir\",\"Sala\":\"1\"}", nombre);
	        }
			wsc.sendMessage(msg);*/
		
        	
	}

}

