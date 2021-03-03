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

    /**
     * This is a memoization for the capturable spaces
     */
    public HashSet<Integer> capturableSpaces;
    protected int moveNumForSpaces = Integer.MIN_VALUE;

    public Piece(int x, int y, Tools.Side side, Board board) {
        this.boardx = x;
        this.boardy = y;
        this.side = side;
        this.board = board;
    }


    // HANDLING MOVING THE PIECE

    public boolean canMoveTo(int nx, int ny){
        return canMove().contains(Tools.toNum(nx, ny));
    }

    /**
     * Move a piece to a square, assuming that it is valid. There is no regard for checks or the like
     *
     * @param nx Target x coordinate
     * @param ny Target y coordinate
     * @throws AbleToMoveException This piece should never(even in simulations) go on top of another piece of its side
     * @implNote there is undefined behavior if the move isn't valid; use canMoveTo to see if you can even move there
     */
    protected void move(int nx, int ny) throws AbleToMoveException {
        Piece pieceAtPos = board.getPiece(nx, ny);
        if (pieceAtPos != null)
            if (pieceAtPos.side == this.side)
                throw new AbleToMoveException("Somehow, this piece was able to collide into another piece on its side... ");
            else{
                if(pieceAtPos == board.enPassant)
                    board.addUndoMove(new Move(Tools.Instruction.setEnPassant, pieceAtPos, null));
                board.removePiece(pieceAtPos);
                board.addUndoMove(new Move(Tools.Instruction.add, pieceAtPos, null));
            }

        board.addUndoMove(new Move(Tools.Instruction.move, this, new int[]{boardx, boardy}));
        board.changePosition(this, nx, ny);
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
            board.doMove(this, new int[]{Tools.getX(number), Tools.getY(number)});
            board.nextMove();
            if (board.inCheck(side))
                toremove.add(number);
            board.undoLatest(side);
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

    /**
     * Returns whether or not a piece at (x,y) will stop this piece from checking the opposing king
     * @param x the x coordinate
     * @param y the y coordinate
     * @return whether or not a piece at (x,y) will stop this piece from checking the opposing king
     * @implNote This method will assume that this piece is actually already checking the king and it gives a quick way
     * of finding if moving to said position will stop this piece from getting to the king
     * @implNote If x=boardx & y=boardy, then this piece assumes that the other piece will "eat" this piece
     */
    public abstract boolean doesBlock(int x, int y);

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
