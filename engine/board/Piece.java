package engine.board;

import engine.Main;
import engine.Tools;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Piece implements Copyable {

    int boardx;
    int boardy;
    public final Tools.Side side;
    Board board;

    public Piece(int x, int y, Tools.Side side, Board board) {
        this.boardx = x;
        this.boardy = y;
        this.side = side;
        this.board = board;
    }


    // HANDLING MOVING THE PIECE

    /**
     * If possible, move the piece to the designated square and return true. If it isn't possible, then return false
     *
     * @param nx wanted cell x coordinate
     * @param ny wanted cell y coordinate
     * @throws AbleToMoveException this occurs when the piece is able to "eat" a piece on its own side
     * @implNote This is generally more useful for the client/human player, as the main difference is that it returns
     * a boolean based on whether or not the move even happened
     */
    public boolean move(int nx, int ny) throws AbleToMoveException {
        if (canMove().contains(Tools.toNum(nx, ny))) {
            moveTo(nx, ny);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Move a piece to a square, assuming that it is valid. There is no regard for checks or the like
     *
     * @param nx Target x coordinate
     * @param ny Target y coordinate
     * @throws AbleToMoveException This piece should never(even in simulations) go on top of another piece of its side
     */
    protected void moveTo(int nx, int ny) throws AbleToMoveException {
        Piece pieceAtPos = board.getPiece(nx, ny);
        if (pieceAtPos != null)
            if (pieceAtPos.side == this.side)
                throw new AbleToMoveException("Somehow, this piece was able to collide into another piece on its side... ");
            else
                board.removePiece(pieceAtPos);
        this.boardx = nx;
        this.boardy = ny;
    }

    /**
     * Force sets position, useful for undoing or bot commands,
     *
     * @param x the x coordinate to set to
     * @param y the y coordinate to set to
     */
    public void setPosition(int x, int y) {
        this.boardx = x;
        this.boardy = y;
    }


    // GETTING MOVES IT CAN GO TO

    /**
     * This returns the VALID moves that a piece can go to
     *
     * @return A HashSet of Integers of the hashed coordinates for where the piece can go to
     */
    public HashSet<Integer> canMove() {
        HashSet<Integer> toreturn = this.possibleMoves();
        ArrayList<Integer> toremove = new ArrayList<>();
        for (Integer number : toreturn) {
            Board pseudoBoard = board.copy();
            Piece self = pseudoBoard.getPiece(boardx, boardy);
            self.moveTo(Tools.getX(number), Tools.getY(number));
            if (pseudoBoard.inCheck(side))
                toremove.add(number);
        }
        toreturn.removeAll(toremove);
        return toreturn;
    }

    /**
     * Gets a list of coordinates that the piece can move to - there is no regard for check and each coordinate is
     * a place where the piece can MOVE TO. This is important as with castling and en passant, there may be differences
     *
     * @return the set of hashed coordinates
     */
    protected abstract HashSet<Integer> possibleMoves();

    /**
     * Similar to possibleMoves, but instead of the squares a piece can go to, it returns the squares that a piece
     * is attacking(the behavior is changed for castling & en passant, it's also better extrapolated and more understandable)
     *
     * @return the set of hashed coordinates
     */
    protected abstract HashSet<Integer> capturableSpaces();


    // PIECE SPECIFIC METHODS

    public abstract Tools.Piece getPiece();

    public abstract Piece copy();

    public abstract float rating();

    // RENDERING

    public void render() {
        Tools.drawImage(Tools.getSprite(side, getPiece()), boardx * Main.CELL_SIZE, boardy * Main.CELL_SIZE);
    }

    public void renderSelected() {
        Tools.drawRect(boardx * Main.CELL_SIZE, boardy * Main.CELL_SIZE, Main.CELL_SIZE, Main.CELL_SIZE, 255, 87, 51, 130);
        render();
    }


    // TOOL METHODS
    protected float getRating(){
        int layer = side == Tools.Side.Black ? boardy : 7 - boardy;
        return Tools.getPositionRating(getPiece(), boardx, layer);
    }
}
