package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Knight extends Piece {

    public Knight(int x, int y, Tools.Side side, Board board) {
        super(x, y, side, board);
    }

    public Knight(Knight other){
        super(other);
    }

    // GETTING POSSIBLE MOVES

    @Override
    public boolean willCheck(Piece toMove, int newX, int newY) {
        return !(boardx == newX && boardy == newY);
    }

    @Override
    public boolean otherKingInRange() {
        King otherKing = board.getKing(Tools.opposite(side));
        inPath.clear();
        return (Math.abs(otherKing.boardx - boardx) == 2 && Math.abs(otherKing.boardy - boardy) == 1) || (Math.abs(otherKing.boardy - boardy) == 2 && Math.abs(otherKing.boardx - boardx) == 1);
    }

    @Override
    public boolean doesBlock(int x, int y) {
        return x == boardx && y == boardy;
    }

    @Override
    protected HashSet<Integer> possibleMoves() {
        HashSet<Integer> toreturn = new HashSet<>();
        for (int[] direction : new int[][]{{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {-2, 1}, {-2, -1}}) {
            int cx = boardx + direction[0];
            int cy = boardy + direction[1];
            Piece thePiece = board.getPiece(cx, cy);
            if ((thePiece == null || thePiece.side != this.side) && board.inBounds(cx, cy))
                toreturn.add(Tools.toNum(cx, cy));
        }
        return toreturn;
    }

    @Override
    protected HashSet<Integer> capturableSpaces() {
        if (moveNumForSpaces != board.moveNum)
            capturableSpaces = possibleMoves();
        return capturableSpaces;
    }


    // PIECE SPECIFIC METHODS

    @Override
    public engine.Tools.Piece getPiece() {
        return Tools.Piece.knight;
    }

    @Override
    public Piece copy() {
        return new Knight(this);
    }

    @Override
    public float rating() {
        return (this.side == Tools.Side.White ? 1 : -1) * (30 + getRating());
    }

}
