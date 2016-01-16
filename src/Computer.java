import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Keeps track of a Computer Player. It can make its best move and has access to
 * the whole board and the other player's pieces.
 * 
 * @author Derrick Thai and Riddle Li
 * @version v3.1 Final, Last Updated: December 20, 2015
 */
public class Computer extends Player
{
	// Give the computer access to the board and the other player's pieces
	private int depth;
	private ChessPoint[][] board;
	private Player[] players;
	private int currentPlayer;

	/**
	 * Constructs a new Computer given its colour and difficultly.
	 * @param colour the colour of the Computer
	 * @param difficulty the depth to search to
	 */
	public Computer(int colour, int difficulty)
	{
		super(colour);
		depth = difficulty;
	}

	/**
	 * Finds the best move for the AI.
	 * @param board the board of chess points
	 * @param human the human player (to get its pieces)
	 * @param random true if it randomly chooses the pick the second best move
	 * @return the best move the computer can make
	 */
	public Move bestMove(ChessPoint[][] board, Player human, boolean random)
	{
		this.board = board;
		Move[] bestMoves = new Move[2];
		int[] maxValues = { Integer.MIN_VALUE, Integer.MIN_VALUE };

		players = new Player[] { this, human };
		currentPlayer = 0;

		Queue<Move> moves = generateMoves(board);
		if (moves.size() < 2)
		{
			if (!moves.isEmpty())
				bestMoves[0] = moves.remove();
			return bestMoves[0];
		}

		int noOfMoves = 0;
		while (!moves.isEmpty())
		{
			Move move = moves.remove();

			makeMove(move);
			int value = alphaBetaMin(maxValues[0], Integer.MAX_VALUE, depth - 1);
			undoMove(move);

			if (noOfMoves == 0)
			{
				maxValues[0] = value;
				bestMoves[0] = move;
			}
			else if (noOfMoves == 1)
			{
				if (value > maxValues[0])
				{
					maxValues[1] = maxValues[0];
					bestMoves[1] = bestMoves[0];
					maxValues[0] = value;
					bestMoves[0] = move;
				}
				else
				{
					maxValues[1] = value;
					bestMoves[1] = move;
				}
			}
			else if (value > maxValues[0])
			{
				maxValues[0] = value;
				bestMoves[0] = move;
			}
			else if (value > maxValues[1])
			{
				maxValues[1] = value;
				bestMoves[1] = move;
			}
			noOfMoves++;
		}

		if (random && (int) (Math.random() * depth) == 0)
			return bestMoves[1];
		else
			return bestMoves[0];
	}

	/**
	 * Generates all moves from the given board.
	 * @param board the board of chess points
	 * @return a queue of all moves
	 */
	private Queue<Move> generateMoves(ChessPoint[][] board)
	{
		Queue<Move> moves = new PriorityQueue<Move>();

		for (Piece piece : players[currentPlayer].getAlivePieces())
			for (ChessPoint point : piece.generateMoves(true))
				moves.add(new Move(piece.point, point, piece, point.getPiece()));

		return moves;
	}

	/**
	 * Calculates the board value of the current game situation with an
	 * evaluation function. This value is 0 based, negative values are assigned
	 * to red while positive values are assigned to black.
	 * 
	 * @return the board value of the current game situation.
	 */
	private int getBoardValue()
	{
		int boardValue = 0;
		// For each player, go through all their alive pieces.
		for (Player eachPlayer : players)
		{
			double eachPlayerValue = 0;
			for (Piece eachPiece : eachPlayer.getAlivePieces())
			{
				// Add in each piece's piece value, positional value and
				// flexibility value
				eachPlayerValue += eachPiece.getPieceValue()
						+ eachPiece.getPositionValue()
						+ eachPiece.getFlexibilityValue();

			}
			// Add the value to the overall board value if the player is
			// computer (black)
			// Otherwise subtract it from the overall board value if the player
			// is human (red)
			if (eachPlayer instanceof Computer)
				boardValue += (int) (eachPlayerValue);
			else
				boardValue -= (int) (eachPlayerValue);
		}
		// Calculates the central control value by going through the central
		// region of the board.
		// For each computer piece, add in 1/10th of that piece's piece value
		// Do the same for human pieces, but subtract 1/10th of the piece's
		// piece value
		for (int row = 2; row <= 7; row++)
		{
			for (int col = 2; col <= 6; col++)
			{
				Piece piece = board[row][col].getPiece();
				// Don't count generals
				if (!(piece instanceof General))
				{
					if (piece != null && piece.colour == Piece.BLACK)
						boardValue += piece.getPieceValue() / 10;
					else if (piece != null && piece.colour == Piece.RED)
						boardValue -= piece.getPieceValue() / 10;
				}

			}
		}
		return boardValue;
	}

	/**
	 * Recursive alpha beta searching method for the maximizing player that
	 * determines
	 * @param alpha the minimum score that the maximizing player is assured of
	 * @param beta the maximum score that the minimizing player is assured of
	 * @param depth the number of generations that this current generation is
	 *            away from the target depth
	 * @return the maximizing player's best score
	 */
	int alphaBetaMax(int alpha, int beta, int depth)
	{
		// Once we reach the last generation, return the current board value all
		// the way up the tree
		if (depth == 0)
			return getBoardValue();

		// If there are no moves, return a very large negative value so that the
		// minimizing player will chose this path
		Queue<Move> moves = generateMoves(board);
		if (moves.isEmpty())
			return -9001;

		int currentValue;
		while (!moves.isEmpty())
		{
			Move move = moves.remove();
			makeMove(move);
			currentValue = alphaBetaMin(alpha, beta, depth - 1);
			undoMove(move);

			if (currentValue >= beta)
				return beta;
			if (currentValue > alpha)
				alpha = currentValue;
		}
		return alpha;
	}

	/**
	 * The
	 * @param alpha the minimum score that the maximizing player is assured of
	 * @param beta the maximum score that the minimizing player is assured of
	 * @param depth the number of generations that this current generation is
	 *            away from the target depth
	 * @return the minimizing player's best score
	 */
	int alphaBetaMin(int alpha, int beta, int depth)
	{
		// Once we reach the last generation, return the current board value all
		// the way up the tree
		if (depth == 0)
			return getBoardValue();

		// If there are no moves, return a very large value so that the
		// maximizing player will chose this path
		Queue<Move> moves = generateMoves(board);
		if (moves.isEmpty())
			return 9001;

		// Try every move out and keep track of the best score, if
		int currentValue;
		while (!moves.isEmpty())
		{
			Move move = moves.remove();
			makeMove(move);
			currentValue = alphaBetaMax(alpha, beta, depth - 1);
			undoMove(move);

			if (currentValue <= alpha)
				return alpha;
			if (currentValue < beta)
				beta = currentValue;
		}
		return beta;
	}

	/**
	 * Executes the given move, changing the current player.
	 * @param move the move to make
	 */
	private void makeMove(Move move)
	{
		move.execute();
		changePlayer();
	}

	/**
	 * Undos the given move, changing the current player.
	 * @param move the move to undo
	 */
	private void undoMove(Move move)
	{
		move.undo();
		changePlayer();
	}

	/**
	 * Changes the current player to the other player.
	 */
	private void changePlayer()
	{
		if (currentPlayer == Player.RED)
			currentPlayer = Player.BLACK;
		else
			// currentPlayer == Player.BLACK
			currentPlayer = Player.RED;
	}
}
