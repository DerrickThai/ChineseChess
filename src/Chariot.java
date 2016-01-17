//import java.awt.Graphics;
import java.util.HashSet;

/**
 * Keeps track of a Chariot object.Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to.
 * 
 * @author Derrick Thai and Riddle Li
 * @version January 20, 2015
 *
 */
public class Chariot extends Piece
{

	private static final long serialVersionUID = 1L;
	// 2 arrays of integers to alter this piece's column and row indexes
	static int[] DROW = { -1, 0, +1, 0 };
	static int[] DCOL = { 0, 1, 0, -1 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 600;

	final static int[][] BOARD_VALUE = {
			{ 14, 14, 12, 18, 16, 18, 12, 14, 14 },
			{ 16, 20, 18, 24, 26, 24, 18, 20, 16 },
			{ 12, 12, 12, 18, 18, 18, 12, 12, 12 },
			{ 12, 18, 16, 22, 22, 22, 16, 18, 12 },
			{ 12, 14, 12, 18, 18, 18, 12, 14, 12 },
			{ 12, 16, 14, 20, 20, 20, 14, 16, 12 },
			{ 6, 10, 8, 14, 14, 14, 8, 10, 6 },
			{ 4, 8, 6, 14, 12, 14, 6, 8, 4 },
			{ 8, 4, 8, 16, 8, 16, 8, 4, 8 },
			{ -2, 10, 6, 14, 12, 14, 6, 10, -2 } };

	/**
	 * Constructs a new Chariot piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public Chariot(int colour, int pieceNo)
	{

		super(COLOUR_NAMES[colour] + "Chariot.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = Chariot.pieceValue;
		this.maxMoves = 17;
	}

	/**
	 * Gets the 2-D array of Position values of the Red Chariot @ return this
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

			// For each direction keep going until you reach a piece,then this
			// piece cannot go further.
			while (row >= 0 && row <= 9 && col >= 0 && col <= 8
					&& !board[row][col].hasPiece())
			{
				moves.add(board[row][col]);
				row += DROW[move];
				col += DCOL[move];

			}

			// Make sure its still in bounds
			if (row >= 0 && row <= 9 && col >= 0 && col <= 8)
				if (!board[row][col].getPiece().sameColour(this))
					moves.add(board[row][col]);
		}
		// remove illegal moves
		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
	}

}
