package es.codeurjc.em.snake;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collection.*;
public class SnakeGame {

	private final static long TICK_DELAY = 100;

	private ConcurrentHashMap<Integer, Snake> snakes = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<Integer, Sala> salas = new ConcurrentHashMap<>();
	
	private AtomicInteger numSnakes = new AtomicInteger();
	
	private AtomicInteger numSalas = new AtomicInteger();

	private ScheduledExecutorService scheduler;


	public void addSnake(Snake snake) {
		snakes.put(snake.getId(), snake);
		numSnakes.getAndIncrement();
	}
	
	public boolean addSala(Sala sala){
		salas.put(sala.getId(), sala);
		numSalas.getAndIncrement();
		
		
		return true;
	}
	
	//comprobar si la sala existe true si existe false si no
	public boolean comprobarSala(String sala){
		
		for(ConcurrentHashMap.Entry<Integer, Sala> entry : salas.entrySet()) {
		    String key = entry.getValue().getName();
		   if(sala.equals(key)){
			   return true;
		   }
		
		}
		
		return false;
	}
	
	public Collection<Snake> getSnakes() {
		return snakes.values();
	}

	public void removeSnake(Snake snake) throws Exception {

		
		//snakes.remove(Integer.valueOf(snake.getId()));
		snake.getSala().EliminarJugador(snake);
		int aux = snake.getSala().contador.availablePermits();
		if(aux == 4){
			//se elimina la sala
			removeSala(snake.getSala());
		}
		
		
	}
	
	void removeSala(Sala sala) throws Exception{
		salas.remove(sala.getId());
		int count = numSalas.decrementAndGet();
		if(count==0){
			//cerrar juego
			stopTimer();
			//mandar mensaje para que salga la puntuacion global del juego
			String msg = String.format("{\"type\": \"fin\"}");
			broadcast(msg, sala);
		}
	}

	private void tick() {
	
		for(Sala sal : salas.values()){
		try {
			
			if(sal.partida_empezada==true){
				
				if((sal.getComida()== null)&&(sal.getContadorComida()<=3)){
					generarComida(sal);
					sal.setContadorComida(sal.getContadorComida()+1);
					
				}
				//comprobar!!!
				if(sal.getContadorComida() > 3){
					for (Snake s : sal.getLista().values()){
						String msg = String.format("{\"type\": \"leave\", \"id\": %d,\"nombre\":\"%s\"}", s.getId(),s.getName());
						broadcast(msg, s.getSala());
						removeSnake(s);
					}
				}
				else{
					for (Snake snake : sal.getLista().values()) {
					    snake.update(sal.getLista().values());
					   }

					   StringBuilder sb = new StringBuilder();
					   for (Snake snake : sal.getLista().values()) {
						  
					    sb.append(getLocationsJson(snake));
					    sb.append(',');
					   }
					   sb.deleteCharAt(sb.length()-1);
					   String msg = String.format("{\"type\": \"update\", \"data\" : [%s]}", sb.toString());

					   broadcast(msg,sal);
				   }
				}
				
				
				  } catch (Throwable ex) {
				   System.err.println("Exception processing tick()");
				   ex.printStackTrace(System.err);
				  }
			}
				
	}

	private String getLocationsJson(Snake snake) {

		synchronized (snake) {

			StringBuilder sb = new StringBuilder();
			sb.append(String.format("{\"x\": %d, \"y\": %d}", snake.getHead().x, snake.getHead().y));
			for (Location location : snake.getTail()) {
				sb.append(",");
				sb.append(String.format("{\"x\": %d, \"y\": %d}", location.x, location.y));
			}

			return String.format("{\"id\":%d,\"body\":[%s]}", snake.getId(), sb.toString());
		}
	}
	
	
	public Sala getSala(String nombre){
		  Sala s=null;
		 
		  for(ConcurrentHashMap.Entry<Integer, Sala> entry : salas.entrySet()) {
		      String key = entry.getValue().getName();
		     if(nombre.equals(key)){
		      s=entry.getValue();
		      return s; 
		     }
		  
		 }
		  return s;
		}
	

	public void broadcast(String message, Sala sala) throws Exception {

		for (Snake snake : sala.getLista().values()) {
			try {
				 //System.out.println("------------- Serpiente:>"+snake.getName());
				 //System.out.println("-------------> Sala:"+sala.getName());
				//System.out.println("Sending message " + message + " to " + snake.getId());
				snake.sendMessage(message);

			} catch (Throwable ex) {
				System.err.println("Execption sending message to snake " + snake.getId());
				ex.printStackTrace(System.err);
				removeSnake(snake);
			}
		}
	}

	public void startTimer() {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> tick(), TICK_DELAY, TICK_DELAY, TimeUnit.MILLISECONDS);
	}

	public void stopTimer() {
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}
	public void generarComida(Sala sal) throws Exception{
		  
		  Location l=SnakeUtils.getRandomLocation();
		  Comida com=new Comida(l);
		  sal.setComida(com);
		  
		  String msg = String.format("{\"type\": \"comida\", \"x\": %d,\"y\":\"%d\"}", l.x,l.y);
		  broadcast(msg,sal);
		  
		 }
}
