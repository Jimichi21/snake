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
		idSala = salasIds.getAndIncrement();

	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		try {

			String payload = message.getPayload();
			
			JSONObject json = new JSONObject(payload);
			
			String tipo=json.getString("type");
			
			switch (tipo){
			
			case "user":
			
				
			    String nombre = json.getString("user");
			    System.out.println(nombre);
			    s = new Snake(id, session, nombre);
			    System.out.println("Nombre de usuario "+nombre);
			    session.getAttributes().put(SNAKE_ATT, s);

			    snakeGame.addSnake(s);

			    StringBuilder sb = new StringBuilder();
			    for (Snake snake : snakeGame.getSnakes()) {   
			     sb.append(String.format("{\"id\": %d, \"color\": \"%s\",\"nombre\":\"%s\"}", snake.getId(), snake.getHexColor(),nombre));
			     sb.append(',');
			    }
			    sb.deleteCharAt(sb.length()-1);
			    String msg = String.format("{\"type\": \"join\",\"data\":[%s]}", sb.toString());
			    
			    snakeGame.broadcast(msg);
				
			break;
				
			case "direction":
				Snake s = (Snake) session.getAttributes().get(SNAKE_ATT);
				String aux=json.getString("direction");
				System.out.println("-------------------------------------->"+aux);
				Direction d = Direction.valueOf(aux.toUpperCase());
				s.setDirection(d);
				return;
			
			
			case "ping":
			return;
			
			case "sala":
				 	String nom = json.getString("sala");
				    System.out.println(nom);
				    sal = new Sala(id, nom);
				    System.out.println("Nombre de sala "+nom);
				    //session.getAttributes().put(SNAKE_ATT, s);

				    snakeGame.addSala(sal);

				    String msn = String.format("{\"type\": \"sala\",\"data\":\"%s\"}", nom);
				    
				    snakeGame.broadcast(msn);
			
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
		snakeGame.broadcast(msg);
	}

}
