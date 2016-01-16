import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;

import javax.swing.ImageIcon;

/**
 * Keeps track of a generic Piece object. Keeps track of a Chinese chess piece
 * and its data including the colour, image and point on the board it is
 * currently located on. Can construct a new Piece object with a given file name
 * and a unique number (used to identify the piece). Can set this Piece's point
 * position to a given point, or move to another point. Can also be placed on a
 * ChessPoint on the board. Contains behaviour to trim off any moves that would
 * result in a check. Can also determine's this Piece's piece value, flexibility
 * value and positional value.
 * 
 * @author Riddle and Derrick
 * @version January 20, 2015
 */
public abstract class Piece extends Rectangle
{
	private static final long serialVersionUID = 1L;

	// Uses integers to represent the red and black colours.
	public static final int RED = 0;
	public static final int BLACK = 1;

	// Static variables to keep track of the actual colour, as well as the board
	// which all pieces will be placed on.
	protected static ChessPoint[][] board;
	protected static final Color[] COLOURS = { Color.RED, Color.GRAY };
	public static final String[] COLOUR_NAMES = { "Red", "Black" };

	// Keeps track of width and height of the piece
	public static final int WIDTH = GamePanel.SPACING - 20;
	public static final int HEIGHT = GamePanel.SPACING - 20;

	// Keeps track of the ChessPoint which both generals are on.
	public static ChessPoint[] posOfGeneral = new ChessPoint[2];

	// Instance variables to keep track of the ChessPoint this piece is on, its
	// colour and image as well as whether it is captured or not.
	protected ChessPoint point;
	protected int colour;
	protected boolean isCaptured;
	protected Image image;

	private int pieceNo;
	// Keeps track of this piece's maximum number of moves and its piece value
	// for board evaluation use.
	protected int pieceValue;
	protected int maxMoves;

	/**
	 * Initializes the board which all pieces will be placed on. Also initialize
	 * the original positions of the generals.
	 * 
	 * @param board The array of chessPoints which every piece will be placed
	 *            on.
	 */
	public static void linkBoard(ChessPoint[][] board)
	{
		Piece.board = board;
		posOfGeneral[RED] = board[9][4];
		posOfGeneral[BLACK] = board[0][4];

	}

	/**
	 * Constructs a new Piece object given the image file name and its unique
	 * piece number.
	 * 
	 * @param fileName The name of the image file.
	 * @param pieceNo A unique number used to identify this piece.
	 */
	public Piece(String fileName, int pieceNo)
	{
		super(new Dimension(WIDTH, HEIGHT));
		// Set this piece to being not captured originally.
		isCaptured = false;
		this.pieceNo = pieceNo;
		// Get its image from the image folder and resize it to fit the piece's
		// size.
		image = new ImageIcon("images/" + fileName).getImage();
		Image resized = image.getScaledInstance(WIDTH, HEIGHT,
				java.awt.Image.SCALE_SMOOTH);
		image = new ImageIcon(resized).getImage();
	}

	/**
	 * Moves this Piece object to a new point.
	 * 
	 * @param initialPos The point that this Piece was originally on
	 * @param finalPos The point that this Piece will be moved to
	 */
	public void move(Point initialPos, Point finalPos)
	{
		x += finalPos.x - initialPos.x;
		y += finalPos.y - initialPos.y;
	}

