//import java.awt.Graphics;
import java.util.HashSet;

/**
 * Keeps track of a General object.Includes data for this Piece's piece value.
 * Also includes behaviour to generate a list of chess points which this piece
 * can move to. Can also determine if it is being checked.
 * 
 * @author Riddle Li
 * @version January 20, 2015
 *
 */
public class General extends Piece
{
	private static final long serialVersionUID = 1L;
	// 2 arrays of integers to alter this piece's column and row indexes
	static int[] DROW = { -1, 0, +1, 0 };
	static int[] DCOL = { 0, +1, 0, -1 };

	// Variables to store this piece's piece value and positional values.
	// These can be used to calculate this piece's numerical worth in a chess
	// game.
	public static int pieceValue = 6000;

	final static int[][] BOARD_VALUE = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, -2, -2, -2, 0, 0, 0 },
			{ 0, 0, 0, -2, -2, -2, 0, 0, 0 },
			{ 0, 0, 0, -2, 2, -2, 0, 0, 0 } };

	/**
	 * Constructs a new General piece given the colour and unique piece number.
	 * @param colour the colour of this piece.
	 * @param pieceNo the unique piece number.
	 */
	public General(int colour, int pieceNo)
	{
		super(COLOUR_NAMES[colour] + "General.png", pieceNo);
		// initializes this Piece's colour, piece value, and maximum number of
		// moves (calculated by head)
		this.colour = colour;
		super.pieceValue = General.pieceValue;
		this.maxMoves = 4;
	}

	/**
	 * Gets the 2-D array of Position values of the Red General @ return this
	 * Piece's position values
	 */
	public int[][] getBoardValues()
	{
		return BOARD_VALUE;
	}

	/**
	 * Check if this Piece is under check
	 * @param generalPoint This position of the general
	 * @return true if this piece is under check, false if not
	 */
	public boolean checkForCheck(ChessPoint generalPoint)
	{
		// Search the entire board for enemy Pieces
		for (int row = 0; row < GamePanel.NO_OF_ROWS; row++)
			for (int col = 0; col < GamePanel.NO_OF_COLS; col++)
				if (board[row][col].hasPiece()
						&& !board[row][col].getPiece().sameColour(this))
					// We don't check elephants and advisors because they can
					// never check
					if (!(board[row][col].getPiece() instanceof Elephant)
							&& !(board[row][col].getPiece() instanceof Advisor))
					{
						// If that piece's possible moves includes the point
						// that this general is on, return true.
						HashSet<ChessPoint> moves = board[row][col].getPiece()
								.generateMoves(false);

						if (moves.contains(generalPoint))
							return true;
					}

		return false;
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
			if (row >= 0 && row <= 9 && col >= 3 && col <= 5)
			{
				if (!board[row][col].hasPiece()
						|| !board[row][col].getPiece().sameColour(this))
				{
					// Check if this piece is within the palace.
					if ((this.colour == Piece.BLACK && row < 3)
							|| (this.colour == Piece.RED && row > 6))
						moves.add(board[row][col]);
					// Check if this piece is facing the opponent's general
					while (DCOL[move] == 0 && row >= 0 && row <= 9
							&& !board[row][col].hasPiece())
						row += DROW[move];

					if (row >= 0 && row <= 9)
						if (board[row][col].getPiece() instanceof General)
							moves.add(board[row][col]);
				}
			}
		}
		// Remove extra moves
		if (removeExtra)
			moves = this.removeExtraMoves(moves);
		return moves;
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

			if (this.colour == Piece.BLACK)
				Piece.posOfGeneral[Piece.BLACK] = eachMove;
			else
				Piece.posOfGeneral[Piece.RED] = eachMove;

			// If the general on the same side as this piece isn't in check,
			// then its a legit move.
			if (this.colour == Piece.RED)
			{
				if (!(((General) Piece.posOfGeneral[Piece.RED].getPiece())
						.checkForCheck(Piece.posOfGeneral[Piece.RED])))
					allLegitMoves.add(eachMove);
			}
			else if (!(((General) Piece.posOfGeneral[Piece.BLACK].getPiece())
					.checkForCheck(Piece.posOfGeneral[Piece.BLACK])))
				allLegitMoves.add(eachMove);

			// return the board back to how it was before each move was tried
			eachMove.addPiece(piece);
			this.point.addPiece(this);

			if (this.colour == Piece.BLACK)
				Piece.posOfGeneral[Piece.BLACK] = this.point;
			else
				Piece.posOfGeneral[Piece.RED] = this.point;

		}
		return allLegitMoves;
	}
}
