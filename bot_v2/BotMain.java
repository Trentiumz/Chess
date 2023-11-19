package bot_v2;

import bot.InvalidSideException;
import bot.UnableToMoveException;
import bot_v2.board.Board;
import engine.Tools;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class BotMain {

    public final Board board;
    public Side botSide;

    public final static int layers = 4;
    public final static int movesPerLayer = 50;
    // int[first move] = {int[] first instruction, int[] second instruction, {rating Bounds} }
    public final static int firstMoveCount = 50;
    public volatile Evaluator[] toRuns = new Evaluator[firstMoveCount];
    public static final boolean toDebug = false;

    public BotMain(Board board, Side botSide) {
        this.board = board;
        this.botSide = botSide;
    }

    // GETTING THE MOVES
    /**
     * Gets the highest rated move from the board
     *
     * @return the highest rated move from the board
     */
    public Move getMove() throws InterruptedException, TimeoutException, IllegalStateException {
        if(botSide != board.getCurMove()) throw new IllegalStateException("Bot is moving on the wrong move");

        // get a list of moves alongside their rating
        List<RatedMove> moves = board.getMoves().stream().map((move) -> {
            board.makeMove(botSide, move);
            RatedMove ret = new RatedMove(move, board.rating());
            board.undoLastMove();
            return ret;
        }).sorted().collect(Collectors.toList());

        // Sorted from most desireable to least desireable moves
        if(botSide == Side.White) Collections.reverse(moves);

        // Get the number of evaluations
        int evals = Math.min(toRuns.length, moves.size());
        ExecutorService executor = Executors.newFixedThreadPool(evals);

        for(int i = 0; i < evals && i < moves.size(); i++) {
            Board copy = new Board(board);
            copy.makeMove(botSide, moves.get(i).move);
            toRuns[i] = new Evaluator(layers - 1, botSide == Side.White ? Side.Black : Side.White, movesPerLayer, copy);

            if(toDebug) {
                toRuns[i].run();
            } else {
                executor.execute(toRuns[i]);
            }
        }

        executor.shutdown();
        if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
            throw new TimeoutException("oop lol the ai timed out");
        }

        Move bestMove = null;
        float best = botSide == Side.White ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < evals; ++i) {
            float forcedRating = toRuns[i].getResult();
            if ((forcedRating > best) == (botSide == Side.White)) {
                best = forcedRating;
                bestMove = moves.get(i).move;
            }
        }

        return bestMove;
    }
}
