import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The panels above and below the GamePanel that is used to make game window
 * appear more complete and make the window fit the monitor better.
 * 
 * @author Derrick Thai and Riddle Li
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class HorizontalPanel extends JPanel
{
	// To remove the warning
	private static final long serialVersionUID = 1L;

	// Constants for the panel colour and dimensions
	public static final Color PANEL_COLOUR = new Color(225, 206, 158);

	public static final int WIDTH = GamePanel.WIDTH + 2 * (SidePanel.WIDTH);
	public static final int HEIGHT = SidePanel.WIDTH / 2;

	// To differentiate the top and bottom panels
	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	private int location;

	// Background image
	private Image background;

	/**
	 * Constructs a new TopPanel.
	 */
	public HorizontalPanel(int location)
	{
		// Set up the size, background, and background colours
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		
		if(location == TOP)
		background = new ImageIcon("images/topPanel.png").getImage();
		else
			background = new ImageIcon("images/bottomPanel.png").getImage();

		this.location = location;
	}

	/**
	 * Draws the selected item and background.
	 * @param g the Graphics context to do the drawing
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (location == TOP)
		{
			// Traditional background
			if (ChineseChessMain.theme == ChineseChessMain.TRADITIONAL)
			{
				g.setColor(PANEL_COLOUR);
				g.fillRect(GamePanel.THICKNESS, GamePanel.THICKNESS, WIDTH - 2
						* GamePanel.THICKNESS, HEIGHT - GamePanel.THICKNESS);
			}
			else if (ChineseChessMain.theme == ChineseChessMain.SPACE)
				// Space background
				g.drawImage(background, 0, 0, null);

			// Selected Piece
			if (GamePanel.selectedPiece != null)
				GamePanel.selectedPiece.draw(g, SidePanel.WIDTH, HEIGHT);
		}
		else if (location == BOTTOM)
		{
			// Traditional background
			if (ChineseChessMain.theme == ChineseChessMain.TRADITIONAL)
			{
				g.setColor(PANEL_COLOUR);
				g.fillRect(GamePanel.THICKNESS, 0, WIDTH - 2
						* GamePanel.THICKNESS,
						HEIGHT - GamePanel.THICKNESS);
			}
			// Space background
			else if (ChineseChessMain.theme == ChineseChessMain.SPACE)
				g.drawImage(background, 0, 0, null);

			// Selected Piece
			if (GamePanel.selectedPiece != null)
				GamePanel.selectedPiece.draw(g, SidePanel.WIDTH,
						-GamePanel.HEIGHT);
		}
	}
}
