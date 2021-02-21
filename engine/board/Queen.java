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
    protected HashSet<Integer> capturableSpaces() {
        return possibleMoves();
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
}
