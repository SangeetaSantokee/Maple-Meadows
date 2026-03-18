import javax.swing.*;			// need this for GUI objects
import java.awt.*;			// need this for Layout Managers
import java.awt.event.*;		// need this to respond to GUI events
	
public class GameWindow extends JFrame 
				implements ActionListener,
					   KeyListener,
					   MouseListener
{
	// declare instance variables for user interface objects

	// declare labels 

	private JLabel statusBarL;
	private JLabel keyL;
	//private JLabel mouseL;
	private JLabel fpsL;
	private JLabel effectL;
	private JLabel playerPosL;
	private JLabel foxCountL;

	// declare text fields

	private JTextField statusBarTF;
	private JTextField keyTF;
	//private JTextField mouseTF;
	private JTextField fpsTF;
	private JTextField effectTF;
	private JTextField playerPosTF;
	private JTextField foxCountTF;

	// declare buttons

	private JButton startB;
	private JButton pauseB;
	private JButton endB;
	private JButton startNewB;
	private JButton focusB;
	private JButton exitB;

	private Container c;

	private JPanel mainPanel;
	private GamePanel gamePanel;

	private Timer infoTimer;

	@SuppressWarnings({"unchecked"})
	public GameWindow() {
 
		setTitle ("Maple Meadows");
		setSize (600, 710);

		// create user interface objects

		// create labels

		statusBarL = new JLabel ("Application Status: ");
		keyL = new JLabel("Key Pressed: ");
		//mouseL = new JLabel("Location of Mouse Click: ");
		effectL = new JLabel("Current Effect: ");
		fpsL = new JLabel("FPS: ");
		playerPosL = new JLabel("Player Position: ");
		foxCountL = new JLabel("Foxes Collected: ");

		// create text fields and set their colour, etc.

		statusBarTF = new JTextField (25);
		keyTF = new JTextField (25);
		//mouseTF = new JTextField (25);
		fpsTF = new JTextField(25);
		effectTF = new JTextField(25);
		playerPosTF = new JTextField(25);
		foxCountTF = new JTextField(25);

		statusBarTF.setEditable(false);
		keyTF.setEditable(false);
		//mouseTF.setEditable(false);
		fpsTF.setEditable(false);
		effectTF.setEditable(false);
		playerPosTF.setEditable(false);
		foxCountTF.setEditable(false);

		statusBarTF.setBackground(new Color(255, 204, 204));
		keyTF.setBackground(new Color(255, 229, 180));
		fpsTF.setBackground(new Color(204, 229, 255));
		effectTF.setBackground(new Color(204, 255, 204));
		playerPosTF.setBackground(new Color(230, 204, 255));
		foxCountTF.setBackground(new Color(255, 255, 204));

		// create buttons

		startB = new JButton ("Start Game");
		pauseB = new JButton ("Pause Game");
		endB = new JButton ("End Game");
		exitB = new JButton ("Exit");


		// add listener to each button (same as the current object)

		startB.addActionListener(this);
		pauseB.addActionListener(this);
		endB.addActionListener(this);
		exitB.addActionListener(this);
		
		// create mainPanel

		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		GridLayout gridLayout;

		// create the gamePanel for game entities

		gamePanel = new GamePanel();
		gamePanel.setPreferredSize(new Dimension(500, 500));

		// create infoPanel

		JPanel infoPanel = new JPanel();
		gridLayout = new GridLayout(6, 2);
		infoPanel.setLayout(gridLayout);
		infoPanel.setBackground(new Color(220, 255, 230));

		// add user interface objects to infoPanel
	
		infoPanel.add (statusBarL);
		infoPanel.add (statusBarTF);

		infoPanel.add (keyL);
		infoPanel.add (keyTF);		

		//infoPanel.add (mouseL);
		//infoPanel.add (mouseTF);

		infoPanel.add(fpsL);
		infoPanel.add(fpsTF);

		infoPanel.add(effectL);
		infoPanel.add(effectTF);

		infoPanel.add(playerPosL);
		infoPanel.add(playerPosTF);

		infoPanel.add(foxCountL);
		infoPanel.add(foxCountTF);

		// create buttonPanel

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(1, 4);
		buttonPanel.setLayout(gridLayout);

		// add buttons to buttonPanel

		buttonPanel.add (startB);
		buttonPanel.add (pauseB);
		buttonPanel.add (endB);
		buttonPanel.add (exitB);

		// add sub-panels with GUI objects to mainPanel and set its colour

		mainPanel.add(infoPanel);
		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		//mainPanel.setBackground(new Color(255, 230, 210));
		mainPanel.setBackground(Color.PINK);

		// set up mainPanel to respond to keyboard and mouse

		gamePanel.addMouseListener(this);
		mainPanel.addKeyListener(this);


		// add mainPanel to window surface

		c = getContentPane();
		c.add(mainPanel);

		// set properties of window

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		infoTimer = new Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fpsTF.setText(gamePanel.getFPS() + " fps");

				effectTF.setText(gamePanel.getCurrentEffectName());

				playerPosTF.setText(gamePanel.getPlayerPosition());

				foxCountTF.setText(gamePanel.getFoxesCollected() + " / 6");
			}
		});
		infoTimer.start();

		// set status bar message

		statusBarTF.setText("Application started.");
	}


	// implement single method in ActionListener interface

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		statusBarTF.setText(command + " button clicked.");

		if (command.equals(startB.getText())) {
			gamePanel.startGame();
		}

		if (command.equals(pauseB.getText())) {
			gamePanel.pauseGame();
			if (pauseB.getText().equals("Pause Game"))
				pauseB.setText ("Resume");
			else
				pauseB.setText ("Pause Game");

		}
		
		if (command.equals(endB.getText())) {
			gamePanel.endGame();
		}

		if (command.equals(exitB.getText()))
			System.exit(0);

		mainPanel.requestFocus();
	}


	// implement methods in KeyListener interface

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String keyText = e.getKeyText(keyCode);
		keyTF.setText(keyText + " pressed.");

		if (keyCode == KeyEvent.VK_LEFT) {
			gamePanel.updateBat (1);
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			gamePanel.updateBat (2);
		}

		if (keyCode == KeyEvent.VK_UP) {
			gamePanel.updateBat (3);
		}

		if (keyCode == KeyEvent.VK_DOWN) {
			gamePanel.updateBat (4);
		}
		if (keyCode == KeyEvent.VK_SPACE) {
			gamePanel.shoot();
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_LEFT ||
				keyCode == KeyEvent.VK_RIGHT ||
				keyCode == KeyEvent.VK_UP ||
				keyCode == KeyEvent.VK_DOWN) {
			gamePanel.stopBat();
		}
	}

	public void keyTyped(KeyEvent e) {

	}


	// implement methods in MouseListener interface

	public void mouseClicked(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		if (gamePanel.isOnBat(x, y)) {
			statusBarTF.setText ("Mouse click on bat!");
			statusBarTF.setBackground(Color.RED);
		}
		else {
			statusBarTF.setText ("");
			statusBarTF.setBackground(Color.CYAN);
		}

		//mouseTF.setText("(" + x +", " + y + ")");

	}


	public void mouseEntered(MouseEvent e) {
	
	}

	public void mouseExited(MouseEvent e) {
	
	}

	public void mousePressed(MouseEvent e) {
	
	}

	public void mouseReleased(MouseEvent e) {
	
	}

}