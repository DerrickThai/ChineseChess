/**
 * Keeps track of a move including the piece that was moved, the captured piece
 * if applicable, and the ChessPoints the moved Piece came from and went to. Can
 * execute a move and undo a move. Moves also have a value based on what was
 * moved and captured so that moves can be sorted by value.
 * 
 * @author Derrick Thai
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class Move implements Comparable<Move>
{
	// Variables to keep track of the Pieces and ChessPoints involved in this
	// Move and a the move's value
	private ChessPoint fromPoint, toPoint;
	private Piece moved, captured;
	private int value;

	/**
	 * Constructs a new move object given the Piece that was moved
	 * @param from the ChessPoint the moved Piece came from
	 * @param to the ChessPoint the moved Piece is going to
	 * @param moved the Piece that was moved
	 * @param captured the Piece that was captured
	 */
	public Move(ChessPoint from, ChessPoint to, Piece moved, Piece captured)
	{
		this.fromPoint = from;
		this.toPoint = to;
		this.moved = moved;
		this.captured = captured;

		// Determine the move value
		value = 0;
		if (this.captured != null)
			value += this.captured.pieceValue;

		int[][] posValues = moved.getBoardValues();
		value += posValues[this.toPoint.getRow()][this.toPoint.getColumn()]
				- posValues[this.fromPoint.getRow()][this.fromPoint.getColumn()];

		if (moved.colour == Piece.RED)
		{
			value += posValues[this.toPoint.getRow()][this.toPoint.getColumn()]
					- posValues[this.fromPoint.getRow()][this.fromPoint
							.getColumn()];
		}
		else
			value += posValues[9 - this.toPoint.getRow()][this.toPoint
					.getColumn()]
					- posValues[9 - this.fromPoint.getRow()][this.fromPoint
							.getColumn()];
	}

	/**
	 * Undos this move.
	 * @return true if the undo put back a captured Piece onto the board
	 */
	public boolean undo()
	{
		moved.placeOn(fromPoint);
		toPoint.removePiece();
		if (captured != null)
		{
			captured.isCaptured = false;
			toPoint.addPiece(captured);
		}

		return captured != null;
	}

	/**
	 * Executes this move.
	 * @return true if a Piece was captured or false if not
	 */
	public boolean execute()
	{
		Piece captured = moved.placeOn(toPoint);
		return captured != null;
	}

	/**
	 * 
	 * @param other
	 * @return
	 */
	public int compareTo(Move other)
	{
		return other.value - this.value;
	}

	/**
	 * 
	 * @return
	 */
	public ChessPoint getToPoint()
	{
		return toPoint;
	}

	/**
	 * 
	 * @return
	 */
	public ChessPoint getFromPoint()
	{
		return fromPoint;
	}

	/**
	 * 
	 * @return
	 */
	public Piece getMoved()
	{
		return moved;
	}

	/**
	 * 
	 */
	public String toString()
	{
		return Integer.toString(value);
		// return String.format("Move: %s From: %s To: %s Captured: %s",
		// moved, fromPoint, toPoint, captured);
	}
}
