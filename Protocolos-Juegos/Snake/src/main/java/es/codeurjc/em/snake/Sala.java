package es.codeurjc.em.snake;

import java.util.concurrent.ConcurrentHashMap;

public class Sala {
	int id; //identificador de la sala
	private ConcurrentHashMap<Integer, Snake> snakes = new ConcurrentHashMap<>();
	int contador = 1;//numero de jugadores en la sala
	String nombre;
	int idCreador;
	
	Sala(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
	
		
	}
	
	boolean AñadirJugador(Snake jugador){
		if(contador <= 4){
			snakes.put(jugador.getId(), jugador);
			contador++;
			return true;
		}
		else{
			return false;
		}
	}
	
	boolean Empezar(){
		return contador>=2;
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
