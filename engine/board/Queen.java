package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Queen extends Piece {

    public Queen(int x, int y, Tools.Side side, Board board) {
        super(x, y, side, board);
    }


    // GETTING SPACES IT CAN MOVE TO

    @Override
    protected HashSet<Integer> possibleMoves() {
        HashSet<Integer> toreturn = new HashSet<>();
        for (int[] direction : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}}) {
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
        if(x == boardx && y == boardy)
            return true;
        King otherKing = board.getKing(Tools.opposite(side));
        // If on the same row/column, see if it's in the same direction & closer
        if(x == boardx || y == boardy) {
            if (Integer.signum(otherKing.boardx - boardx) == Integer.signum(x - boardx) && Integer.signum(otherKing.boardy - boardy) == Integer.signum(y - boardy) && Math.abs(y - boardy) < Math.abs(otherKing.boardy - boardy))
                return true;
        // If on the same diagonal, see if it's in the same direction & closer
        }else if(Math.abs(x - boardx) == Math.abs(y - boardy)) {
            if (Math.signum(otherKing.boardx - boardx) == Math.signum(x - boardx) && Math.signum(otherKing.boardy - boardy) == Math.signum(y - boardy) && Math.abs(x - boardx) < Math.abs(otherKing.boardx - boardx))
                return true;
        }
        return false;
    }

    @Override
    protected HashSet<Integer> capturableSpaces() {
        if(moveNumForSpaces != board.moveNum)
            capturableSpaces = possibleMoves();
        return capturableSpaces;
    }


    // PIECE SPECIFIC METHODS

    @Override
    public engine.Tools.Piece getPiece() {
        return Tools.Piece.queen;
    }

    @Override
    public Queen copy(){
        return new Queen(boardx, boardy, side, board);
    }

    @Override
    public float rating() {
        return (this.side == Tools.Side.White ? 1 : -1) * (100 + getRating());
    }
}
