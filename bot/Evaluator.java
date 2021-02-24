package bot;

import engine.Tools;
import engine.board.Board;
import engine.board.Move;
import engine.board.Pawn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
        Pawn p = board.enPassant;
        for(int i = 0; i < moves.length; ++i){
            moves[i] = new int[][]{moves[i][0], moves[i][1], new int[]{getRating(moves[i], board, currentSide)}};
            if((p != null) == (board.enPassant == null)){
                System.out.println("en Passant changed");
            }
        }
        // Comparator: Positive if order is a -> b; negative if order is b -> a
        Arrays.sort(moves, Comparator.comparingInt((int[][] a) -> a[2][0]));

        int highestForcedRating = Integer.MIN_VALUE;

        for (int i = moves.length - 1; i > moves.length - 1 - movesPerLayer && i >= 0; --i) {
            if(!board.canDoMove(board.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1])){
                System.out.println("Copy failed to move");
            }
            board.doMove(board.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
            board.nextMove();
            // The highest rating that the other player can get, then the negative, is the worst possible rating for this
            int worstCase = -ratingBounds(layers - 1, Tools.opposite(currentSide), board);
            board.undoLatest(currentSide);
            highestForcedRating = Math.max(worstCase, highestForcedRating);
        }

        return highestForcedRating;
    }

    public synchronized int getResult() {
        return result;
    }

    // DEBUGGING
    static ArrayList<Move> lastM;

    public static int getRating(int[][] move, Board board, Tools.Side currentSide){
        if(!board.canDoMove(board.getPiece(move[0][0], move[0][1]), move[1])){
            System.out.println("oop board failed move");
        }
        board.doMove(board.getPiece(move[0][0], move[0][1]), move[1]);
        int fr = board.rating();
        board.nextMove();
        board.undoLatest(currentSide);
        return fr;
    }

}
