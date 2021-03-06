package engine.board;

import engine.Tools;

import java.util.ArrayList;
import java.util.HashSet;

public class Rook extends Piece {

    boolean isMoved = false;

    public Rook(int x, int y, Tools.Side side, Board board) {
        super(x, y, side, board);
    }

    public Rook(Rook other){
        super(other);
        this.isMoved = other.isMoved;
    }

    // MOVING

    @Override
    protected void move(int nx, int ny) throws AbleToMoveException {
        Piece pieceAtPos = board.getPiece(nx, ny);

        if (pieceAtPos != null)
            if (pieceAtPos.side == this.side)
                throw new AbleToMoveException("Somehow, this piece was able to collide into another piece on its side...");
            else {
                if (pieceAtPos == board.enPassant)
                    board.addUndoMove(new Move(Tools.Instruction.setEnPassant, pieceAtPos, null));
                board.removePiece(pieceAtPos);
                board.addUndoMove(new Move(Tools.Instruction.add, pieceAtPos, null));
            }

        if (!isMoved)
            board.addUndoMove(new Move(Tools.Instruction.rookUnMoved, this, null));

        board.addUndoMove(new Move(Tools.Instruction.move, this, new int[]{boardx, boardy}));
        board.changePosition(this, nx, ny);
        this.isMoved = true;
    }


    // GETTING POSSIBLE MOVES

    @Override
    protected HashSet<Integer> possibleMoves() {
        HashSet<Integer> toreturn = new HashSet<>();
        for (int[] direction : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
            for (int cx = boardx + direction[0], cy = boardy + direction[1]; board.inBounds(cx, cy); cx += direction[0], cy += direction[1]) {
                Piece thePiece = board.getPiece(cx, cy);
                if (thePiece == null)
                    toreturn.add(Tools.toNum(cx, cy));
                else if (thePiece.side == this.side)
                    break;
                else {
                    toreturn.add(Tools.toNum(cx, cy));
                    break;
                }
            }
        }
        return toreturn;
    }

    @Override
    public boolean doesBlock(int x, int y) {
        King oppositeKing = board.getKing(Tools.opposite(side));
        // If the oppponent "eats" this piece
        if (x == boardx && y == boardy)
            return true;
        // If the opponent is on the column, is in the same direction as the king from this piece and is closer to this piece
        else if (x == boardx && Integer.signum(oppositeKing.boardy - boardy) == Integer.signum(y - boardy) && Math.abs(y - boardy) < Math.abs(oppositeKing.boardy - boardy))
            return true;
        // If the opponent is on the same row, is in the same direction as the king from this piece and is closer to this piece
        else if (y == boardy && Integer.signum(oppositeKing.boardx - boardx) == Integer.signum(x - boardx) && Math.abs(x - boardx) < Math.abs(oppositeKing.boardx - boardx))
            return true;
        // If none of these are true, then moving to x,y does not block this piece
        else
            return false;
    }

    @Override
    protected HashSet<Integer> capturableSpaces() {
        if(moveNumForSpaces != board.moveNum)
            capturableSpaces = possibleMoves();
        return capturableSpaces;
    }

    @Override
    public boolean willCheck(Piece toMove, int newX, int newY) {
        // Checking for a freaking en Passant
        if(inPath.size() == 2 && board.enPassant != null && toMove != board.enPassant && inPath.contains(board.enPassant) && inPath.contains(toMove) && toMove.boardy == board.enPassant.boardy && toMove.boardy == boardy && Math.abs(toMove.boardx - board.enPassant.boardx) == 1 && newX == board.enPassant.boardx && toMove instanceof Pawn)
            return true;

        // will check if nothing's in the path and it's not being blocked; or if there's one thing in the path but it moves away; or if the piece captures this piece
        if(newX == boardx && newY == boardy)
            return false;
        return (inPath.size() == 0 && !doesBlock(newX, newY)) || (inPath.size() == 1 && inPath.get(0) == toMove && !doesBlock(newX, newY));
    }

    // PIECE SPECIFIC METHODS

    @Override
    public boolean otherKingInRange() {
        King otherKing = board.getKing(Tools.opposite(side));
        this.inPath.clear();
        if(otherKing.boardx == boardx || otherKing.boardy == boardy){
            // Get all pieces in between this and the other king
            int xDiff = (int) Math.signum(otherKing.boardx - boardx);
            int yDiff = (int) Math.signum(otherKing.boardy - boardy);
            for(int x = boardx + xDiff, y = boardy + yDiff; board.getPiece(x, y) != otherKing; x += xDiff, y += yDiff){
                if(board.getPiece(x, y) != null)
                    inPath.add(board.getPiece(x, y));
            }

            return true;
        }
        return false;
    }

    @Override
    public engine.Tools.Piece getPiece() {
        return Tools.Piece.rook;
    }

    @Override
    public Rook copy() {
        return new Rook(this);
    }

    @Override
    public float rating() {
        return (this.side == Tools.Side.White ? 1 : -1) * (50 + getRating());
    }

}
