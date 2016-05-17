/**
 * Maze class and representation for the Server.
 * Last Edited: 05/01/2016
 * @author Melkis Espinal, Ian Jacobs, Ally Colisto, and Janine Jay
 */
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MazeServer extends JFrame{
	
	//Variables
	public static final int ROWS = 20;
	public static final int COLUMNS = 20;
	public static final int WINDOW_SIZE = 25;
	private static int _map[][] = new int[COLUMNS][ROWS];
	private static int _endLoc;
	private Thread serverThread;
	private Player _player, _player2;//_player is Server, _player2 is Client
	private ServerSocket serverSocket;
	private String port;
	private PrintWriter out;
	private BufferedReader in;

	//Constructor
	public MazeServer(String str, String port, String userName, String ip, String type){
		this.port = port;
		loadMap(str);
		this.setResizable(false);
		this.setSize((COLUMNS*WINDOW_SIZE)+50, (ROWS*WINDOW_SIZE)+70);
		this.setTitle("A-Maze-Ing. " + " IP: " + ip + ". PORT: " + port + ". User Name: " + userName + " Type: " + type);
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setUp(str,port,userName,ip,type);
		this.setVisible(true);
	}

	/**
	 * This method runs all the server side things.
	 */
	public void setUp(String str, String port, String userName, String ip, String type){
		this.startServer();

		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if(in != null && out != null){
					try {
						serverSocket.close();
						in.close();
						out.close();
					} catch (IOException e1) {e1.printStackTrace();}
				}
				System.exit(0);
			}
		});

		this.setLocationRelativeTo(null);

		//Create player
		_player = new Player();
		_player.setVisible(true);
		_player.setColor(Color.RED);
		this.add(_player);

		//Create opposite player
		_player2 = new Player();
		_player2.setVisible(true);
		_player2.setColor(Color.BLUE);
		this.add(_player2);

		//Color map
		for(int y = 0; y < COLUMNS; y++){
			for(int x = 0; x < ROWS; x++){
				Tile tile = new Tile(x, y);
				tile.setSize(WINDOW_SIZE, WINDOW_SIZE);
				tile.setLocation((x*WINDOW_SIZE)+23, (y*WINDOW_SIZE)+25);
				if(_map[x][y] == 0){
					tile.setBackground(Color.green);
				}else{
					tile.setBackground(Color.LIGHT_GRAY);
					tile.setWall(false);
					if(x == 0){
						//player 1 starting position
						_player.setInitialX(0);
						_player.setInitialY(5);
						_player.setLocation((x*WINDOW_SIZE)+23, (y*WINDOW_SIZE)+25);
						_player.set_x(0);
						_player.set_y(5);

						//player 2 starting position
						_player2.setInitialX(12);
						_player2.setInitialY(19);
						_player2.setLocation(498,325);
						_player2.set_y(12);
						_player2.set_x(19);
					}
					if(x == COLUMNS-1){
						_endLoc = y;
					}
				}
				tile.setVisible(true);
				this.add(tile);
			}
		}
	}

	/**
	 * Loads the map.
	 * @param str: location of the map
	 */
	public void loadMap(String str){
		try{
			BufferedReader br = new BufferedReader(new FileReader(str));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}

			String mapStr = sb.toString();
			int counter = 0;
			for(int y = 0; y < COLUMNS; y++){
				for(int x = 0; x < ROWS; x++){
					String mapChar = mapStr.substring(counter, counter+1);
					if(!mapChar.equals("\r\n") && !mapChar.equals("\n")&& !mapChar.equals("\r")){

						_map[x][y] = Integer.parseInt(mapChar);
					}else{
						x--;
					}
					counter++;
				}
			}
			br.close();
		}catch(Exception e){e.printStackTrace();}
	}
	//getter
	public static int[][] get_map() {
		return _map;
	}

	/**
	 * Thread class for incoming client.
	 */
	private class ThreadClass extends Thread{
		//instance variable
		private Socket socket;

		/**
		 * Constructor.
		 * @param socket: socket from client.
		 */
		public ThreadClass(Socket socket){this.socket = socket;}

		@Override
		public void run(){
			//add the key listener when the other player is connected so 
			//the server doesn't get an advantage of moving while client connects.
			addKeyListener(new KeyListener(){
				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();

					//Player movement
					//Up Key
					if(key == KeyEvent.VK_UP){
						if(_player.moveUp(true)){
							out.println(_player.get_x() + "," + _player.get_y());
						}
					}
					//Left Key
					if(key == KeyEvent.VK_LEFT){
						if(_player.moveLeft(true)){
							out.println(_player.get_x() + "," + _player.get_y());
						}
					}
					//Down Key
					if(key == KeyEvent.VK_DOWN){
						if(_player.moveDown(true)){
							out.println(_player.get_x() + "," + _player.get_y());
						}
					}
					//Right Key
					if(key == KeyEvent.VK_RIGHT){
						if(_player.moveRight(true)){
							out.println(_player.get_x() + "," + _player.get_y());
						}
					}
					//Server wins and it informs the client
					if(_player.get_x() == COLUMNS-1 && _player.get_y() == _endLoc){
						JOptionPane.showMessageDialog(null, "Server Wins!", "End Game", JOptionPane.INFORMATION_MESSAGE);
						out.println("LOST");
						try {
							serverSocket.close();
							in.close();
							out.close();
						} catch (IOException e1) {e1.printStackTrace();}
						System.exit(0);
					}
				}
				@Override
				public void keyReleased(KeyEvent arg0) {}
				@Override
				public void keyTyped(KeyEvent arg0) {}
			});

			try{
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				while(true){
					String input = in.readLine();
					if (input == null) {return;}

					if(input.equals("LOST")){//server loses
						JOptionPane.showMessageDialog(null,"Server Loses.", "End Game",
								JOptionPane.INFORMATION_MESSAGE);
						serverSocket.close();
						in.close();
						out.close();
						System.exit(0);
					}

					String xPosStr = "";
					String yPosStr = "";
					if(input.length() > 0){
						xPosStr = input.substring(0, input.indexOf(','));
						yPosStr = input.substring(input.indexOf(',')+1,input.length());
					}

					if(xPosStr.length() > -1 && yPosStr.length() > -1){
						int xPosNew = Integer.parseInt(xPosStr);
						int yPosNew = Integer.parseInt(yPosStr);
						int oldX = _player2.get_x();
						int oldY = _player2.get_y();

						//controls the movement of player 2 in the server side
						if((oldX - xPosNew) > 0){
							int newXPos = (int)_player2.getLocation().getX() - 25;
							_player2.set_x(xPosNew);
							_player2.set_y(yPosNew);
							_player2.setLocation(newXPos,(int)_player2.getLocation().getY());
						}
						if((oldX - xPosNew) < 0){
							int newXPos = (int)_player2.getLocation().getX() + 25;
							_player2.set_x(xPosNew);
							_player2.set_y(yPosNew);
							_player2.setLocation(newXPos,(int)_player2.getLocation().getY());
						}

						if((oldY - yPosNew) > 0){
							int newYPos = (int)_player2.getLocation().getY() - 25;
							_player2.set_x(xPosNew);
							_player2.set_y(yPosNew);
							_player2.setLocation((int)_player2.getLocation().getX(),newYPos);
						}

						if((oldY - yPosNew) < 0){
							int newYPos = (int)_player2.getLocation().getY() + 25;
							_player2.set_x(xPosNew);
							_player2.set_y(yPosNew);
							_player2.setLocation((int)_player2.getLocation().getX(),newYPos);
						}
					}
				}

			} catch (IOException e) {e.printStackTrace();} 
			finally {
				//close its socket.
				try {socket.close();} 
				catch (IOException e) {e.printStackTrace();}
			}		
		}
	}

	public void startServer(){
		try {serverSocket = new ServerSocket(Integer.parseInt(port));} 
		catch (IOException e1) {e1.printStackTrace();}
		Runnable r = new Runnable(){
			@Override
			public void run() {
				try{
					while(!serverSocket.isClosed()){
						try {new ThreadClass(serverSocket.accept()).start();} 
						catch (IOException ioe) {ioe.printStackTrace();}
					}
				} finally{
					try {serverSocket.close();} 
					catch (IOException ioe1) {ioe1.printStackTrace();}
				}
			}	
		};
		serverThread = new Thread(r);
		serverThread.start();//start server
	}
}