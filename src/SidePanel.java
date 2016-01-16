import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The panel to the sides of the GamePanel that is used to make game window
 * appear more complete and make the window fit the monitor better.
 * 
 * @author Derrick Thai and Riddle Li
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class SidePanel extends JPanel
{
	// To remove the warning
	private static final long serialVersionUID = 1L;

	// Constants for the colours, fonts, and dimensions
	public static final Color PANEL_COLOUR = new Color(225, 206, 158);
	public static final Color TURN_COLOUR = new Color(250, 240, 150);
	public static final Color OUT_TURN_COLOUR = new Color(200, 200, 200);

	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD,
			GamePanel.SPACING / 2);
	private static final Font CHECK_FONT = new Font("Arial", Font.BOLD,
			GamePanel.SPACING / 3);

	public static final int WIDTH = (int) (3.5 * Piece.WIDTH);
	public static final int HEIGHT = GamePanel.HEIGHT;

	// This class is used for both the left and right panel. Put both sides into
	// one class (unlike top/bottom) because the side panels are more complex
	public static final int LEFT = 0;
	public static final int RIGHT = 1;

	private int side;

	// Background image
	private Image background;

	// Each side panel will keep track of a different player
	private Player player;

	/**
	 * Constructs a new SidePanel with the given side and Player
	 * @param side the side of the panel (left = 0, right = 1)
	 * @param player the player that this SidePanel will keep track of
	 */
	public SidePanel(int side, Player player)
	{
		// Set up the size, background, and background colours
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);

		if (side == LEFT)
			background = new ImageIcon("images/leftPanel.png").getImage();
		else
			// side == RIGHT
			background = new ImageIcon("images/rightPanel.png").getImage();
		
		
		Image resized = background.getScaledInstance(WIDTH,
				HEIGHT + 4, java.awt.Image.SCALE_SMOOTH);
		background = new ImageIcon(resized).getImage();

		
		this.side = side;
		this.player = player;
	}

	/**
	 * Draws the selected item, this Player's information, and background.
	 * @param g the Graphics context to do the drawing
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (ChineseChessMain.theme == ChineseChessMain.SPACE)
			g.drawImage(background, 0, 0, null);

		// Different drawing depending on the side
		if (side == LEFT)
		{
			// Captured Pieces area
			if (ChineseChessMain.theme == ChineseChessMain.TRADITIONAL)
			{
				g.setColor(PANEL_COLOUR);
				g.fillRect(GamePanel.THICKNESS, GamePanel.THICKNESS + HEIGHT
						/ 8 + HEIGHT / 14, WIDTH - GamePanel.THICKNESS, HEIGHT
						- (GamePanel.THICKNESS + HEIGHT / 8 + HEIGHT / 14)
						- GamePanel.THICKNESS);
			}
			else
			{
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, WIDTH, HEIGHT / 5 + 1);
			}

			// Light up the player whose turn it is
			if (player.isTurn())
				g.setColor(TURN_COLOUR);
			else
				g.setColor(OUT_TURN_COLOUR);

			// Title Box
			g.fillRect(GamePanel.THICKNESS, GamePanel.THICKNESS, WIDTH
					- GamePanel.THICKNESS, HEIGHT / 8 - 2 * GamePanel.THICKNESS);
			g.setColor(Color.RED);
			g.setFont(TITLE_FONT);
			g.drawString("RED", WIDTH / 4, HEIGHT / 13);

			// Status box
			if (player.getStatus() == Player.CHECK)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(GamePanel.THICKNESS, HEIGHT / 8, WIDTH, HEIGHT / 14);
				g.setColor(Color.RED);
				g.setFont(CHECK_FONT);
				g.drawString("CHECK", (int) (WIDTH / 4.2), HEIGHT / 6);
			}
			else
			{
				g.setColor(OUT_TURN_COLOUR);
				g.fillRect(GamePanel.THICKNESS, HEIGHT / 8, WIDTH, HEIGHT / 14);
			}
		}
		else
		// side == RIGHT
		{
			// Captured pieces area
			if (ChineseChessMain.theme == ChineseChessMain.TRADITIONAL)
			{
				g.setColor(PANEL_COLOUR);
				g.fillRect(0, GamePanel.THICKNESS + HEIGHT / 8
						+ HEIGHT / 14, WIDTH - GamePanel.THICKNESS, HEIGHT
						- (GamePanel.THICKNESS + HEIGHT / 8 + HEIGHT / 14)
						- GamePanel.THICKNESS);
			}
			else
			{
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, WIDTH, HEIGHT / 5 + 1);
			}

			// Light up the player whose turn it is
			if (player.isTurn())
				g.setColor(TURN_COLOUR);
			else
				g.setColor(OUT_TURN_COLOUR);

			// Title Box
			g.fillRect(0, GamePanel.THICKNESS, WIDTH
					- GamePanel.THICKNESS, HEIGHT / 8 - 2 * GamePanel.THICKNESS);
			g.setColor(Color.BLACK);
			g.setFont(TITLE_FONT);
			g.drawString("BLACK", WIDTH / 12, HEIGHT / 13);

			// Status box
			if (player.getStatus() == Player.CHECK)
			{
				g.setColor(Color.YELLOW);
				g.fillRect(0, HEIGHT / 8, WIDTH - GamePanel.THICKNESS,
						HEIGHT / 14);
				g.setColor(Color.BLACK);
				g.setFont(CHECK_FONT);
				g.drawString("CHECK", (int) (WIDTH / 4.4), HEIGHT / 6);
			}
			else
			{
				g.setColor(OUT_TURN_COLOUR);
				g.fillRect(0, HEIGHT / 8, WIDTH - GamePanel.THICKNESS,
						HEIGHT / 14);
			}
		}

		// Draw the captured pieces in 2 columns and 8 rows
		int pieceNo = 0, x;
		int y = (int) (HEIGHT - (9 * (Piece.HEIGHT * 1.5)));

		for (Piece piece : player.getCapturedPieces())
		{
			if (++pieceNo % 2 == 1)
			{
				x = Piece.WIDTH / 2;
				y += 1.5 * Piece.HEIGHT;
			}
			else
				x = 2 * Piece.WIDTH;

			g.drawImage(piece.image, x, y, null);
		}

		// Selected Piece
		if (GamePanel.selectedPiece != null)
			if (side == LEFT)
				GamePanel.selectedPiece.draw(g, WIDTH, 0);
			else
				// side == RIGHT
				GamePanel.selectedPiece.draw(g, -GamePanel.WIDTH, 0);
	}
}
