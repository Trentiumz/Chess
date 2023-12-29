package bot_v2.evaluators;

import bot_v2.Side;
import bot_v2.board.Board;

public class AIEvaluator extends Evaluator{


    public AIEvaluator(Side currentSide, Board board) {
        super(currentSide, board);
    }

    @Override
    protected float ratingBounds(Board board, Side currentSide) {
        return 0;
    }
}
