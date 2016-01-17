import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The GamePanel which looks after the entire game. It is responsible for keep
 * track of both player, the board, the pieces, and the moves list. Also keeps
 * track of the side panels and draws the traditional board.
 * 
 * @author Derrick Thai
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class GamePanel extends JPanel implements MouseListener,
		MouseMotionListener
{
	// To remove the warning
	private static final long serialVersionUID = 1L;

	// Constants for the number of rows and columns
	public static final int NO_OF_ROWS = 10;
	public static final int NO_OF_COLS = 9;

	// Spacing between points, border thickness, and panel dimensions
	public static final int SPACING = 60;
	public static final int THICKNESS = SPACING / 15;
	public static final int WIDTH = SPACING * (NO_OF_COLS + 1);
	public static final int HEIGHT = SPACING * (NO_OF_ROWS + 1);

	// Outer and inner board colours
	public static final Color OUTER_COLOUR = new Color(206, 92, 0);
	public static final Color INNER_COLOUR = new Color(252, 175, 62);

	// Animation constants
	private static final int FRAMES = 10;
	private static final int DELAY = 20;

	// Variables to keep track of the board, players, border panels, and moves
	private ChineseChessMain parentFrame;
	private ChessPoint[][] board;
	private Player blackPlayer, redPlayer, currentPlayer;
	private boolean inGame;
	private LinkedList<Move> moves;
	private Set<ChessPoint> validMoves;
	private Piece movingPiece;
	private ChessPoint sourcePoint;
	private Point lastPoint;
	private Image background;
	private SidePanel leftPanel, rightPanel;
	private HorizontalPanel topPanel, bottomPanel;

	// Selected Piece is static since it is shared between the panels
	public static Piece selectedPiece;

	// Animating board for the two player mode
	private JLabel gifLabel;
	private Icon animatingBoard;

	/**
	 * Constructs a new GamePanel given the JFrame and AI information.
	 * @param parentFrame the JFrame to put this JPanel in
	 * @param computer true if the opponent is a computer, false if not
	 * @param level the difficulty of the AI (use 0 if 2 players)
	 */
	public GamePanel(ChineseChessMain parentFrame, boolean computer, int level)
	{
		// Set up the size, background, and background colours
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);

		// The computer must use the still background while the 2 player mode
		// can use the animated background
		if (computer)
			background = new ImageIcon("images/board.jpg").getImage();
		else
		{
			animatingBoard = new ImageIcon("images/animatingBoard.gif");
			gifLabel = new JLabel(animatingBoard);
			add(gifLabel, BorderLayout.CENTER);
		}
		this.parentFrame = parentFrame;

		// Add mouse listeners to the panel
		addMouseListener(this);
		addMouseMotionListener(this);

		// Initialize the board and link the pieces to it
		board = new ChessPoint[NO_OF_ROWS][NO_OF_COLS];
		for (int row = 0; row < NO_OF_ROWS; row++)
			for (int col = 0; col < NO_OF_COLS; col++)
				board[row][col] = new ChessPoint(row, col);
		Piece.linkBoard(board);

		// Initialize the players and setup their pieces
		redPlayer = new Player(Player.RED);
		if (computer)
			blackPlayer = new Computer(Player.BLACK, level);
		else
			blackPlayer = new Player(Player.BLACK);
		redPlayer.resetPieces(board);
		blackPlayer.resetPieces(board);

		// Setup the border panels
		leftPanel = new SidePanel(SidePanel.LEFT, redPlayer);
		rightPanel = new SidePanel(SidePanel.RIGHT, blackPlayer);
		parentFrame.add(leftPanel, BorderLayout.WEST);
		parentFrame.add(rightPanel, BorderLayout.EAST);

		topPanel = new HorizontalPanel(HorizontalPanel.TOP);
		bottomPanel = new HorizontalPanel(HorizontalPanel.BOTTOM);
		parentFrame.add(topPanel, BorderLayout.NORTH);
		parentFrame.add(bottomPanel, BorderLayout.SOUTH);

		// Remaining variables
		moves = new LinkedList<Move>();
		selectedPiece = null;
		parentFrame.setUndoOption(false);
		repaint();
	}

	/**
	 * Starts the game given the player to go first.
	 * @param firstPlayer the player to go first (use Player constants)
	 */
	public void startGame(int firstPlayer)
	{
		// Set the current player
		if (firstPlayer == Player.RED)
		{
			currentPlayer = redPlayer.setTurn(true);
			blackPlayer.setTurn(false);
		}
		else
		{
			currentPlayer = blackPlayer.setTurn(true);
			redPlayer.setTurn(false);
		}

		// Begin the game and if the first player is the computer, make it make
		// the first move
		inGame = true;
		repaint();
		if (currentPlayer instanceof Computer)
			computerMove();
	}

	/**
	 * Determines if an undo operation can be made.
	 * @return true if an undo can be done or false if not
	 */
	public boolean canUndo()
	{
		return !moves.isEmpty();
	}

	/**
	 * If possible, undos the last move. If facing an AI, both the player and
	 * the AI's last move will be undoed.
	 */
	public void undo()
	{
		// Undo if there are moves in the list
		if (canUndo())
		{
			Move lastMove = moves.removeLast();
			lastMove.undo();

			// If facing AI, undo its move and do not change turns
			if (!moves.isEmpty() && blackPlayer instanceof Computer)
			{
				lastMove = moves.removeLast();
				lastMove.undo();
			}
			else
				changeTurn();

			repaint();
		}
	}

	/**
	 * Checks if there is a winner (called before changing turns).
	 * @author Derrick Thai and Riddle Li
	 * @return true if there is a winner or false if not
	 */
	public boolean checkForWinner()
	{
		// Get the other player
		Player other;
		if (currentPlayer.getColour() == Player.BLACK)
			other = redPlayer;
		else
			other = blackPlayer;

		// * In Chinese Chess, when a player has no more moves, it is not a
		// stalemate, that player loses
		ArrayList<Piece> allPieces = other.getAlivePieces();
		for (Piece eachPiece : allPieces)
			if (eachPiece.generateMoves(true).size() > 0)
				return false;
		return true;
	}

	/**
	 * Ends the game after a winner is found.
	 */
	public void win()
	{
		// Disable undos as well as the moving of pieecs
		inGame = false;
		parentFrame.setUndoOption(false);
		setCursor(Cursor.getDefaultCursor());

		// Winning message
		JOptionPane.showMessageDialog(parentFrame, "Checkmate. " +
				Piece.COLOUR_NAMES[currentPlayer.getColour()] + " wins!",
				"Congratulations",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Animates a move from its from point to to point given the Move.
	 * @param move the Move to be animated
	 */
	public void animateMove(Move move)
	{
		// Get the piece and the points to move from and to
		Point toPos = new Point(move.getToPoint().x, move.getToPoint().y);
		Point fromPos = new Point(move.getFromPoint().x, move.getFromPoint().y);
		Piece pieceToMove = move.getMoved();

		// Calculate the change in x and y
		int dx = (toPos.x - fromPos.x) / FRAMES;
		int dy = (toPos.y - fromPos.y) / FRAMES;

		movingPiece = pieceToMove;
		for (int times = 1; times <= FRAMES; times++)
		{
			// Move the piece
			fromPos.x += dx;
			fromPos.y += dy;
			pieceToMove.setPosition(fromPos);

			// Update the drawing area immediately
			paintImmediately(0, 0, getWidth(), getHeight());
			delay(DELAY);
		}
		// Lock the piece to the correct point
		pieceToMove.setPosition(toPos);
		movingPiece = null;
	}

	/**
	 * Draws the selected item, the points and their pieces, and the background.
	 * @param g the Graphics context to do the drawing
	 */
	public void paintChildren(Graphics g)
	{
		// Use paint children to draw on top of the GIF
		super.paintChildren(g);

		// Traditional board
		if (ChineseChessMain.theme == ChineseChessMain.TRADITIONAL)
		{
			g.setColor(OUTER_COLOUR);
			g.fillRect(THICKNESS, THICKNESS, WIDTH - 2 * THICKNESS, HEIGHT
					- 2 * THICKNESS);

			g.setColor(INNER_COLOUR);
			g.fillRect(SPACING, SPACING, WIDTH - 2 * SPACING, HEIGHT - 2
					* SPACING);
		}
		else if (blackPlayer instanceof Computer
				&& ChineseChessMain.theme == ChineseChessMain.SPACE)
			g.drawImage(background, 0, 0, null);

		// Draw the board lines
		g.setColor(Color.BLACK);
		for (int r = 1; r <= NO_OF_ROWS; r++)
			g.drawLine(SPACING, r * SPACING, WIDTH
					- SPACING, r * SPACING);
		for (int c = 2; c < NO_OF_COLS; c++)
			g.drawLine(c * SPACING, SPACING,
					c * SPACING, SPACING * 5);
		for (int c = 2; c < NO_OF_COLS; c++)
			g.drawLine(c * SPACING, SPACING * 6, c
					* SPACING, HEIGHT - SPACING);
		g.drawLine(SPACING, SPACING, SPACING, HEIGHT
				- SPACING);
		g.drawLine(WIDTH - SPACING, SPACING, WIDTH
				- SPACING, HEIGHT - SPACING);
		g.drawLine(SPACING * 4, SPACING, SPACING * 6,
				SPACING * 3);
		g.drawLine(SPACING * 4, SPACING * 8, SPACING * 6,
				SPACING * 10);
		g.drawLine(SPACING * 4, SPACING * 3, SPACING * 6,
				SPACING);
		g.drawLine(SPACING * 4, SPACING * 10, SPACING * 6,
				SPACING * 8);

		// Draw the points and their pieces
		for (ChessPoint[] row : board)
			for (ChessPoint point : row)
				point.draw(g);

		// Draw the animating piece
		if (movingPiece != null)
			movingPiece.draw(g, 0, 0);

		// Draw the selected piece on top
		if (selectedPiece != null)
			selectedPiece.draw(g, 0, 0);
	}

	/**
	 * Switches the current player to the opposite colour.
	 */
	public void changeTurn()
	{
		if (currentPlayer == redPlayer)
		{
			currentPlayer = blackPlayer.setTurn(true);
			redPlayer.setTurn(false);
			repaint();

			// Make the AI make its move if it is now its turn
			if (blackPlayer instanceof Computer)
			{
				setCursor(Cursor.getDefaultCursor());
				computerMove();
			}
		}
		else
		{
			currentPlayer = redPlayer.setTurn(true);
			blackPlayer.setTurn(false);
		}
	}

	/**
	 * Makes the AI make its move.
	 */
	private void computerMove()
	{
		// Get the best move and execute it
		Move move = ((Computer) blackPlayer).bestMove(board, redPlayer, true);

		// Also animate the move and add it to the moves list
		if (move != null)
		{
			animateMove(move);
			move.execute();
			moves.addLast(move);
		}
		else
		{
			// AI Loses when it does not have a move
			changeTurn();
			win();
		}
		repaint();
		delay(100);

		// Check for AI winning and then change turns
		if (checkForWinner())
			win();
		else
			changeTurn();
	}

	/**
	 * Delays the program for the given time in milliseconds.
	 * @param milliSec the time to delay in milliseconds
	 */
	public void delay(int milliSec)
	{
		try
		{
			Thread.sleep(milliSec);
		}
		catch (Exception e)
		{
		}
	}

	/**
	 * Global repaint which immediately repaints all of the panels.
	 * @overrides the repaint() method in the JPanel class
	 */
	public void repaint()
	{
		this.paintImmediately(0, 0, WIDTH, HEIGHT);

		if (leftPanel != null && rightPanel != null && topPanel != null
				&& bottomPanel != null)
		{
			leftPanel.paintImmediately(0, 0, SidePanel.WIDTH, SidePanel.HEIGHT);
			rightPanel
					.paintImmediately(0, 0, SidePanel.WIDTH, SidePanel.HEIGHT);

			topPanel.paintImmediately(0, 0, HorizontalPanel.WIDTH,
					HorizontalPanel.HEIGHT);
			bottomPanel.paintImmediately(0, 0, HorizontalPanel.WIDTH,
					HorizontalPanel.HEIGHT);
		}
	}

	/**
	 * Removes all of the border panels (used when exiting the game panel).
	 * @return this panel (so that it can be removed from the frame)
	 */
	public Component removeBorders()
	{
		parentFrame.remove(topPanel);
		parentFrame.remove(bottomPanel);
		parentFrame.remove(leftPanel);
		parentFrame.remove(rightPanel);

		return this;
	}

	/**
	 * Handles the mouse pressed events to pick up a Piece.
	 * @param event the event information for mouse pressed
	 */
	public void mousePressed(MouseEvent event)
	{
		if (!inGame || currentPlayer == null
				|| currentPlayer instanceof Computer || selectedPiece != null)
			return;

		Point clickedPoint = event.getPoint();

		// Find which piece was pressed on
		for (Piece piece : currentPlayer.getAlivePieces())
			if (piece.contains(clickedPoint))
			{
				// Pick up the piece
				ChessPoint point = piece.getPoint();
				selectedPiece = point.getPiece();
				sourcePoint = point;
				lastPoint = clickedPoint;

				// Highlight its valid moves
				validMoves = selectedPiece.generateMoves(true);
				for (ChessPoint validPoint : validMoves)
					validPoint.setHighlighted(true);

				repaint();
				return;
			}

	}

	/**
	 * Handles the mouse released events to drop a Piece on a Point
	 * @param event the event information for mouse released
	 */
	public void mouseReleased(MouseEvent event)
	{
		if (!inGame || selectedPiece == null)
			return;

		Point pieceCentre = selectedPiece.getCentre();

		// Find which ChessPoint the selected piece was dropped on
		for (ChessPoint[] row : board)
			for (ChessPoint point : row)
				// If the move is valid carry it through
				if (point.contains(pieceCentre)
						&& validMoves.contains(point))
				{
					// Move the piece and add the move to the moves list
					Piece captured = selectedPiece.placeOn(point);
					moves.addLast(new Move(sourcePoint, point,
							selectedPiece, captured));
					parentFrame.setUndoOption(true);

					// Update the piece values of pieces that have decay/growth
					Cannon.updatePieceValue(moves.size());
					Horse.updatePieceValue(moves.size());

					// Unhighlight valid points after move is done
					selectedPiece = null;
					for (ChessPoint validPoint : validMoves)
						validPoint.setHighlighted(false);
					repaint();

					// Check for a winner and change turns
					if (checkForWinner())
						win();
					else
						changeTurn();

					repaint();
					return;
				}

		// Return the piece to original spot if not a valid move and also
		// unhighlight the valid ChessPoints
		for (ChessPoint validPoint : validMoves)
			validPoint.setHighlighted(false);

		selectedPiece.placeOn(sourcePoint);
		selectedPiece = null;
		validMoves = null;

		repaint();

	}

	/**
	 * Handles the mouse moved events to show which Pieces can be picked up
	 * @param event the event information for mouse moved
	 */
	public void mouseMoved(MouseEvent event)
	{
		if (!inGame || currentPlayer == null)
			return;

		// Change the mouse pointer to the hand if it is hovering over a movable
		// piece (only pieces of the current player)
		Point mousePoint = event.getPoint();
		for (Piece piece : currentPlayer.getAlivePieces())
			if (piece.contains(mousePoint))
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				return;
			}

		// Otherwise use the default cursor
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * Handles the mouse dragged events to drag the moving Piece
	 * @param event the event information for mouse dragged
	 */
	public void mouseDragged(MouseEvent event)
	{
		if (!inGame)
			return;

		Point mousePoint = event.getPoint();

		// Move the selected piece along with the mouse
		if (selectedPiece != null)
		{
			selectedPiece.move(lastPoint, mousePoint);
			lastPoint = mousePoint;
			repaint();
		}
	}

	// Unused mouse methods
	public void mouseClicked(MouseEvent event)
	{
	}

	public void mouseEntered(MouseEvent event)
	{
	}

	public void mouseExited(MouseEvent event)
	{
	}

}
