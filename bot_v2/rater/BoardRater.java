package bot_v2.rater;

import bot_v2.Move;
import bot_v2.Piece;
import bot_v2.board.Board;
import bot_v2.Side;

/**
 * Board rating system
 *
 * @implNote one should almost always call setBoard prior to any operations
 */
public abstract class BoardRater {
    /**
     * Sets the board
     * @param board the board
     */
    public abstract void setBoard(Piece[][] board);

    /**
     * Adds a piece to the board
     * @param piece piece
     * @param r row
     * @param c column
     */
    public void addPiece(Piece piece, int r, int c){

    }

    /**
     * removes a piece
     * @param r row of piece
     * @param c column of piece
     */
    public void remPiece(int r, int c){

    }
    public void makeMove(Move move){

    }
    public void undoLastMove(){

    }
    public abstract float rating();
    public abstract BoardRater copy();
}
