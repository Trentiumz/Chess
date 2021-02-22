package bot;

import engine.Tools;
import engine.board.Board;

import java.util.Arrays;

class Evaluator implements Runnable {

    private int result;
    private final int layers;
    private final Tools.Side currentSide;
    private final Board board;
    private final int movesPerLayer;


    public Evaluator(int layers, Tools.Side currentSide, int movesPerLayer, Board board) {
        this.layers = layers;
        this.currentSide = currentSide;
        this.movesPerLayer = movesPerLayer;
        this.board = board;
    }

    public void run() {
        result = ratingBounds(layers, currentSide, board);
    }

    private int ratingBounds(int layers, Tools.Side currentSide, Board board) {
        if (layers <= 0)
            return board.rating();
        int[][][] moves = board.getMoves(currentSide);
        // Comparator: Positive if order is a -> b; negative if order is b -> a
        Arrays.sort(moves, (int[][] a, int[][] b) -> compareBoards(a, b, board));

        int highestForcedRating = Integer.MIN_VALUE;

        for (int i = moves.length - 1; i > moves.length - 1 - movesPerLayer && i >= 0; --i) {
            Board copy = board.copy();
            copy.doMove(copy.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
            copy.currentMove = copy.opposite();
            // The highest rating that the other player can get, then the negative, is the worst possible rating for this
            int worstCase = -ratingBounds(layers - 1, Tools.opposite(currentSide), copy);
            highestForcedRating = Math.max(worstCase, highestForcedRating);
        }

        return highestForcedRating;
    }

    public synchronized int getResult() {
        return result;
    }


    /**
     * Compares two moves to see which one has a higher rating - search depth of 1
     *
     * @param a     the first move
     * @param b     the second move
     * @param board the board that the move is acted on
     * @return returns something >1 if move a is better; something <1 if move b is better; 0 if they're equal - it uses Integer.compare()
     */
    public static int compareBoards(int[][] a, int[][] b, Board board) {
        Board first = board.copy();
        first.doMove(first.getPiece(a[0][0], a[0][1]), a[1]);

        Board second = board.copy();
        second.doMove(second.getPiece(b[0][0], b[0][1]), b[1]);

        int fr = first.rating();
        int sr = second.rating();
        return Integer.compare(fr, sr);
    }

}
