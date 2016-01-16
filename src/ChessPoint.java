import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Keeps track of a ChessPoint object including its Piece, whether it is
 * highlighted, and its row and column. Can add and remove Pieces from this
 * ChessPoint and draws the point as well as its Piece onto the board.
 * 
 * @author Derrick Thai
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class ChessPoint extends Rectangle
{
	// To remove the warning
	private static final long serialVersionUID = 1L;

	// Constants for the piece radius and highlighted colour
	private static final int RADIUS = (int) (GamePanel.SPACING / 5.5);
	private static final Color HIGHTLIGHT = new Color(124, 252, 0, 120);

	// Instance variables to keep track of this ChessPoint's piece, row and
	// column of the board, and highlighted status
	private Piece piece;
	private boolean isHighlighted;
	private int row, col;

	/**
	 * Constructs a new ChessPoint with the given row and column.
	 * @param row the row index of the board for this ChessPoint
	 * @param col the column index of the board for this ChessPoint
	 */
	ChessPoint(int row, int col)
	{
		this.row = row;
		this.col = col;

		// We extend rectangle so we must set our dimension and coordinates
		this.width = GamePanel.SPACING;
		this.height = GamePanel.SPACING;

		this.x = (col + 1) * width - width / 2;
		this.y = (row + 1) * height - height / 2;
	}

	/**
	 * Determines if this ChessPoint has a Piece on it.
	 * @return true if there is a Piece on this ChessPoint or false if there is
	 *         not a Piece on this ChessPoint
	 */
	public boolean hasPiece()
	{
		return piece != null;
	}

	/**
	 * Adds the given piece to this ChessPoint, but assumes there is no Piece
	 * currently on this ChessPoint.
	 * @param piece the Piece to add to this ChessPoint
	 */
	public void addPiece(Piece piece)
	{
		this.piece = piece;
	}

	/**
	 * Adds the given piece to this ChessPoint, capturing the Piece that was
	 * originally on this ChessPoint and returns it.
	 * @param piece the Piece to add to this ChessPoint
	 * @return the captured Piece that was originally on this ChessPoint
	 */
	public Piece capturePiece(Piece piece)
	{
		Piece capturedPiece = this.piece;
		this.piece = piece;
		return capturedPiece;
	}

	/**
	 * Removes the current Piece from this ChessPoint.
	 */
	public void removePiece()
	{
		this.piece = null;
	}

	/**
	 * Gets the current Piece on this ChessPoint.
	 * @return the Piece on this ChessPoint or null if there is none
	 */
	public Piece getPiece()
	{
		return piece;
	}

	/**
	 * Highlights or unhighlights this ChessPoint.
	 * @param isHighlighted true if this ChessPoint is to be highlighted or
	 *            false if it is to be unhighlighted
	 */
	public void setHighlighted(boolean isHighlighted)
	{
		this.isHighlighted = isHighlighted;
	}

	/**
	 * Gets the row index of the board for this ChessPoint.
	 * @return this ChessPoint's row index
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * Gets the column index of the board for this ChessPoint.
	 * @return this ChessPoint's column index
	 */
	public int getColumn()
	{
		return col;
	}

	/**
	 * Draws this ChessPoint and its Piece if it has one.
	 * @param g The Graphics context to do the drawing
	 */
	public void draw(Graphics g)
	{
		// The little point on the board
		if (isHighlighted)
			g.setColor(Color.GREEN);
		else
			g.setColor(Color.DARK_GRAY);

		g.fillOval(x + width / 2 - RADIUS / 2, y + height / 2 - RADIUS / 2,
				RADIUS, RADIUS);

		g.setColor(Color.BLACK);
		g.drawOval(x + width / 2 - RADIUS / 2, y + height / 2 - RADIUS / 2,
				RADIUS, RADIUS);

		// The Piece on this point
		if (piece != null)
			piece.draw(g, 0, 0);

		// Show there is a Piece covering the highlighted point, highlight the
		// entire piece to show that it can be captured
		if (isHighlighted && piece != null)
		{
			Rectangle rect = piece.getRect();
			g.setColor(HIGHTLIGHT);
			g.fillOval(rect.x, rect.y, rect.width, rect.height);
		}
	}

	/**
	 * Generates a unique hash code for this ChessPoint.
	 * @return this ChessPoint's hash code
	 */
	public int hashCode()
	{
		return row * 9 + col;
	}

	/**
	 * Determines if this ChessPoint is equal to the give ChessPoint
	 * @param other the other ChessPoint to check for equality
	 * @return true if this ChessPoint is equal to the other ChessPoint of false
	 *         if they are not equal
	 */
	public boolean equals(Object other)
	{
		if (!(other instanceof ChessPoint))
			return false;

		ChessPoint otherPoint = (ChessPoint) other;
		return this.row == otherPoint.row && this.col == otherPoint.col;
	}

	/**
	 * Returns a string representation of this ChessPoint for debugging
	 * @return this ChessPoint's row and column
	 */
	public String toString()
	{
		return String.format("[Row: %d Col: %d]", row, col);
	}
}
