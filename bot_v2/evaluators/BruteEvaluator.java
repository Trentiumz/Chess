package bot_v2.evaluators;

import bot_v2.Move;
import bot_v2.RatedMove;
import bot_v2.Side;
import bot_v2.board.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteEvaluator extends Evaluator{
    private final int layers;
    private final int movesPerLayer;
    public int counter = 0;

    public BruteEvaluator(int layers, Side currentSide, int movesPerLayer, Board board) {
        super(currentSide, board);
        this.layers = layers;
        this.movesPerLayer = movesPerLayer;
    }

    protected float ratingBounds(Board board, Side currentSide) throws IllegalStateException {
        return calcRating(layers, board, currentSide);
    }
    private float calcRating(int layers, Board board, Side currentSide){
        ++counter;
        // If we've reached the target depth or there are no moves left, then we return the current rating
        if (layers <= 0)
            return board.rating();

        if(board.getCurMove() != currentSide) throw new IllegalStateException("The bot is trying to play a side it shouldn't!");

        // get a list of moves alongside their rating
        List<Move> possibleMoves = board.getMoves();
        List<RatedMove> moves = new ArrayList<>(possibleMoves.size());
        for (Move possibleMove : possibleMoves) {
            board.makeMove(currentSide, possibleMove);
            RatedMove ret = new RatedMove(possibleMove, board.rating());
            board.undoLastMove();
            moves.add(ret);
        }
        Collections.sort(moves);

        // Sorted from most desireable to least desireable moves
        if(currentSide == Side.White) Collections.reverse(moves);

        if(moves.isEmpty()){
            return board.rating();
        }

        float optimalRating = currentSide == Side.White ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for(int i = 0; i < movesPerLayer && i < moves.size(); i++){
            board.makeMove(currentSide, moves.get(i).move);
            float worst = calcRating(layers - 1, board, currentSide == Side.White ? Side.Black : Side.White);
            optimalRating = currentSide == Side.White ? Math.max(optimalRating, worst) : Math.min(optimalRating, worst);
            board.undoLastMove();
        }

        return optimalRating;
    }
}
