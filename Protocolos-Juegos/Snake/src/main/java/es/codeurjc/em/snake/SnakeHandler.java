package es.codeurjc.em.snake;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.json.JSONObject;

public class SnakeHandler extends TextWebSocketHandler {

	private static final String SNAKE_ATT = "snake";

	private AtomicInteger snakeIds = new AtomicInteger(0);
	private AtomicInteger salasIds = new AtomicInteger(0);

	private SnakeGame snakeGame = new SnakeGame();
    
	Snake s;
	Sala sal;
	
	int idSala;
	int id;
	String user_name;
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		id = snakeIds.getAndIncrement();
		

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		try {

			String payload = message.getPayload();
			
			JSONObject json = new JSONObject(payload);
			
			String tipo=json.getString("type");
			
			switch (tipo){
			
			case "user":
			
				String msg; //Tendrá el mensaje de confirmación o no de que la sala existe o no
			    String nombre = json.getString("user");
			    System.out.println(nombre);
			    s = new Snake(id, session, nombre);
			    System.out.println("Nombre de usuario "+nombre);
			    
			    
			    if(json.getString("ComandoSala").equals("Crear")){
			    	
			    	//Si no existe la sala la crea
			    	if(!snakeGame.comprobarSala(json.getString("Sala"))){
				    	idSala = salasIds.getAndIncrement();
					 	String nom = json.getString("Sala");
					    System.out.println(nom);
					   
					    sal = new Sala(id, nom);
					    sal.AñadirJugador(s);
					    s.setSala(sal);
					    System.out.println("Nombre de sala "+nom);
					    
					    snakeGame.addSala(sal);
			    	
					    msg="{\"type\": \"Okcrear\",\"data\":\"Ok\"}";
					    s.sendMessage(msg);
			    	
			    }else{
			    	
			    	msg="{\"type\": \"Okcrear\",\"data\":\"NotOk\"}";
			    	s.sendMessage(msg);
			    	return;
			    	}
			    }else{
			    	
			    	//si existe la sala (Se tiene que devolver la sala de la lista de salas)
					if(snakeGame.comprobarSala(json.getString("Sala"))){
						
						sal=snakeGame.getSala(json.getString("Sala"));
						
						
						if(sal.comprobarJugador()){
						sal.AñadirJugador(s);
						s.setSala(sal);
				    
						msg="{\"type\": \"Okunir\",\"data\":\"Ok\"}";
				    	s.sendMessage(msg);
						}else{
							msg="{\"type\": \"Okunir\",\"data\":\"NotOk\",\"info\":\"Error, Sala llena esperando 5 segundos\"}";
							s.sendMessage(msg);
							 wait(5000);
							
						}
				    }
				    else{
				    	
				    	msg="{\"type\": \"Okunir\",\"data\":\"NotOk\",\"info\":\"Error, la sala introducida no existe\"}";
				    	s.sendMessage(msg);
				    	
				    	return;
				    }
			    	
			    }
			    session.getAttributes().put(SNAKE_ATT, s);
			    snakeGame.addSnake(s);

			    StringBuilder sb = new StringBuilder();
			    for (Snake snake : sal.getLista().values()) {   
			     sb.append(String.format("{\"id\": %d, \"color\": \"%s\",\"nombre\":\"%s\"}", snake.getId(), snake.getHexColor(),nombre));
			     sb.append(',');
			    }
			    sb.deleteCharAt(sb.length()-1);
			    String msg2 = String.format("{\"type\": \"join\",\"data\":[%s]}", sb.toString());
			    
			    snakeGame.broadcast(msg2, s.getSala());
				
			break;
				
			case "direction":
				Snake sn = (Snake) session.getAttributes().get(SNAKE_ATT);
				String aux=json.getString("direction");
				Direction d = Direction.valueOf(aux.toUpperCase());
				sn.setDirection(d);
				return;
			
			
			case "ping":
			return;
			
			case "cancelar":
				    System.out.println("-------------------------------\n---------------------\n cancelar");
			
			msg="{\"type\": \"cancelar\",\"info\": \"Espera cancelada\"}";
	    	s.sendMessage(msg);
			}	

			System.out.println(payload);

		} catch (Exception e) {
			System.err.println("Exception processing message " + message.getPayload());
			e.printStackTrace(System.err);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		System.out.println("Connection closed. Session " + session.getId());

		Snake s = (Snake) session.getAttributes().get(SNAKE_ATT);

		snakeGame.removeSnake(s);

		String msg = String.format("{\"type\": \"leave\", \"id\": %d,\"nombre\":\"%s\"}", s.getId(),s.getName());
		System.out.println("-------------------------------->"+s.getId());
		Snake sn=(Snake) session.getAttributes().get(SNAKE_ATT);
	    snakeGame.broadcast(msg, sn.getSala());
	}

}
