/**
 * Maze class and representation for the client/opponent.
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
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MazeClient extends JFrame{
	
	//Variables
	public static final int ROWS = 20;
	public static final int COLUMNS = 20;
	public static final int WINDOW_SIZE = 25;
	private static int _map[][] = new int[COLUMNS][ROWS];
	private Thread clientThread;
	private Player _player, _player2;//_player is Client, _player2 is Server
	private Socket serverSocket;
	private String port;
	private String ip;
	private static BufferedReader in;
	private static PrintWriter out;

	//Constructor
	public MazeClient(String str, String port, String userName, String ip, String type){
		this.loadMap(str);
		this.setResizable(false);
		this.setSize((COLUMNS*WINDOW_SIZE)+50, (ROWS*WINDOW_SIZE)+70);
		this.setTitle("A-Maze-Ing. " + " IP: " + ip + ". PORT: " + port + ". User Name: " + userName + ". Type: " + type);
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.port = port;
		this.ip = ip;

		this.connectToServer();//try to connect to the server

		this.addKeyListener(new KeyListener(){
			
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				
				//Player movement
				//Up Key
				if(key == KeyEvent.VK_UP){
					if(_player.moveUp(false)){//only if they're allowed to move
						out.println(_player.get_x() + "," + _player.get_y());
					}

				}
				//Left Key
				if(key == KeyEvent.VK_LEFT){
					if(_player.moveLeft(false)){
						out.println(_player.get_x() + "," + _player.get_y());
					}
				}
				//Down Key
				if(key == KeyEvent.VK_DOWN){
					if(_player.moveDown(false)){
						out.println(_player.get_x() + "," + _player.get_y());
					}	
				}
				//Right Key
				if(key == KeyEvent.VK_RIGHT){
					if(_player.moveRight(false)){
						out.println(_player.get_x() + "," + _player.get_y());
					}	
				}
				//if client gets to the server position first, client wins
				if(_player2.getInitialX() == _player.get_x() && _player2.get_y() == _player2.getInitialY()){
					JOptionPane.showMessageDialog(null, "Client Wins", "End Game", JOptionPane.INFORMATION_MESSAGE);
					out.println("LOST");
					if(in != null && out != null){
						try {
							if(!serverSocket.isClosed()) serverSocket.close();
							in.close();
							out.close();
						} catch (IOException e1) {e1.printStackTrace();}
						System.exit(0);
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});

		//Exit game when window is closed
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.setLocationRelativeTo(null);

		//Create player
		_player = new Player();
		_player.setVisible(true);
		_player.setColor(Color.BLUE);
		this.add(_player);

		//Create player 2
		_player2 = new Player();
		_player2.setVisible(true);
		_player2.setColor(Color.RED);
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
						//client player
						_player.setInitialX(12);
						_player.setInitialY(19);
						_player.setLocation(498,325);
						_player.set_y(12);
						_player.set_x(19);

						//server player 2
						_player2.setInitialX(0);
						_player2.setInitialY(5);
						_player2.setLocation((x*WINDOW_SIZE)+23, (y*WINDOW_SIZE)+25);
						_player2.set_y(y);
					}
				}
				tile.setVisible(true);
				this.add(tile);
			}
		}
		this.setVisible(true);
	}

	/**
	 * This method loads the map from the map.map file
	 * @param str: location of the file
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
	 * Client tries to connect to server
	 */
	public void connectToServer(){
		try {serverSocket = new Socket(ip, 2020);}
		catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Connection refused. Server must be off.", "Socket Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		if(serverSocket != null){//if you got the socket
			try {
				in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				out = new PrintWriter(serverSocket.getOutputStream(), true);
			} catch (IOException e) {e.printStackTrace();}
		}

		//Updates game on both screens
		Runnable r = new Runnable(){
			@Override
			public void run() {
				// Process all messages from server
				while (!serverSocket.isClosed()) {
					String line = null;
					try {
						if(in != null){
							line = in.readLine();

							//if lost is received, means that client loses and server wins
							if (line.equals("LOST")) {
								JOptionPane.showMessageDialog(null,"Client Lost", "End Game",
										JOptionPane.INFORMATION_MESSAGE);
								if(!serverSocket.isClosed())serverSocket.close();
								in.close();
								out.close();
								System.exit(0);
							} 

							String xPosStr = line.substring(0, line.indexOf(','));
							String yPosStr = line.substring(line.indexOf(',')+1,line.length());

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
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}	
		};
		clientThread = new Thread(r);
		clientThread.start();//start server
	}
}