package bot_v2.ai_export;

import bot_v2.*;
import bot_v2.board.Board;
import bot_v2.evaluators.BruteEvaluator;
import bot_v2.evaluators.Evaluator;
import bot_v2.rater.Version1Rating;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Simulator {

    public final static int CORES = 2;
    public final static int MAX_MOVES = 100;
    private int numRunning;
    private final boolean APPEND = true;

    public class SimulatorThread implements Runnable{
        private final Board board;
        private final String fileName;

        private SimulatorThread(Board b, String fn){
            this.board = b;
            this.fileName = fn;
        }

        @Override
        public void run() {
            simulate(this.board, this.fileName);
            --numRunning;
        }
    }

    public void simulate(Board board, String fileName){
        try {
            PrintWriter pw = new PrintWriter("data/position_evaluations/v5/" + fileName + ".txt");
            List<RatedMove> moves;
            float lasRating = 0;
            for(int curInd = 1; curInd < MAX_MOVES; ++curInd){
                System.out.println("Test " + fileName + " index " + curInd);

                // find good moves
                Board evaluation = new Board(board);
                moves = new BotMain(evaluation, evaluation.getCurMove()).getMoves(4, 50);
                if(moves.isEmpty()) break;
                String boardHash = board.boardHash();

                for(RatedMove move : moves){
                    String result = boardHash + " " + (curInd % 2 ^ 1) + " " + move.move.sr + " " + move.move.sc + " " + move.move.er + " " +
                            move.move.ec + " " + (move.rating - lasRating) + " " +
                            move.rating;
                    pw.println(result);
                    System.out.println(result);
                }

                // do move
                RatedMove move;
                if(evaluation.getCurMove() == Side.Black) Collections.reverse(moves);
                float[] weights = new float[moves.size()];
                for(int i = 0; i < weights.length; i++) weights[i] = (float) Math.pow(i+1, 1.5);
                float tot = 0;
                for(float i : weights) tot += i;
                float val = (float) Math.random() * tot;
                int i;
                for(i = 0; i < weights.length && val > weights[i]; val-=weights[i++]){
                    val -= weights[i];
                }
                move = moves.get(i);
                board.makeMove(evaluation.getCurMove(), move.move);

                // calculate rating changes
                lasRating = move.rating;
            }
            pw.close();
        } catch (FileNotFoundException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void generateSimulations(){
        int simulationInd = 0;
        while(simulationInd < 100000000){
            String filename = "test" + (simulationInd);
            if(APPEND){
                if(new File("data/position_evaluations/v5/" + filename + ".txt").exists()){
                    ++simulationInd;
                    continue;
                }
            }
            if(numRunning < CORES){
                ++numRunning;
                Board toUse = new Board(new Version1Rating());
                SimulatorThread curThread = new SimulatorThread(toUse, filename);
                Thread t = new Thread(curThread);
                t.start();
                ++simulationInd;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
        Simulator simulator = new Simulator();
        simulator.generateSimulations();
    }
}
