package bot_v2;

import bot_v2.board.Board;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Evaluator implements Runnable {

    private float result;
    private final int layers;
    private final Side currentSide;
    private final Board board;
    private final int movesPerLayer;

    public Evaluator(int layers, Side currentSide, int movesPerLayer, Board board) {
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
    private float ratingBounds(int layers, Board board, Side currentSide) throws IllegalStateException {
        // If we've reached the target depth or there are no moves left, then we return the current rating
        if (layers <= 0)
            return board.rating();

        if(board.getCurMove() != currentSide) throw new IllegalStateException("The bot is trying to play a side it shouldn't!");

        // get a list of moves alongside their rating
        List<RatedMove> moves = board.getMoves().stream().map((move) -> {
            board.makeMove(currentSide, move);
            RatedMove ret = new RatedMove(move, board.rating());
            board.undoLastMove();
            return ret;
        }).sorted().collect(Collectors.toList());

        // Sorted from most desireable to least desireable moves
        if(currentSide == Side.White) Collections.reverse(moves);

        if(moves.isEmpty()){
            return board.rating();
        }

        float optimalRating = currentSide == Side.White ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for(int i = 0; i < movesPerLayer && i < moves.size(); i++){
            board.makeMove(currentSide, moves.get(i).move);
            float worst = ratingBounds(layers - 1, board, currentSide == Side.White ? Side.Black : Side.White);
            optimalRating = currentSide == Side.White ? Math.max(optimalRating, worst) : Math.min(optimalRating, worst);
            board.undoLastMove();
        }

        return optimalRating;
    }

    public synchronized float getResult() {
        return result;
    }

}