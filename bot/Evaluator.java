package bot;

import engine.Tools;
import engine.board.Board;
import engine.board.Pawn;

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
        result = ratingBounds(layers, board, currentSide);
    }

    /**
     * Returns the optimal rating using the minimax algorithm
     * @param layers the "depth" of which to calculate
     * @param board The board to do the moves on
     * @param currentSide The current side the method is playing for
     * @return The optimal rating
     */
    private int ratingBounds(int layers, Board board, Tools.Side currentSide) {
        // If we've reached the target depth or there are no moves left, then we return the current rating
        if (layers <= 0)
            return board.rating();
        int[][][] moves = board.getMoves(currentSide);
        if(moves.length == 0){
            return board.rating();
        }

        // Sort the possible moves from lowest immediate rating to highest immediate rating
        Pawn p = board.enPassant;
        for(int i = 0; i < moves.length; ++i){
            moves[i] = new int[][]{moves[i][0], moves[i][1], new int[]{getRating(moves[i], board, currentSide)}};
            if((p != null) == (board.enPassant == null)){
                System.out.println("en Passant changed");
            }
        }
        // Comparator: Positive if order is a -> b; negative if order is b -> a
        Arrays.sort(moves, Comparator.comparingInt((int[][] a) -> a[2][0]));

        int optimalRating;
//        System.out.println(board.getMoves(currentSide).length);
        if(currentSide == Tools.Side.White){
            optimalRating = Integer.MIN_VALUE;

            // Alpha-Beta pruning; we assume both sides will only play the top x moves
            for (int i = moves.length - 1; i > moves.length - 1 - movesPerLayer && i >= 0; --i) {
//                System.out.println(board.getMoves(currentSide).length);
                // Mostly for debugging
                if(!board.canMove(board.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1])){
                    System.out.println("Copy failed to move");
                }

                // We play the move
                board.doMove(board.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
                board.nextMove();

                // The optimal rating - the lowest rating the opponent can get
                int worstCase = ratingBounds(layers - 1, board, Tools.opposite(currentSide));

                // Undo the move, and update the optimal rating
                board.undoLatest(currentSide);
                optimalRating = Math.max(worstCase, optimalRating);
            }
        }else{
            // Symmetric to the above MAX algorithm
            optimalRating = Integer.MAX_VALUE;

            for (int i = 0; i < movesPerLayer && i < moves.length; ++i) {
//                System.out.println(board.getMoves(currentSide).length);
                if(!board.canMove(board.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1])){
                    System.out.println("Copy failed to move");
                }
//                System.out.println(board.getPiece(moves[i][0][0], moves[i][0][1]));
                board.doMove(board.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
                board.nextMove();

                int worstCase = ratingBounds(layers - 1, board, Tools.opposite(currentSide));

                board.undoLatest(currentSide);
                optimalRating = Math.min(worstCase, optimalRating);
            }
        }

        return optimalRating;
    }

    public synchronized int getResult() {
        return result;
    }

    /**
     * Get the rating of the board after making a move
     * @param move The move that currentSide will do
     * @param board the board to do the move on
     * @param currentSide the current side that is doing the move
     * @return the rating of currentSide after currentSide does the move
     */
    public static int getRating(int[][] move, Board board, Tools.Side currentSide){
        if(!board.canMove(board.getPiece(move[0][0], move[0][1]), move[1])){
            System.out.println("oop board failed move");
        }
        // Do the move
        board.doMove(board.getPiece(move[0][0], move[0][1]), move[1]);
        board.nextMove();
        int fr = board.rating();
        board.undoLatest(currentSide);
        return fr;
    }

}