	/**
	 * Sets this Piece's position to a given Point.
	 * 
	 * @param point the Point to set this Piece's position to.
	 */
	public void setPosition(Point point)
	{
		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Places this Piece onto a new Point on the Chess board and removes this
	 * Piece from its current Chess Point
	 * 
	 * @param toPoint The Chess Point to move to
	 * @return The Piece that was captured by this Piece, if it exists
	 */
	public Piece placeOn(ChessPoint toPoint)
	{

		// Remove this Piece from its parent Chess Point, and set this Piece's
		// position to the new given point.
		if (point != null)
			point.removePiece();
		point = toPoint;

		// Store the captured Piece if there are any by removing it from the
		// given Point.
		Piece captured = null;
		if (point.hasPiece())
		{
			captured = point.capturePiece(this);
			captured.isCaptured = true;
		}
		else
			point.addPiece(this);

		// Align the piece
		x = point.x + (point.width - this.width) / 2;
		y = point.y + (point.height - this.height) / 2;

		// Update the static positions of the generals if a general was moved.
		if (this instanceof General)
		{
			if (this.colour == Piece.BLACK)
				Piece.posOfGeneral[BLACK] = toPoint;
			else
				Piece.posOfGeneral[RED] = toPoint;
		}

		return captured;
	}

	/**
	 * Getter for this Piece's chess point
	 * 
	 * @return this Piece's chess point
	 */
	public ChessPoint getPoint()
	{
		return point;
	}

	/**
	 * Removes the reference of this Piece's chess point
	 */
	public void removeFromPoint()
	{
		point = null;
	}

	/**
	 * Determines if this piece is the same colour as another piece.
	 * 
	 * @param other the other piece to compare this piece to
	 * @return true if they are the same colour, false otherwise
	 */
	public boolean sameColour(Piece other)
	{
		return this.colour == other.colour;
	}

	/**
	 * Returns the point on this Piece's center
	 * 
	 * @return this piece's center as a Point
	 */
	public Point getCentre()
	{
		return new Point(x + width / 2, y + height / 2);
	}

	/**
	 * Getter for this piece's frame.
	 * 
	 * @return this piece itself as a rectangle
	 */
	public Rectangle getRect()
	{
		return this;
	}

	/**
	 * Gets this Piece's piece value
	 * 
	 * @return this piece's piece value
	 */
	public int getPieceValue()
	{
		return this.pieceValue;
	}

	/**
	 * Returns this Piece's position value.
	 * 
	 * @return this Piece's position value
	 */
	public int getPositionValue()
	{
		// store the 2-D array of integers which represent this Piece's position
		// values
		int[][] boardValues = getBoardValues();
		// Return the integer that corresponds to this Piece's position on the
		// board.
		if (this.colour == Piece.RED)
			return boardValues[point.getRow()][point.getColumn()];
		return boardValues[9 - point.getRow()][point.getColumn()];
	}

	/**
	 * Calculates this piece's Flexibility value.
	 * 
	 * @return this Piece's flexibility value
	 */
	public double getFlexibilityValue()

	{
		// We ignore the flexibility of soldiers, elephants, advisors and
		// generals, since they aren't important.
		if (this instanceof Soldier || this instanceof Elephant
				|| this instanceof Advisor || this instanceof General)
			return 0;
		// The flexibility value is calculated by the ratio between this piece's
		// current number of moves and its maximum number of moves multiplied by
		// 1/4th of this piece's piece value;
		return this.generateMoves(true).size() / this.maxMoves
				* this.getPieceValue() / 4;
	}

	/**
	 * Draws this piece using its image.
	 * 
	 * @param g the graphics
	 * @param xOffset the amount of horizontal offset
	 * @param yOffset the amount of vertical offset.
	 */
	public void draw(Graphics g, int xOffset, int yOffset)
	{
		if (!isCaptured)

			g.drawImage(image, x + xOffset, y + yOffset, null);
	}

	/**
	 * Tries out each of this Piece's possible moves and removes the ones that
	 * would result in this Piece's general getting checked. (removes the
	 * illegal moves).
	 * @author Riddle Li
	 * @param allMoves The hash set of all the chess points that this piece can
	 *            move to including the illegal ones
	 * @return a hashset of all the legal points which this piece can move to
	 */
	public HashSet<ChessPoint> removeExtraMoves(HashSet<ChessPoint> allMoves)
	{
		HashSet<ChessPoint> allLegitMoves = new HashSet<ChessPoint>(
				allMoves.size());

		// For each possible move of this piece:
		for (ChessPoint eachMove : allMoves)
		{
			// Try each move out, store the piece on the chess point that this
			// piece is moving to
			this.point.removePiece();
			Piece piece = eachMove.getPiece();
			eachMove.addPiece(this);

			// If the general on the same side as this piece isn't in check,
			// then its a legit move.
			if (this.colour == Piece.RED)
			{
				if (!(((General) Piece.posOfGeneral[RED].getPiece())
						.checkForCheck(Piece.posOfGeneral[RED])))
					allLegitMoves.add(eachMove);
			}
			else if (!(((General) Piece.posOfGeneral[BLACK].getPiece())
					.checkForCheck(Piece.posOfGeneral[BLACK])))
				allLegitMoves.add(eachMove);

			// return the board back to how it was before each move was tried
			eachMove.addPiece(piece);
			this.point.addPiece(this);

		}
		return allLegitMoves;
	}

	/**
	 * Determines if this Piece is exactly the same piece as a given piece.
	 * @param other the object being compared to this piece
	 * @return true if both pieces are the same, false otherwise
	 */
	public boolean equals(Object other)
	{
		if (!(other instanceof Piece))
			return false;
		// The piece number is unique to each piece, so they can be directly
		// compared.
		return this.pieceNo == (((Piece) other).pieceNo);
	}

	/**
	 * Produces this piece's hash-code
	 * @return this object's hash code
	 */
	public int hashCode()
	{
		return pieceNo;
	}

	/**
	 * Checks if this piece can be placed on a given chess point
	 * @param point the point to check if this piece can be placed on it
	 * @return true if this piece can be placed on the given point, false
	 *         otherwise
	 */
	public boolean canPlaceOn(ChessPoint point)
	{
		// Check if the given point is contained within this piece's legal
		// moves.
		HashSet<ChessPoint> moves = this.generateMoves(true);
		return moves.contains(point);
	}

	/**
	 * obtains the position value array of this specific piece.
	 * @return the 2 d array of position values
	 */
	abstract int[][] getBoardValues();

	/**
	 * Generates a list of chess point which this piece can move to
	 * @param removeExtra whether if we want to remove all the illegal moves
	 * @return the hash set of chess points that this piece can move to
	 */
	abstract public HashSet<ChessPoint> generateMoves(boolean removeExtra);

}
