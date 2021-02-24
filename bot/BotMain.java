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

    public final static int layers = 20;
    public final static int movesPerLayer = 10;
    // int[first move] = {int[] first instruction, int[] second instruction, {rating Bounds} }
    public final static int firstMoveCount = 15;
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
        try{
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
        int[][][] moves = board.board.getMoves(botSide);
        for(int i = 0; i < moves.length; ++i){
            moves[i] = new int[][]{moves[i][0], moves[i][1], new int[]{Evaluator.getRating(moves[i], board.board, botSide)}};
        }
        Arrays.sort(moves, Comparator.comparingInt((int[][] a) -> -a[2][0]));

        int evals = Math.min(toRuns.length, moves.length);
        ExecutorService executor = Executors.newFixedThreadPool(evals);
        for (int i = 0; i < evals; ++i) {
            Board copy = board.board.copy();
            copy.doMove(copy.getPiece(moves[i][0][0], moves[i][0][1]), moves[i][1]);
            copy.nextMove();

            toRuns[i] = new Evaluator(layers - 1, Tools.opposite(botSide), movesPerLayer, copy);
            this.moves[i] = moves[i];
            executor.execute(toRuns[i]);
        }

        executor.shutdown();
        if(!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)){
            throw new TimeoutException("oop lol the ai timed out");
        }

        int best = Integer.MIN_VALUE;
        int[][] bestMove = {null, null};
        for(int i = 0; i < evals; ++i){
            int lowestRating = -toRuns[i].getResult();
            if(lowestRating > best){
                best = lowestRating;
                bestMove = this.moves[i];
            }
        }
        return bestMove;
    }
}
