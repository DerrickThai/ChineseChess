//import java.awt.Graphics;
import java.util.HashSet;

/**
 * Keeps track of a Soldier object.Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to.
 * 
 * @author Riddle Li
 * @version January 20, 2015
 *
 */
public class Soldier extends Piece
{

	private static final long serialVersionUID = 1L;
	// 2 arrays of integers to alter this piece's column and row indexes
	static int[] DROW = { -1, 0, +1, 0 };
	static int[] DCOL = { 0, 1, 0, -1 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 60;

	final static int[][] BOARD_VALUE = { { 0, 3, 6, 9, 12, 9, 6, 3, 0 },
			{ 18, 36, 56, 80, 120, 80, 56, 36, 18 },
			{ 14, 26, 42, 60, 80, 60, 42, 26, 14 },
			{ 10, 20, 30, 34, 40, 34, 30, 20, 10 },
			{ 6, 12, 18, 18, 20, 18, 18, 12, 6 },
			{ 2, 0, 8, 0, 8, 0, 8, 0, 2 },
			{ 0, 0, -2, 0, 4, 0, -2, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

	/**
	 * Constructs a new Soldier piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public Soldier(int colour, int pieceNo)
	{
		super(COLOUR_NAMES[colour] + "Soldier.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = Soldier.pieceValue;
		this.maxMoves = 3;
	}

	/**
	 * Gets the 2-D array of Position values of the Red Soldier @ return this
	 * Piece's position values
	 */
	public int[][] getBoardValues()
	{
		return BOARD_VALUE;
	}

	/**
	 * Generates a list of chess point which this piece can move to
	 * @param removeExtra whether if we want to remove all the illegal moves
	 * @return the hash set of chess points that this piece can move to
	 */
	public HashSet<ChessPoint> generateMoves(boolean removeExtra)
	{
		HashSet<ChessPoint> moves = new HashSet<ChessPoint>();
		// A captures piece has no possible moves.
		if (isCaptured)
			return moves;

		int row = this.point.getRow() - 1;

		if (this.colour == Player.BLACK)
			row = this.point.getRow() + 1;

		int col = this.point.getColumn();

		// Moves this piece forward by 1, and check if that point is empty or
		// has an opponent's piece.
		if (row >= 0 && row <= 9
				&& (!board[row][col].hasPiece() || !board[row][col].getPiece()
						.sameColour(this)))
			moves.add(board[row][col]);

		// Check sideways directions if this piece crossed the river already.
		// Check again if those points contain enemy pieces or are empty.
		for (int sideWays = -1; sideWays <= 1; sideWays += 2)
		{
			if ((colour == 1 && point.getRow() >= 5)
					|| (this.colour == 0 && point.getRow() <= 4))
			{
				row = this.point.getRow();
				col = this.point.getColumn() + sideWays;
				if (col >= 0 && col <= 8
						&& (!board[row][col].hasPiece() || !board[row][col]
								.getPiece().sameColour(this)))
					moves.add(board[row][col]);
			}
		}

		// remove illegal moves
		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
	}

}
