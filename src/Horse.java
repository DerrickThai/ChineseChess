import java.util.HashSet;

/**
 * Keeps track of a Horse object.Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to.
 * 
 * @author Derrick Thai and Riddle Li
 * @version January 20, 2015
 *
 */
public class Horse extends Piece
{
	private static final long serialVersionUID = 1L;

	// 2 arrays of integers to alter this piece's column and row indexes
	static final int[] DROW_BLOCK = { -1, 0, 0, +1, +1, 0, 0, -1 };
	static final int[] DCOL_BLOCK = { 0, +1, +1, 0, 0, -1, -1, 0 };

	static final int[] DROW = { -2, -1, +1, +2, +2, +1, -1, -2 };
	static final int[] DCOL = { +1, +2, +2, -1, +1, -2, -2, -1 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 280;

	final static int[][] BOARD_VALUE = { { 4, 8, 16, 12, 4, 12, 16, 8, 4 },
			{ 4, 10, 28, 16, 8, 16, 28, 10, 4 },
			{ 12, 14, 16, 20, 18, 20, 16, 14, 12 },
			{ 8, 24, 18, 24, 20, 24, 18, 24, 8 },
			{ 6, 16, 14, 18, 16, 18, 14, 16, 6 },
			{ 4, 12, 16, 14, 12, 14, 16, 12, 4 },
			{ 2, 6, 8, 6, 10, 6, 8, 6, 2 },
			{ 4, 2, 8, 8, 4, 8, 8, 2, 4 },
			{ 0, 2, 4, 4, -2, 4, 4, 2, 0 },
			{ 0, -4, 0, 0, 0, 0, 0, -4, 0 } };

	/**
	 * Constructs a new Horse piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public Horse(int colour, int pieceNo)
	{

		super(COLOUR_NAMES[colour] + "Horse.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = Horse.pieceValue;
		this.maxMoves = 8;
	}

	/**
	 * Gets the 2-D array of Position values of the Red Horse @ return this
	 * Piece's position values
	 */
	public int[][] getBoardValues()
	{
		return BOARD_VALUE;
	}

	public static void updatePieceValue(int noOfMoves)
	{
		pieceValue = 280 + noOfMoves / 3;
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

		// Go through the 8 directions which this piece can travel to.
		// Check if the points this piece is moving to is empty or has an
		// opponent's piece.
		// Check if this piece is being blocked.
		for (int move = 0; move < 8; move++)
		{
			int row = this.point.getRow() + DROW[move];
			int col = this.point.getColumn() + DCOL[move];

			if (row >= 0 && row <= 9 && col >= 0 && col <= 8)

				if (!board[this.point.getRow() + DROW_BLOCK[move]][this.point
						.getColumn() + DCOL_BLOCK[move]].hasPiece())
					if (!board[row][col].hasPiece()
							|| !board[row][col].getPiece().sameColour(this))
						// Add the point as a move if those condition are met.
						moves.add(board[row][col]);

		}

		// remove illegal moves
		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
	}
}
