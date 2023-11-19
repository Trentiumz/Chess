package bot_v2.rater;

import bot_v2.board.Board;
import bot_v2.Side;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Version1Rating implements BoardRater{

    /**
     * Ratings, indexed by piece type, r, and then c
     *
     * The ratings are from the perspective of the white player (i.e. lower row values are closer to home row)
     */
    private final float[][][] ratings;
    private static Version1Rating singleton = null;
    private static final String[] order = {"pawn", "bishop", "knight", "rook", "queen", "king"};

    private Version1Rating(){

        ratings = new float[6][8][8];
        for(int i = 0; i < 6; ++i){
            try {
                BufferedReader br = new BufferedReader(new FileReader("data/botAssist/positionRatings/" + order[i] + ".pr"));
                for(int l = 7; l >= 0; --l){
                    String[] parts = br.readLine().split(" ");
                    for(int k = 0; k < 8; ++k) ratings[i][l][k] = Float.parseFloat(parts[k]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Version1Rating getRater(){
        if(singleton == null) singleton = new Version1Rating();
        return singleton;
    }

    @Override
    public float rating(Board board, Side curSide) {
        float ret = 0;
        for (int r = 0; r < 8; ++r)
            for (int c = 0; c < 8; ++c) {
                if(!board.board[r][c].isEmpty()){
                    int multiplier = board.board[r][c].side == Side.White ? 1 : -1;
                    int cr = board.board[r][c].side == Side.Black ? 7 - r : r;

                    float val = switch(board.board[r][c].type){
                        case Pawn -> multiplier * (10 + ratings[0][cr][c]);
                        case Bishop -> multiplier * (30 + ratings[1][cr][c]);
                        case Knight -> multiplier * (30 + ratings[2][cr][c]);
                        case Rook -> multiplier * (50 + ratings[3][cr][c]);
                        case Queen -> multiplier * (100 + ratings[4][cr][c]);
                        case King -> multiplier * (900 + ratings[5][cr][c]);
                        case Empty -> 0;
                    };
                    ret += val;
                }
            }
        return ret;
    }
}
