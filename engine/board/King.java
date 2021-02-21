package engine.board;

import engine.Tools;
import engine.Tools.Side;

import java.util.ArrayList;
import java.util.HashSet;

public class King extends Piece {

    private boolean didMove;

    public King(int x, int y, Side side, Board board) {
        super(x, y, side, board);
    }


    // MOVING

    protected void moveTo(int nx, int ny) throws AbleToMoveException {
        if (Math.abs(nx - boardx) == 2) {
            int rx = nx > boardx ? 7 : 0;
            board.getPiece(rx, boardy).boardx = nx > boardx ? 5 : 3;
        }
        Piece pieceAtPos = board.getPiece(nx, ny);
        if (pieceAtPos != null)
            if (pieceAtPos.side == this.side)
                throw new AbleToMoveException("Somehow, this piece was able to collide into another piece on its side...");
            else
                board.removePiece(pieceAtPos);
        this.boardx = nx;
        this.boardy = ny;
        didMove = true;
    }


    // GETTING POSSIBLE MOVES

    @Override
    protected HashSet<Integer> possibleMoves() {
        HashSet<Integer> toreturn = capturableSpaces();
        for (int i : new int[]{1, -1})
            // the two squares are empty, the 3 squares aren't in check
            // Rook exists on the side, rook didn't move, king didn't move
            if (!board.otherSideCanGet(side, boardx, boardy) && !board.otherSideCanGet(side, boardx + i, boardy)
                    && !board.otherSideCanGet(side, boardx + 2 * i, boardy)
                    && board.inBounds(boardx + 2 * i, boardy) && board.isEmpty(boardx + i, boardy) &&
                    board.isEmpty(boardx + 2 * i, boardy) &&
                    board.getPiece(i == 1 ? 7 : 0, boardy) instanceof Rook && !board.isRookMoved(i == 1 ? 7 : 0, boardy) && !didMove)
                // And out comes this monstrosity of a conditional :>
                toreturn.add(Tools.toNum(boardx + 2 * i, boardy));
        return toreturn;
    }

    @Override
    protected HashSet<Integer> capturableSpaces() {
        HashSet<Integer> toreturn = new HashSet<>();
        for (int[] direction : new int[][]{{1, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            int cx = boardx + direction[0];
            int cy = boardy + direction[1];
            Piece thePiece = board.getPiece(cx, cy);
            if ((thePiece == null || thePiece.side != this.side) && board.inBounds(cx, cy))
                toreturn.add(Tools.toNum(cx, cy));
        }
        return toreturn;
    }


    // PIECE SPECIFIC METHODS

    @Override
    public engine.Tools.Piece getPiece() {
        return Tools.Piece.king;
    }

    @Override
    public King copy() {
        King toreturn = new King(boardx, boardy, side, board);
        toreturn.didMove = this.didMove;
        return toreturn;
    }
}
