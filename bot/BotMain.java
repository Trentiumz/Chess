package bot;

import engine.Tools;
import engine.board.Board;
import engine.board.BoardClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BotMain {

    public final BoardClient board;
    public Tools.Side botSide;

    public final static int layers = 5;
    public final static int movesPerLayer = 8;
    // int[first move] = {int[] first instruction, int[] second instruction, {rating Bounds} }
    public final static int firstMoveCount = 50;
    public volatile Evaluator[] toRuns = new Evaluator[firstMoveCount];
    public volatile int[][][] moves = new int[firstMoveCount][][];

    public BotMain(BoardClient board, Tools.Side botSide) {
        this.board = board;
        this.botSide = botSide;
    }


    // GETTING THE MOVES

    public Tools.Result mainMove() throws InvalidSideException {
        if (board.board.currentMove != botSide)
            throw new InvalidSideException("The bot is trying to play " + board.board.currentMove + " but was defined to play " + botSide);
        if (!board.isMainState())
            throw new InvalidSideException("Apparently, the user is supposed to promote right now, but it's the bot's move! ");

        int[][] move = new int[][]{null, null};
        try {
            move = getMove();
        } catch (InterruptedException | TimeoutException e) {
            System.out.println("Oops the AI failed concurrency lol");
            e.printStackTrace();
        }

        if (move[0] == null)
            throw new UnableToMoveException("The bot has no valid moves; the game should've already ended beforehand!");

        return board.botClick(move[0], move[1]);
    }

    /**
     * Gets the highest rated move from the board
     *
     * @return the highest rated move from the board
     */
    private int[][] getMove() throws InterruptedException, TimeoutException {
        // Debugging for the time elapsed
        Board.time = 0;
        long start = System.currentTimeMillis();

        // moves[i] = {start position, end position, {rating for botSide after the move}}
        int[][][] moves = board.board.getMoves(botSide);
        for (int i = 0; i < moves.length; ++i) {
            moves[i] = new int[][]{moves[i][0], moves[i][1], new int[]{Evaluator.getRating(moves[i], board.board, botSide)}};
        }

        // moves is now sorted from the moves giving lowest rating to the moves giving highest rating
        Arrays.sort(moves, Comparator.comparingInt((int[][] a) -> a[2][0]));

        // Get the number of evaluations
        int evals = Math.min(toRuns.length, moves.length);
        ExecutorService executor = Executors.newFixedThreadPool(evals);

        int[][] bestMove = {null, null};

        // If botSide == Tools.Side.White, then we want the highest rating
        if (botSide == Tools.Side.White) {
            for (int i = evals - 1; i >= 0; --i) {
                // Copy the board and do the move
                Board copy = board.board.copy();
                copy.doMove(copy.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
                copy.nextMove();

                // Add this to the evaluator
                toRuns[i] = new Evaluator(layers - 1, Tools.opposite(botSide), movesPerLayer, copy);
                this.moves[i] = moves[i];

                // In general, we'll use multithreading, but in the debug console, we can turn this off and analyze slowly
                boolean toDebug = false;
                if (toDebug)
                    toRuns[i].run();
                else
                    executor.execute(toRuns[i]);
            }

            executor.shutdown();
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                throw new TimeoutException("oop lol the ai timed out");
            }


            int best = Integer.MIN_VALUE;
            for (int i = 0; i < evals; ++i) {
                int forcedRating = toRuns[i].getResult();
                if (forcedRating > best) {
                    best = forcedRating;
                    bestMove = this.moves[i];
                }
            }
        }else{
            // Otherwise, we're trying to get the lowest possible rating - this mirrors the "max" algorithm
            for (int i = 0; i < evals && i < moves.length; ++i) {
                Board copy = board.board.copy();
                copy.doMove(copy.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
                copy.nextMove();

                toRuns[i] = new Evaluator(layers - 1, Tools.opposite(botSide), movesPerLayer, copy);
                this.moves[i] = moves[i];

                boolean toDebug = false;
                if (toDebug)
                    toRuns[i].run();
                else
                    executor.execute(toRuns[i]);
            }

            executor.shutdown();
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                throw new TimeoutException("oop lol the ai timed out");
            }


            int best = Integer.MAX_VALUE;
            for (int i = 0; i < evals; ++i) {
                int forcedRating = toRuns[i].getResult();
                if (forcedRating < best) {
                    best = forcedRating;
                    bestMove = this.moves[i];
                }
            }
        }


        // For time-related debugging
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(Board.time);

        return bestMove;
    }
}
