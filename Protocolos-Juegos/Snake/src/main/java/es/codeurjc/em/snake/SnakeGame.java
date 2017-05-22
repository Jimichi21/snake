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

		
		
		
		int count = numSnakes.getAndIncrement();

		if (count == 0) {
		
			
			startTimer(snake.getSala());
		}
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

	public void removeSnake(Snake snake) {

		snakes.remove(Integer.valueOf(snake.getId()));

		int count = numSnakes.decrementAndGet();

		if (count == 0) {
			stopTimer();
		}
	}

	private void tick(Sala sala) {

		try {

			   for (Snake snake : sala.getLista().values()) {
			    snake.update(sala.getLista().values());
			   }

			   StringBuilder sb = new StringBuilder();
			   for (Snake snake : sala.getLista().values()) {
				  
			    sb.append(getLocationsJson(snake));
			    sb.append(',');
			   }
			   sb.deleteCharAt(sb.length()-1);
			   String msg = String.format("{\"type\": \"update\", \"data\" : [%s]}", sb.toString());

			   broadcast(msg,sala);

			  } catch (Throwable ex) {
			   System.err.println("Exception processing tick()");
			   ex.printStackTrace(System.err);
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

	public void broadcast(String message, Sala sala) throws Exception {

		
		
		
		for (Snake snake : sala.getLista().values()) {
			try {
				 System.out.println("------------->"+snake.getName());
				System.out.println("Sending message " + message + " to " + snake.getId());
				snake.sendMessage(message);

			} catch (Throwable ex) {
				System.err.println("Execption sending message to snake " + snake.getId());
				ex.printStackTrace(System.err);
				removeSnake(snake);
			}
		}
	}

	public void startTimer(Sala sala) {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> tick(sala), TICK_DELAY, TICK_DELAY, TimeUnit.MILLISECONDS);
	}

	public void stopTimer() {
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}
}
