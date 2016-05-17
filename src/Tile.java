import javax.swing.JPanel;

/**
 * Tile Class
 * Created by Janine Jay, Melkis Espinal, Ian Jacobs and Ally Colisto
 */

public class Tile extends JPanel{
    
	//Variables
	int _x, _y;
    boolean _isWall = true;
    
    //Constructor
    public Tile(int x, int y){
        this._x = x;
        this._y = y;
    }
    
    //Setter method
    public void setWall(boolean isWall){
        this._isWall = isWall;
    }
}