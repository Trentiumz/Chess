package bot_v2.evaluators;

import bot_v2.Move;
import bot_v2.RatedMove;
import bot_v2.Side;
import bot_v2.board.Board;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Evaluator implements Runnable {

    private float result;
    protected final Side currentSide;
    protected final Board board;
    public int counter = 0;

    public Evaluator(Side currentSide, Board board) {
        this.currentSide = currentSide;
        this.board = board;
    }

    public void run() {
        result = ratingBounds(board, currentSide);
    }

    /**
     * Returns the optimal rating using the minimax algorithm
     * @param board The board to do the moves on
     * @param currentSide The current side the method is playing for
     * @return The optimal rating
     */
    protected abstract float ratingBounds(Board board, Side currentSide);

    public synchronized float getResult() {
        return result;
    }

}
