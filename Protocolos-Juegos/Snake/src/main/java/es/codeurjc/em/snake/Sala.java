package es.codeurjc.em.snake;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Sala {
	int id; //identificador de la sala
	private ConcurrentHashMap<Integer, Snake> snakes = new ConcurrentHashMap<>();
	private AtomicInteger contador = new AtomicInteger(0);//numero de jugadores en la sala
	String nombre;
	int idCreador;
	
	Sala(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
	
		
	}
	
	void AñadirJugador(Snake jugador){
		
			snakes.put(jugador.getId(), jugador);
		    contador.getAndIncrement();
			
		}
		
		
	
	
	
	
	boolean comprobarJugador(){
		
		if(contador.get()<=3){
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
}
