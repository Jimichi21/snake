package es.codeurjc.em.snake;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
public class Sala {
	int id; //identificador de la sala
	private ConcurrentHashMap<Integer, Snake> snakes = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Snake> snakesEspera = new ConcurrentHashMap<>();
	 Semaphore contador;
	boolean partida_empezada=false;
	
	 
	String nombre;
	int idCreador;
	
	Sala(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
		contador=new Semaphore(4, true);
		
	}
	void EliminarJugador(Snake jugador){
		
		snakes.remove(Integer.valueOf(jugador.getId()));
		contador.release();
		
	}
	 boolean AñadirJugador(Snake jugador) throws Exception{
		  
		   
		   	  if(contador.availablePermits()==0){
		   		  String m="{\"type\": \"espera\"}";
		   		  jugador.sendMessage(m);
		   		  //jugador.getsession().wait(5000);
		   	  }
		   	  if(contador.tryAcquire(5,TimeUnit.SECONDS) ){
		      //if(contador.tryAcquire() ){
		    	  snakes.put(jugador.getId(), jugador);
		    	  return true;
		      }else{
		    	  return false;
		      }
		   
	 }

		 
		 
		 
	
	int getId(){
		return this.id;
	}
	String getName(){
		return this.nombre;
	}
	ConcurrentHashMap<Integer,Snake> getLista(){
		return this.snakes;
	}
	ConcurrentHashMap<Integer,Snake> getEspera(){
		return this.snakesEspera;
	}
	boolean SerpientesEsperando(){
		return snakesEspera.isEmpty();
	}
}
