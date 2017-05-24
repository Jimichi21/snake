
var user;
var sala;
var comandoSala;


window.onload = function() {
	  document.getElementById('modal2').style.display = 'none';
	};
	
function actionNombre(){
	
	user = $("#texto1").val()
	
	  console.log(user);

	  document.getElementById('modal1').style.display = "none";
	  document.getElementById('button4').style.display = "none";
	  document.getElementById('modal2').style.display = "block";
		  
}
function actionCrear(){
	sala = $("#texto2").val();
	comandoSala="Crear";
	
	
	juego();
	 
	
}
function actionUnir(){
	sala = $("#texto2").val();
	comandoSala="Unir";

	
	juego();
}

function actionCancelar(){
	var mensaje={"type": "cancelar"};
	game.enviar(mensaje);
}


	
	
var Console = {};

Console.log = (function(message) {
	var console = document.getElementById('console');
	var p = document.createElement('p');
	p.style.wordWrap = 'break-word';
	p.innerHTML = message;
	console.appendChild(p);
	while (console.childNodes.length > 25) {
		console.removeChild(console.firstChild);
	}
	console.scrollTop = console.scrollHeight;
});



class Snake {

	constructor() {
		this.snakeBody = [];
		this.color = null;
	}

	draw(context) {
		for (var pos of this.snakeBody) {
			context.fillStyle = this.color;
			context.fillRect(pos.x, pos.y,
				game.gridSize, game.gridSize);
		}
	}
}

class Game {
	
	enviar(mens){
		var pack = JSON.stringify(mens);
		this.socket.send(pack);
	
	}
	
	constructor(){
		this.socket = null;
		this.fps = 30;
		this.nextFrame = null;
		this.interval = null;
		this.direction = 'none';
		this.gridSize = 10;
		
		this.skipTicks = 1000 / this.fps;
		this.nextGameTick = (new Date).getTime();
	}

	initialize() {	
	
		this.snakes = [];
		let canvas = document.getElementById('playground');
		if (!canvas.getContext) {
			Console.log('Error: 2d canvas not supported by this browser.');
			return;
		}
		
		this.context = canvas.getContext('2d');
		window.addEventListener('keydown', e => {
			
			var code = e.keyCode;
			if (code > 36 && code < 41) {
				switch (code) {
				case 37:
					if (this.direction != 'east')
						this.setDirection('west');
					break;
				case 38:
					if (this.direction != 'south')
						this.setDirection('north');
					break;
				case 39:
					if (this.direction != 'west')
						this.setDirection('east');
					break;
				case 40:
					if (this.direction != 'north')
						this.setDirection('south');
					break;
				}
			}
		}, false);
		
		this.connect();
	}

	setDirection(direction) {
		this.direction = direction;
		var aux = {"type":"direction","direction":direction};
		var mens=JSON.stringify(aux);
		this.socket.send(mens);
		Console.log('Sent: Direction ' + direction);
	}

	startGameLoop() {
	
		this.nextFrame = () => {
			requestAnimationFrame(() => this.run());
		}
		
		this.nextFrame();		
	}

	stopGameLoop() {
		this.nextFrame = null;
		if (this.interval != null) {
			clearInterval(this.interval);
		}
	}

	draw() {
		this.context.clearRect(0, 0, 640, 480);
		for (var id in this.snakes) {			
			this.snakes[id].draw(this.context);
		}
	}

	addSnake(id, color) {
		this.snakes[id] = new Snake();
		this.snakes[id].color = color;
	}

	updateSnake(id, snakeBody) {
		if (this.snakes[id]) {
			this.snakes[id].snakeBody = snakeBody;
		}
	}

	removeSnake(id) {
		this.snakes[id] = null;
		// Force GC.
		delete this.snakes[id];
	}

	run() {
	
		while ((new Date).getTime() > this.nextGameTick) {
			this.nextGameTick += this.skipTicks;
		}
		this.draw();
		if (this.nextFrame != null) {
			this.nextFrame();
		}
	}

	connect() {
		
		this.socket = new WebSocket("ws://127.0.0.1:8080/snake");

		this.socket.onopen = () => {
			
			// Socket open.. start the game loop.
			Console.log('Info: WebSocket connection opened.');
			Console.log('Info: Press an arrow key to begin.');
			
			
			
			
			//enviamos el usuario al servidor
			var aux = {"type": "user", "user": user, "ComandoSala":comandoSala,"Sala":sala};
			var mens=JSON.stringify(aux);
			this.socket.send(mens);
			
			this.startGameLoop();
			
			var aux = {"type": "ping"};
			var mens=JSON.stringify(aux);
			setInterval(() => this.socket.send(mens), 5000);
		}

		this.socket.onclose = () => {
			Console.log('Info: WebSocket closed.');
			this.stopGameLoop();
		}

		this.socket.onmessage = (message) => {

			var packet = JSON.parse(message.data);
			
			switch (packet.type) {
		      case 'update':
		       for (var i = 0; i < packet.data.length; i++) {
		        this.updateSnake(packet.data[i].id, packet.data[i].body);
		       }
		       break;
		      case 'join':
		       
		       Console.log(packet.data[0].nombre+" Ha entrado en la sala");
		       for (var j = 0; j < packet.data.length; j++) {
		        
		        this.addSnake(packet.data[j].id, packet.data[j].color,packet.data[j].nombre);
		       }
		       break;
		      case 'leave':
		       Console.log(packet.nombre+' ha dejado la partida');
		       this.removeSnake(packet.id);
		       break;
		      case 'dead':
		       Console.log('Info: Your snake is dead, bad luck!');
		       this.direction = 'none';
		       break;
		      case 'kill':
		       Console.log('Info: Head shot!');
		       break;
		       
		      case 'Okcrear': 
		       if(packet.data==='Ok'){
		        Console.log('Sala '+sala+" creada con éxito"); 
		        document.getElementById('modal2').style.display = "none";
		        
		       }else{
		        Console.log('Error, la sala '+sala+" ya existe"); 
		       }
		       break;
		       
		      case 'Okunir': 
		       if(packet.data==='Ok'){
		        
		        document.getElementById('modal2').style.display = "none";
		        
		       }else{
		        Console.log(packet.info); 
		        if(packet.info==="Error, Sala llena esperando 5 segundos"){
		         
		         document.getElementById('button4').style.display = "block";
		        }
		       }
		       break;
		      
		      case "cancelar" :
		       Console.log(packet.info);
		       document.getElementById('button4').style.display = "none";
		   }
		  }
		}
	}
		

let game=new Game();


function juego(){
game.initialize();

}

