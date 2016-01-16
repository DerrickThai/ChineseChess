//import java.awt.Graphics;
import java.util.HashSet;

/**
 * Keeps track of a Elephant object.Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to.
 * 
 * @author Riddle Li
 * @version January 20, 2015
 *
 */
public class Elephant extends Piece
{

	private static final long serialVersionUID = 1L;
	// 2 arrays of integers to alter this piece's column and row indexes
	static final int[] DROW_BLOCK = { -1, +1, +1, -1 };
	static final int[] DCOL_BLOCK = { +1, +1, -1, -1 };

	static final int[] DROW = { -2, +2, +2, -2 };
	static final int[] DCOL = { +2, +2, -2, -2 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 130;

	static final int[][] BOARD_VALUE = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, -1, 0, 0, 0, -1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ -2, 0, 0, 0, 3, 0, 0, 0, -2 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 1, 0, 0, 0, 1, 0, 0 } };

	/**
	 * Constructs a new Elephant piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public Elephant(int colour, int pieceNo)
	{

		super(COLOUR_NAMES[colour] + "Elephant.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = Elephant.pieceValue;
		this.maxMoves = 4;
	}

	/**
	 * Gets the 2-D array of Position values of the Red Elephant @ return this
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
		// Check if this Piece is being blocked.
		for (int move = 0; move < 4; move++)
		{
			int row = this.point.getRow() + DROW[move];
			int col = this.point.getColumn() + DCOL[move];

			if (row >= 0 && row <= 9 && col >= 0 && col <= 8)
				if (!board[this.point.getRow() + DROW_BLOCK[move]][this.point
						.getColumn() + DCOL_BLOCK[move]].hasPiece())
					if (!board[row][col].hasPiece()
							|| !board[row][col].getPiece().sameColour(this))
						// Check to make sure this piece didn't cross the river
						if ((this.colour == 1 && row <= 4)
								|| (this.colour == 0 && row >= 5))
							moves.add(board[row][col]);
		}
		// Remove extra moves

		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
	}

}
