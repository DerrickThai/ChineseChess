import java.util.HashSet;

/**
 * Keeps track of a Advisor object. Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to.
 * 
 * @author Derrick Thai and Riddle Li
 * @version January 20, 2015
 *
 */
public class Advisor extends Piece
{
	private static final long serialVersionUID = 1L;
	// 2 arrays of integers to alter this piece's column and row indexes
	static int[] DROW = { -1, +1, +1, -1 };
	static int[] DCOL = { +1, +1, -1, -1 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 120;

	final static int[][] BOARD_VALUE = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, -1, 0, -1, 0, 0, 0 }, { 0, 0, 0, 0, 3, 0, 0, 0, 0 },
			{ 0, 0, 0, 1, 0, 1, 0, 0, 0 } };

	/**
	 * Constructs a new Advisor piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public Advisor(int colour, int pieceNo)
	{

		super(COLOUR_NAMES[colour] + "Advisor.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = Advisor.pieceValue;
		this.maxMoves = 4;
	}

	/**
	 * Gets the 2-D array of Position values of the Red Advisor @ return this
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

			if (row >= 0 && row < GamePanel.NO_OF_ROWS && col >= 3 && col <= 5)
				if (!board[row][col].hasPiece()
						|| !board[row][col].getPiece().sameColour(this))
					// Make sure this piece is within the palace.
					if ((this.colour == 1 && row <= 2)
							|| (this.colour == 0 && row >= 7))
						moves.add(board[row][col]);
		}
		// remove illegal moves
		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
	}

}
