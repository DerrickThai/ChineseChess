import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

/**
 * The frame of the game which keeps track of the main menu, help menu, and
 * credits. Responsible for switching between three main states: menu, game, and
 * help. Also handles the menu bar and their options.
 * 
 * @author Derrick Thai
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class ChineseChessMain extends JFrame implements ActionListener
{
	// To remove the warning
	private static final long serialVersionUID = 1L;

	// Different game states
	private static final int MENU = 0;
	private static final int GAME = 1;
	private static final int HELP = 2;

	// Names of the game modes
	private static String[] GAMEMODES = { "Player vs. Player",
			"Player vs. Easy AI", "Player vs. Normal AI", "Player vs. Hard AI",
			"Player vs. Expert AI" };

	// Menus, menu bar, and menu bar items
	private int state;
	private GamePanel gamePanel;
	private MainMenu mainMenu;
	private Instructions instructions;
	private JMenuItem returnToMenuOption, newGameOption, undoOption,
			quitOption, aboutOption, howToPlayOption;
	private JMenu themeMenu;
	JRadioButtonMenuItem traditionalButton, spaceButton;

	// Two board themes: traditional and space
	public static final int TRADITIONAL = 0;
	public static final int SPACE = 1;

	// Theme is static to be shared with all of the panels
	public static int theme;

	/**
	 * Constructs a new ChineseChessMain JFrame.
	 */
	public ChineseChessMain()
	{
		// Set the title of the window plus make it not resizable
		super("Chinese Chess");
		setLocation(10, 10);
		setResizable(false);
		setLayout(new BorderLayout());
		setVisible(true);

		// Add an icon in the corner of the window
		setIconImage(new ImageIcon("images/BlackGeneral.png").getImage());

		// Starting board theme is space
		theme = SPACE;

		// Setup the menu bar and load the main menu
		setupMenuBar();
		loadMainMenu();
	}

	/**
	 * Adds the menu bar and its options to this JFrame.
	 */
	public void setupMenuBar()
	{
		// Add the Game Menu to the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G'); // Alt + G

		// Set up the Menu Items
		returnToMenuOption = new JMenuItem("Main Menu");
		returnToMenuOption.addActionListener(this);

		newGameOption = new JMenuItem("New Game");
		newGameOption.addActionListener(this);
		newGameOption.setEnabled(true);
		newGameOption.setVisible(false);

		undoOption = new JMenuItem("Undo Move");
		undoOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK));
		undoOption.addActionListener(this);
		undoOption.setEnabled(false);
		newGameOption.setVisible(false);

		quitOption = new JMenuItem("Exit");
		quitOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		quitOption.addActionListener(this);

		// Add the game menu items in preferred order
		gameMenu.add(returnToMenuOption);
		gameMenu.add(undoOption);
		gameMenu.addSeparator();
		gameMenu.add(quitOption);
		menuBar.add(gameMenu);

		// Add the theme menu
		themeMenu = new JMenu("Board Theme");
		themeMenu.setMnemonic('B'); // Alt + B
		themeMenu.setEnabled(true);
		themeMenu.setVisible(false);
		ButtonGroup buttonGroup = new ButtonGroup();
		traditionalButton = new JRadioButtonMenuItem("Traditional");
		traditionalButton.addActionListener(this);
		buttonGroup.add(traditionalButton);
		themeMenu.add(traditionalButton);
		spaceButton = new JRadioButtonMenuItem("Space");
		spaceButton.addActionListener(this);
		spaceButton.setSelected(true);
		buttonGroup.add(spaceButton);
		themeMenu.add(spaceButton);
		menuBar.add(themeMenu);

		// Add the Help Menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H'); // Alt + H
		howToPlayOption = new JMenuItem("How to Play");
		howToPlayOption.addActionListener(this);
		aboutOption = new JMenuItem("About...");
		aboutOption.addActionListener(this);
		helpMenu.add(howToPlayOption);
		helpMenu.add(aboutOption);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
	}

	/**
	 * Loads the main menu panel and adds it to this frame.
	 */
	private void loadMainMenu()
	{
		// Change and update the state
		if (state == GAME)
			remove(gamePanel.removeBorders());
		else if (state == HELP)
			remove(instructions);
		state = MENU;

		// Add the menu panel to the frame
		mainMenu = new MainMenu();
		add(mainMenu, BorderLayout.CENTER);
		returnToMenuOption.setVisible(false);
		undoOption.setVisible(false);
		themeMenu.setVisible(false);
		howToPlayOption.setVisible(true);
		revalidate();
		repaint();
	}

	/**
	 * Loads the GamePanel and adds it to the frame.
	 */
	public void loadGame()
	{
		// Get the desired game mode
		String gameMode = (String) JOptionPane.showInputDialog(this,
				"Select a Gamemode", "Gamemode", JOptionPane.QUESTION_MESSAGE,
				null, GAMEMODES, GAMEMODES[0]);

		if (gameMode != null)
		{
			// Get the colour of the player to go first
			int firstPlayer = JOptionPane.showOptionDialog(this,
					"Select the Player to Go First", "First Player",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, Piece.COLOUR_NAMES, Piece.COLOUR_NAMES[0]);

			// Change and update the state
			if (state == MENU)
				remove(mainMenu);
			else if (state == GAME)
				remove(gamePanel);
			else if (state == HELP)
				remove(instructions);
			state = GAME;

			// Create the correct game mode
			if (gameMode == GAMEMODES[0])
				gamePanel = new GamePanel(this, false, 0);
			else if (gameMode == GAMEMODES[1])
				gamePanel = new GamePanel(this, true, 2);
			else if (gameMode == GAMEMODES[2])
				gamePanel = new GamePanel(this, true, 3);
			else if (gameMode == GAMEMODES[3])
				gamePanel = new GamePanel(this, true, 4);
			else
				gamePanel = new GamePanel(this, true, 5);

			// Add the GamePanel to the frame
			add(gamePanel, BorderLayout.CENTER);
			gamePanel.requestFocus();
			returnToMenuOption.setVisible(true);
			undoOption.setVisible(true);
			themeMenu.setVisible(true);
			howToPlayOption.setVisible(false);
			revalidate();
			repaint();

			// Start the game
			gamePanel.startGame(firstPlayer);
		}
	}

	/**
	 * Loads the help panel and add it to the frame.
	 */
	public void loadHelp()
	{
		// Change and update the state
		if (state == MENU)
			remove(mainMenu);
		else if (state == GAME)
			remove(gamePanel.removeBorders());
		state = HELP;

		// Add the help panel to the frame
		instructions = new Instructions();
		add(instructions, BorderLayout.CENTER);
		returnToMenuOption.setVisible(true);
		undoOption.setVisible(false);
		themeMenu.setVisible(false);
		revalidate();
		repaint();
	}

	/**
	 * Shows the game credits with message dialog.
	 */
	public void showCredits()
	{
		JOptionPane
				.showMessageDialog(
						this,
						"Chinese Chess\nHead Programmer and Menus: Derrick Thai\nGraphics and Programming: Riddle Li\n\u00a9 2015",
						"About", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Deals with the menu bar's menu options' events.
	 * @param event the event that triggered this method
	 */
	public void actionPerformed(ActionEvent event)
	{
		// Handle each option accordingly
		if (event.getSource() == newGameOption)
			loadGame();
		else if (event.getSource() == undoOption)
		{
			gamePanel.undo();
			if (!gamePanel.canUndo())
				setUndoOption(false);
		}
		else if (event.getSource() == quitOption)
			System.exit(0);
		else if (event.getSource() == howToPlayOption)
		{
			// Only load help if we are not currently in help
			if (state != HELP)
				loadHelp();
		}
		else if (event.getSource() == aboutOption)
			showCredits();
		else if (event.getSource() == returnToMenuOption)
			loadMainMenu();
		// Change themes
		else if (event.getActionCommand().equals("Traditional"))
		{
			theme = TRADITIONAL;
			gamePanel.repaint();
		}
		else if (event.getActionCommand().equals("Space"))
		{
			theme = SPACE;
			gamePanel.repaint();
		}
	}

	/**
	 * Sets the undo menu option to true or false
	 * @param canUndo true to enable the undo option and false to disable it
	 */
	public void setUndoOption(boolean canUndo)
	{
		this.undoOption.setEnabled(canUndo);
	}

	public static void main(String[] args)
	{
		ChineseChessMain frame = new ChineseChessMain();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Private inner class that is the JPanel for the main menu.
	 * 
	 * @author Derrick Thai
	 * @version v3.1 Final, Last Updated: December 20, 2015
	 */
	private class MainMenu extends JPanel implements MouseListener,
			MouseMotionListener
	{
		// To remove the warning
		private static final long serialVersionUID = 1L;

		// Constants for the dimension and button indexes
		private final Dimension SIZE = new Dimension(HorizontalPanel.WIDTH,
				GamePanel.HEIGHT + 2 * HorizontalPanel.HEIGHT);

		private final int BUTTON_PLAY = 0;
		private final int BUTTON_HELP = 1;
		private final int BUTTON_CREDITS = 2;
		private final int BUTTON_EXIT = 3;

		// Menu image and rectangle array for the buttons
		private Image menuImage;
		private Rectangle[] buttons;

		// Animating board for the two player mode
		private JLabel gifLabel;
		private Icon animatingBoard;

		/**
		 * Constructs a new Main Menu.
		 */
		private MainMenu()
		{
			// Add the listeners
			addMouseListener(this);
			addMouseMotionListener(this);

			// Panel preferences
			setPreferredSize(SIZE);
			setFocusable(true);
			requestFocusInWindow();

			// Menu image
			menuImage = new ImageIcon("images/menuOverlay.png").getImage();
			Image resized = menuImage.getScaledInstance(SIZE.width,
					SIZE.height, java.awt.Image.SCALE_SMOOTH);
			menuImage = new ImageIcon(resized).getImage();

			animatingBoard = new ImageIcon("images/menu.gif");
			gifLabel = new JLabel(animatingBoard);
			add(gifLabel, BorderLayout.CENTER);

			// Button coordinate only for spacing of 60
			buttons = new Rectangle[4];
			buttons[BUTTON_PLAY] = new Rectangle(60, 335, 220, 35);
			buttons[BUTTON_HELP] = new Rectangle(60, 392, 220, 35);
			buttons[BUTTON_CREDITS] = new Rectangle(60, 450, 220, 35);
			buttons[BUTTON_EXIT] = new Rectangle(60, 507, 220, 35);
		}

		/**
		 * Paints the menu's background image.
		 * 
		 * @param g The Graphics context to do the drawing
		 */
		public void paintChildren(Graphics g)
		{
			super.paintChildren(g);
			g.drawImage(menuImage, 0, 0, this);
		}

		/**
		 * Handles mouse movements and changes cursor accordingly.
		 * 
		 * @param event information about the mouse moved event
		 */
		public void mouseMoved(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			// Change to hand cursor if mouse is hovering a button
			for (Rectangle button : buttons)
				if (button.contains(currentPoint))
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					repaint();
					return;
				}

			// If not hovering any button use default cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			repaint();
		}

		/**
		 * Handles mouse presses on buttons.
		 * 
		 * @param event information about the mouse pressed event
		 */
		public void mousePressed(MouseEvent event)
		{
			Point selectedPoint = event.getPoint();

			// Determine what button was pressed
			for (int button = 0; button < buttons.length; button++)
				if (buttons[button].contains(selectedPoint))
				{
					// Execute the corresponding method
					if (button == BUTTON_PLAY)
						loadGame();
					else if (button == BUTTON_HELP)
						loadHelp();
					else if (button == BUTTON_CREDITS)
						showCredits();
					else if (button == BUTTON_EXIT)
						System.exit(0);
				}
		}

		// Unused Listeners
		public void mouseClicked(MouseEvent event)
		{
		}

		public void mouseEntered(MouseEvent event)
		{
		}

		public void mouseExited(MouseEvent event)
		{
		}

		public void mouseReleased(MouseEvent event)
		{
		}

		public void mouseDragged(MouseEvent event)
		{
		}

	}

	/**
	 * Private inner class that is the JPanel for the help menu.
	 * 
	 * @author Derrick Thai
	 * @version v3.1 Final, Last Updated: December 20, 2015
	 */
	private class Instructions extends JPanel implements MouseListener,
			MouseMotionListener
	{
		// To remove the warning
		private static final long serialVersionUID = 1L;

		// Constants for the dimension, button indexes, and number of pages
		private final Dimension SIZE = new Dimension(HorizontalPanel.WIDTH,
				GamePanel.HEIGHT + 2 * HorizontalPanel.HEIGHT);

		private final int BUTTON_BACK = 0;
		private final int BUTTON_MENU = 1;
		private final int BUTTON_NEXT = 2;

		private final int NO_PAGES = 9;

		// Image and rectangle arrays for the pages and buttons plus a variable
		// to keep track of the current page
		private Image[] helpImages;
		private Rectangle[] buttons;
		private int currentPage;

		/**
		 * Constructs a new instructions menu.
		 */
		private Instructions()
		{
			// Add the listeners
			addMouseListener(this);
			addMouseMotionListener(this);

			// Panel preferences
			setPreferredSize(SIZE);
			setFocusable(true);
			requestFocusInWindow();

			// Load images for all of the pages
			helpImages = new Image[NO_PAGES];

			for (int i = 0; i < NO_PAGES; i++)
			{
				Image image = new ImageIcon("images/help" + (i + 1) + ".jpg")
						.getImage();
				Image resized = image.getScaledInstance(SIZE.width,
						SIZE.height, java.awt.Image.SCALE_SMOOTH);

				helpImages[i] = new ImageIcon(resized).getImage();
			}
			currentPage = 0;

			// Button coordinates only for a spacing of 60
			buttons = new Rectangle[3];
			buttons[BUTTON_BACK] = new Rectangle(2, 740, 260, 50);
			buttons[BUTTON_MENU] = new Rectangle(280, 740, 295, 50);
			buttons[BUTTON_NEXT] = new Rectangle(590, 740, 288, 50);
		}

		/**
		 * Paints the help menu's current page.
		 * 
		 * @param g The Graphics context
		 */
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(helpImages[currentPage], 0, 0, this);
		}

		/**
		 * Handles mouse movements and changes cursor accordingly.
		 * 
		 * @param event information about the mouse moved event
		 */
		public void mouseMoved(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			// Change to hand cursor if mouse is hovering a button
			for (Rectangle button : buttons)
				if (button.contains(currentPoint))
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					repaint();
					return;
				}

			// If not hovering any button use default cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			repaint();
		}

		/**
		 * Handles mouse presses on buttons.
		 * 
		 * @param event information about the mouse pressed event
		 */
		public void mousePressed(MouseEvent event)
		{
			Point selectedPoint = event.getPoint();

			// Determine what button was pressed
			for (int button = 0; button < buttons.length; button++)
				if (buttons[button].contains(selectedPoint))
				{

					// Change the current page
					if (button == BUTTON_BACK)
					{
						if (currentPage == 0)
							currentPage = NO_PAGES - 1;
						else
							currentPage--;
					}
					else if (button == BUTTON_NEXT)
					{

						if (currentPage == NO_PAGES - 1)
							currentPage = 0;
						else
							currentPage++;
					}
					// Return to the main menu
					if (button == BUTTON_MENU)
					{
						loadMainMenu();
					}
				}
			repaint();
		}

		// Unused Listeners
		public void mouseClicked(MouseEvent event)
		{
		}

		public void mouseEntered(MouseEvent event)
		{
		}

		public void mouseExited(MouseEvent event)
		{
		}

		public void mouseReleased(MouseEvent event)
		{
		}

		public void mouseDragged(MouseEvent event)
		{
		}

	}
}
