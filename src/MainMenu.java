/**
 * Main Menu and main method
 * Last Edited: 05/01/2016
 * @author Melkis Espinal, Ian Jacobs, Ally Colisto, and Janine Jay
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class MainMenu {
	
	//Variables
	private JTextArea textArea = new JTextArea();
	private JTextArea ipv4 = new JTextArea();
	private CustomTextArea portnum = new CustomTextArea();
	private JLabel image = new JLabel();
	private JLabel user = new JLabel("Username");
	private JLabel ip = new JLabel("Input IP");
	private JLabel portz = new JLabel("Input Port #");
	private JFrame Menu = new JFrame("A-Maze-Ing");
	private JButton Login = new JButton("Login");
	private JRadioButton serverButton = new JRadioButton("Server",true);
	private JRadioButton clientButton = new JRadioButton("Client");
	private ButtonGroup bg = new ButtonGroup();

	//Constructor
	public MainMenu() {
		//Menu Variables
		Menu.setResizable(false);
		Menu.setSize(700, 453);
		Menu.setLayout(null);
		Menu.setLocationRelativeTo(null);
		Menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Start Button Variables
		Login.setSize(100,30);
		Login.setLocation(20, 350);
		Menu.add(Login);

		//text area
		textArea.setSize(100, 20);
		textArea.setLocation(20, 200);
		Menu.add(textArea);

		//text area two
		ipv4.setSize(100, 20);
		ipv4.setLocation(20, 250);
		Menu.add(ipv4);

		//text are tres
		portnum.setSize(100, 20);
		portnum.setLocation(20, 300);
		Menu.add(portnum);

		//username
		user.setSize(150, 20);
		user.setLocation(20, 175);
		Menu.add(user);

		//ip label
		ip.setSize(150, 20);
		ip.setLocation(20, 225);
		Menu.add(ip);

		//port label
		portz.setSize(150, 20);
		portz.setLocation(20, 275);
		Menu.add(portz);

		//server radio button
		serverButton.setSize(70,70);
		serverButton.setLocation(20,300);
		Menu.add(serverButton);

		//client radio button
		clientButton.setSize(70,70);
		clientButton.setLocation(90,300);
		Menu.add(clientButton);

		//button group
		bg.add(serverButton);
		bg.add(clientButton);

		//server do not need these
		ipv4.setEditable(false);
		ip.setEnabled(false);
		portnum.setText("2020");

		serverButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				portnum.setText("2020");
				ipv4.setEditable(false);
				ip.setEnabled(false);
				ipv4.setText("");
			}
		});

		clientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ipv4.setEditable(true);
				ip.setEnabled(true);
				ipv4.setText("localhost");
				portnum.setText("2020");
			}
		});

		//background image
		image.setIcon(new ImageIcon("./images/MazeGameTitle.png"));// your image here
		image.setSize(700, 500);
		image.setLocation(0, -45);
		Menu.add(image);

		Login.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(serverButton.isSelected()){
					if(!textArea.getText().isEmpty() && !portnum.getText().isEmpty()){
						if(!portnum.getText().isEmpty()){
							try {new MazeServer("./map.map", portnum.getText(), textArea.getText(), InetAddress.getLocalHost().getHostAddress(),
									"Server.");} 
							catch (UnknownHostException e) {e.printStackTrace();}
							Menu.setVisible(false);
						}
						else{
							JOptionPane.showMessageDialog(null, "Type in a PORT number.", "Attention Needed", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Fill out the blanks.", "Attention Needed", JOptionPane.INFORMATION_MESSAGE);
						textArea.requestFocus();
					}
				}
				else if(clientButton.isSelected()){
					if(!textArea.getText().isEmpty() && !ipv4.getText().isEmpty() && !portnum.getText().isEmpty()){
						new MazeClient("./map.map", portnum.getText(), textArea.getText(), ipv4.getText(), "Client");
						Menu.setVisible(false);
					}
					else{
						JOptionPane.showMessageDialog(null, "Fill out the blanks.", "Attention Needed", JOptionPane.INFORMATION_MESSAGE);
						textArea.requestFocus();
					}
				}

			}

		});	
		Menu.setVisible(true);
	}

	//main
	public static void main(String args[]){
		new MainMenu();
	}
}
