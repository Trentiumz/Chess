package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Rook extends Piece {

    boolean isMoved = false;

    public Rook(int x, int y, Tools.Side side, Board board) {
        super(x, y, side, board);
    }

    // MOVING

    @Override
    protected void moveTo(int nx, int ny) throws AbleToMoveException{
        Piece pieceAtPos = board.getPiece(nx, ny);
        if (pieceAtPos != null)
            if (pieceAtPos.side == this.side)
                throw new AbleToMoveException("Somehow, this piece was able to collide into another piece on its side...");
            else
                board.removePiece(pieceAtPos);
        this.boardx = nx;
        this.boardy = ny;
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
    protected HashSet<Integer> capturableSpaces() {
        return possibleMoves();
    }


    // PIECE SPECIFIC METHODS

    @Override
    public engine.Tools.Piece getPiece() {
        return Tools.Piece.rook;
    }

    @Override
    public Rook copy(){
        Rook toreturn = new Rook(boardx, boardy, side, board);
        toreturn.isMoved = this.isMoved;
        return toreturn;
    }

    @Override
    public float rating() {
        return 50 + getRating();
    }

}
