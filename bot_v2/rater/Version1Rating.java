package bot_v2.rater;

import bot_v2.Piece;
import bot_v2.PieceType;
import bot_v2.board.Board;
import bot_v2.Side;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Version1Rating extends BoardRater{

    /**
     * Ratings, indexed by piece type, r, and then c
     *
     * The ratings are from the perspective of the white player (i.e. lower row values are closer to home row)
     */
    private static float[][][] ratings = null;
    private static final String[] order = {"pawn", "bishop", "knight", "rook", "queen", "king"};
    public Piece[][] board;
    private float curRating;

    public Version1Rating(){
        if(ratings == null){
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

        board = null;
        curRating = 0;
    }

    private Version1Rating(Version1Rating copy){
        this.board = new Piece[copy.board.length][];
        for(int i = 0; i < copy.board.length; i++) this.board[i] = Arrays.copyOf(copy.board[i], copy.board[i].length);
        this.curRating = copy.curRating;
    }

    private float getWeight(PieceType type, int cr, int c){
        return switch(type){
            case Pawn -> (10 + ratings[0][cr][c]);
            case Bishop -> (30 + ratings[1][cr][c]);
            case Knight -> (30 + ratings[2][cr][c]);
            case Rook -> (50 + ratings[3][cr][c]);
            case Queen -> (100 + ratings[4][cr][c]);
            case King -> (900 + ratings[5][cr][c]);
            case Empty -> 0;
        };
    }

    @Override
    public void setBoard(Piece[][] board) {
        this.board = board;
        for (int r = 0; r < 8; ++r)
            for (int c = 0; c < 8; ++c) {
                if(!board[r][c].isEmpty()){
                    int multiplier = board[r][c].side == Side.White ? 1 : -1;
                    int cr = board[r][c].side == Side.Black ? 7 - r : r;
                    this.curRating += multiplier * getWeight(board[r][c].type, cr, c);
                }
            }
    }

    @Override
    public void addPiece(Piece piece, int r, int c) {
        int multiplier = piece.side == Side.White ? 1 : -1;
        int cr = piece.side == Side.Black ? 7 - r : r;
        this.curRating += multiplier * getWeight(piece.type, cr, c);
        this.board[r][c] = piece;
    }

    @Override
    public void remPiece(int r, int c){
        int multiplier = board[r][c].side == Side.White ? 1 : -1;
        int cr = board[r][c].side == Side.Black ? 7 - r : r;
        this.curRating -= multiplier * getWeight(board[r][c].type, cr, c);
        this.board[r][c] = Piece.EMPTY;
    }

    @Override
    public float rating() {
        return curRating;
    }

    @Override
    public BoardRater copy() {
        return new Version1Rating(this);
    }
}
