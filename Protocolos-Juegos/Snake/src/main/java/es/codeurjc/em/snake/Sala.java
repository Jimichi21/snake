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
	private int contadorComida = 0;
	 
	String nombre;
	int idCreador;
	private Comida comida;
	
	Sala(int id, String nombre){
		this.id = id;
		this.nombre = nombre;
		contador=new Semaphore(4, true);
		setComida(null);
	}
	void EliminarJugador(Snake jugador){
		
		snakes.remove(Integer.valueOf(jugador.getId()));
		contador.release();
		
	}
	boolean AñadirJugador(Snake jugador) throws Exception{
		   
	     
        if(contador.availablePermits()==0){
         String m="{\"type\": \"espera\"}";
         jugador.sendMessage(m);
         
        }

         if(contador.tryAcquire(5000,TimeUnit.MILLISECONDS)){
          
          snakes.put(jugador.getId(), jugador);
          return true;
       
         }
          return false;

         }
	 void Cancelar(){
		 
	 }

	synchronized Snake getCreador(){
		return snakes.get(idCreador);
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
	public Comida getComida() {
		return comida;
	}
	public void setComida(Comida comida) {
		this.comida = comida;
	}
	public int getContadorComida() {
		return contadorComida;
	}
	public void setContadorComida(int contadorComida) {
		this.contadorComida = contadorComida;
	}
	
	
}
