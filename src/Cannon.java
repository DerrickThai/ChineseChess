import java.util.HashSet;

/**
 * Keeps track of a Cannon object.Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to.
 * 
 * @author Riddle Li
 * @version January 20, 2015
 *
 */
public class Cannon extends Piece
{

	private static final long serialVersionUID = 1L;
	// 2 arrays of integers to alter this piece's column and row indexes
	static int[] DROW = { -1, 0, +1, 0 };
	static int[] DCOL = { 0, +1, 0, -1 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 290;

	final static int[][] BOARD_VALUE = { { 6, 4, 0, -10, -12, -10, 0, 4, 6 },
			{ 2, 2, 0, -4, -14, -4, 0, 2, 2 },
			{ 2, 2, 0, -10, -8, -10, 0, 2, 2 },
			{ 0, 0, -2, 4, 10, 4, -2, 0, 0 },
			{ 0, 0, 0, 2, 8, 2, 0, 0, 0 },
			{ -2, 0, 4, 2, 6, 2, 4, 0, -2 },
			{ 0, 0, 0, 2, 4, 2, 0, 0, 0 },
			{ 4, 0, 8, 6, 10, 6, 8, 0, 4 },
			{ 0, 2, 4, 6, 6, 6, 4, 2, 0 },
			{ 0, 0, 2, 6, 6, 6, 2, 0, 0 } };

	/**
	 * Constructs a new Cannon piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public Cannon(int colour, int pieceNo)
	{
		super(COLOUR_NAMES[colour] + "Cannon.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = Cannon.pieceValue;
		this.maxMoves = 17;
	}

	public static void updatePieceValue(int noOfMoves)
	{
		pieceValue = 290 - noOfMoves / 3;
	}

	/**
	 * Gets the 2-D array of Position values of the Red Cannon @ return this
	 * Piece's position values
	 */
	public int[][] getBoardValues()
	{
		return BOARD_VALUE;
	}

	/**
	 * Generates a list of chess points which this piece can move to
	 * @param removeExtra whether if we want to remove all the illegal moves
	 * @return the hash set of chess points that this piece can move to
	 */
	public HashSet<ChessPoint> generateMoves(boolean removeExtra)
	{
		HashSet<ChessPoint> moves = new HashSet<ChessPoint>();
		// A captures piece has no possible moves.
		if (isCaptured)
			return moves;
		// Go through the 4 directions which this piece can travel to.
		// Check if the points this piece is moving to is empty or has an
		// opponent's piece.
		for (int move = 0; move < 4; move++)
		{
			int row = this.point.getRow() + DROW[move];
			int col = this.point.getColumn() + DCOL[move];

			// Keep going in one direction until a piece is reached or it
			// becomes out of bounds.
			while (row >= 0 && row <= 9 && col >= 0 && col <= 8
					&& !board[row][col].hasPiece())
			{
				moves.add(board[row][col]);
				row += DROW[move];
				col += DCOL[move];

			}

			// If a piece is reached, then keep checking beyond that piece for
			// an enemy piece to capture.
			if (row >= 0 && row <= 9 && col >= 0 && col <= 8)
			{
				row += DROW[move];
				col += DCOL[move];
				while (row >= 0 && row <= 9 && col >= 0 && col <= 8
						&& !board[row][col].hasPiece())
				{
					row += DROW[move];
					col += DCOL[move];

				}
				// If a piece is found, make sure its an enemy piece

				if (row >= 0 && row <= 9 && col >= 0 && col <= 8)
					if (!board[row][col].getPiece().sameColour(this))
						moves.add(board[row][col]);
			}
		}
		// remove illegal moves
		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
	}
}
