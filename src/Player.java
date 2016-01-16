import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Keeps track of a Player and its pieces plus its colour. Can get its
 * 
 * @author Derrick Thai and Riddle Li
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class Player
{
	// Constants
	public static final int RED = 0;
	public static final int BLACK = 1;

	public static final int NO_CHECK = 0;
	public static final int CHECK = 1;

	private static final int ROW_POS[][] = {
			{ 6, 6, 6, 6, 6, 7, 7, 9, 9, 9, 9, 9, 9, 9, 9, 9 },
			{ 3, 3, 3, 3, 3, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };

	private static final int COL_POS[] = { 0, 2, 4, 6, 8, 1, 7, 0, 8, 1, 7, 2,
			6, 3, 5, 4 };

	// Pieces and colour
	protected Piece[] pieces;
	private int colour;
	private boolean inTurn;
	private LinkedHashSet<Piece> capturedPieces;

	/**
	 * Constructs a new Player object with the given colour.
	 * @param colour the colour of this Player.
	 */
	public Player(int colour)
	{
		this.colour = colour;
		pieces = new Piece[16];
		inTurn = false;

		int pieceNo = 0;
		pieces[pieceNo++] = new Soldier(colour, pieceNo);
		pieces[pieceNo++] = new Soldier(colour, pieceNo);
		pieces[pieceNo++] = new Soldier(colour, pieceNo);
		pieces[pieceNo++] = new Soldier(colour, pieceNo);
		pieces[pieceNo++] = new Soldier(colour, pieceNo);

		pieces[pieceNo++] = new Cannon(colour, pieceNo);
		pieces[pieceNo++] = new Cannon(colour, pieceNo);

		pieces[pieceNo++] = new Chariot(colour, pieceNo);
		pieces[pieceNo++] = new Chariot(colour, pieceNo);

		pieces[pieceNo++] = new Horse(colour, pieceNo);
		pieces[pieceNo++] = new Horse(colour, pieceNo);

		pieces[pieceNo++] = new Elephant(colour, pieceNo);
		pieces[pieceNo++] = new Elephant(colour, pieceNo);

		pieces[pieceNo++] = new Advisor(colour, pieceNo);
		pieces[pieceNo++] = new Advisor(colour, pieceNo);

		pieces[pieceNo++] = new General(colour, pieceNo);

		// 22 to avoid rehashing
		capturedPieces = new LinkedHashSet<>(22);
	}

	/**
	 * Determines the colour of this piece.
	 * @return this Pieces colour.
	 */
	public int getColour()
	{
		return colour;
	}

	/**
	 * Sets this player's turn to true or false
	 * @param turn true if it's this player's turn or false if not
	 * @return this Player
	 */
	public Player setTurn(boolean turn)
	{
		inTurn = turn;
		return this;
	}

	/**
	 * Determine if it's this Player's turn.
	 * @return true if it's this player's turn or false if not
	 */
	public boolean isTurn()
	{
		return inTurn;
	}

	/**
	 * Gets the status of this Player (check or not)
	 * @return the status of this player
	 */
	public int getStatus()
	{
		if (((General) pieces[pieces.length - 1])
				.checkForCheck(Piece.posOfGeneral[colour]))
			return Player.CHECK;
		return Player.NO_CHECK;
	}

	/**
	 * Resets the pieces of this player to original locations.
	 * @param board the board to reset the pieces on
	 */
	public void resetPieces(ChessPoint[][] board)
	{
		for (int piece = 0; piece < pieces.length; piece++)
		{
			pieces[piece].isCaptured = false;
			pieces[piece].point = null;

			pieces[piece]
					.placeOn(board[ROW_POS[colour][piece]][COL_POS[piece]]);
		}

		capturedPieces.clear();
	}

	/**
	 * Gets the alive pieces of this Player.
	 * @return a set of this player's alive pieces
	 */
	public ArrayList<Piece> getAlivePieces()
	{
		// Order does not not matter so use ArrayList
		ArrayList<Piece> alivePieces = new ArrayList<Piece>(16);

		for (Piece piece : pieces)
			if (!piece.isCaptured)
				alivePieces.add(piece);

		return alivePieces;
	}

	/**
	 * Gets the captured pieces of this Player.
	 * @return a set of this player's captured pieces
	 */
	public Set<Piece> getCapturedPieces()
	{
		// Order matters so use LinkedHashSet
		for (Piece piece : pieces)
			if (piece.isCaptured)
			{
				if (!capturedPieces.contains(piece))
					capturedPieces.add(piece);
			}
			else if (capturedPieces.contains(piece))
				capturedPieces.remove(piece);

		return capturedPieces;
	}

}
