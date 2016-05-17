/**
 * Player representation.
 * Last Edited: 05/01/2016
 * @author Melkis Espinal, Ian Jacobs, Ally Colisto, and Janine Jay
 */
import java.awt.Color;
import javax.swing.JPanel;

public class Player extends JPanel{
	
	//Variables
	private int _x, _y;//array locations
	private int initialX = 0;//initial x
	private int initialY = 0;//initial y

	/**
	 * Constructor
	 */
	public Player() {
		this.setSize(MazeServer.WINDOW_SIZE, MazeServer.WINDOW_SIZE);
	}

	//setter
	public void setColor(Color c){
		this.setBackground(c);
	}

	/**
	 * Moves the player left in the maze.
	 * @param isServer: if is the server moving
	 * @return: whether it was able to move or not (if there is a wall in next position)
	 */
	public boolean moveLeft(Boolean isServer) {
		if(isServer){
			if(_x > 0 && MazeServer.get_map()[_x-1][_y] == 1){
				this.setLocation(this.getX()-25, this.getY());
				_x--;
				return true;
			}
			return false;
		}
		else{
			if(_x > 0 && MazeClient.get_map()[_x-1][_y] == 1){    			
				this.setLocation(this.getX()-25, this.getY());
				_x--;
				return true;
			}
			return false;
		}
	}

	/**
	 * Moves the player right in the maze.
	 * @param isServer: if is the server moving
	 * @return: whether it was able to move or not (if there is a wall in next position)
	 */
	public boolean moveRight(Boolean isServer) {
		if(isServer){
			if(_x < MazeServer.COLUMNS-1 && MazeServer.get_map()[_x+1][_y] == 1){
				this.setLocation(this.getX()+25, this.getY());
				_x++;
				return true;
			}
			return false;
		}
		else{
			if(_x < MazeClient.COLUMNS-1 && MazeClient.get_map()[_x+1][_y] == 1){
				this.setLocation(this.getX()+25, this.getY());
				_x++;
				return true;
			}
			return false;
		}
	}

	/**
	 * Moves the player up in the maze.
	 * @param isServer: if is the server moving
	 * @return: whether it was able to move or not (if there is a wall in next position)
	 */
	public boolean moveUp(Boolean isServer) {
		if(isServer){
			if(_y > 0 && MazeServer.get_map()[_x][_y-1] == 1){
				this.setLocation(this.getX(), this.getY()-25);
				_y--;
				return true;
			}
			return false;
		}
		else{
			if(_y > 0 && MazeClient.get_map()[_x][_y-1] == 1){
				this.setLocation(this.getX(), this.getY()-25);
				_y--;
				return true;
			}
			return false;
		}
	}

	/**
	 * Moves the player down in the maze.
	 * @param isServer: if is the server moving
	 * @return: whether it was able to move or not (if there is a wall in next position)
	 */
	public boolean moveDown(Boolean isServer) {
		if(isServer){
			if(_y < MazeServer.ROWS-1 && MazeServer.get_map()[_x][_y+1] == 1){
				this.setLocation(this.getX(), this.getY()+25);
				_y++;
				return true;
			}
			return false;
		}
		else{
			if(_y < MazeClient.ROWS-1 && MazeClient.get_map()[_x][_y+1] == 1){
				this.setLocation(this.getX(), this.getY()+25);
				_y++;
				return true;
			}
			return false;
		}
	}

	//getters
	public int get_x() {return _x;}
	public int get_y() {return _y;}
	public void set_x(int _x) {this._x = _x;}
	public void set_y(int _y) {this._y = _y;}
	public int getInitialX() {return initialX;}

	//setters
	public void setInitialX(int initialX) {this.initialX = initialX;}
	public int getInitialY() {return initialY;}
	public void setInitialY(int initialY) {this.initialY = initialY;}
}