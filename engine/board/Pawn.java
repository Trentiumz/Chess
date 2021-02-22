package engine.board;

import engine.Tools;
import engine.Tools.Side;

import java.util.ArrayList;
import java.util.HashSet;

public class Pawn extends Piece {

    public Pawn(int x, int y, Tools.Side side, Board board) {
        super(x, y, side, board);
    }


    // MOVING

    @Override
    public boolean move(int nx, int ny) throws AbleToMoveException {
        if (canMove().contains(Tools.toNum(nx, ny))) {
            moveTo(nx, ny);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void moveTo(int nx, int ny) throws AbleToMoveException {
        Piece pieceAtPos = board.getPiece(nx, ny);
        ArrayList<Move> toAdd = new ArrayList<>();
        toAdd.add(new Move(Tools.Instruction.move, this, new int[]{boardx, boardy}));
        if (pieceAtPos != null){
            board.removePiece(pieceAtPos);
            toAdd.add(new Move(Tools.Instruction.add, pieceAtPos, null));
        }
        else if (board.enPassant != null && board.enPassant.boardx == nx && this.boardx != board.enPassant.boardx){
            board.removePiece(board.enPassant);
            toAdd.add(new Move(Tools.Instruction.add, board.enPassant, null));
        }
        else if (Math.abs(ny - boardy) == 2){
            board.pawnEnPassant(this);
            toAdd.add(new Move(Tools.Instruction.resetEnPassant, null, null));
        }
        if (ny == 0 || ny == 7){
            board.atEnd = this;
            toAdd.add(new Move(Tools.Instruction.resetAtEnd, null, null));
        }

        board.addUndo(toAdd);

        this.boardx = nx;
        this.boardy = ny;
    }


    // GETTING SPACES IT CAN ACTUALLY MOVE TO

    @Override
    protected HashSet<Integer> possibleMoves() {
        HashSet<Integer> toreturn = new HashSet<>();
        int move = side == Side.White ? -1 : 1;
        boolean atStart = (side == Side.White && boardy == 6) || (side == Side.Black && boardy == 1);
        for (int i = 1; i < (atStart ? 3 : 2) && board.inBounds(boardx, boardy + move * i); ++i) {
            Piece piece = board.getPiece(boardx, boardy + move * i);
            if (piece == null)
                toreturn.add(Tools.toNum(boardx, boardy + move * i));
            else
                break;
        }

        Piece piece1 = board.getPiece(boardx + 1, boardy + move);
        Piece piece2 = board.getPiece(boardx - 1, boardy + move);
        if ((piece1 != null && piece1.side != side) || (board.enPassant != null && board.enPassant.side != side && board.enPassant.boardx == boardx + 1 && board.enPassant.boardy == boardy))
            toreturn.add(Tools.toNum(boardx + 1, boardy + move));
        if ((piece2 != null && piece2.side != side) || (board.enPassant != null && board.enPassant.side != side && board.enPassant.boardx == boardx - 1 && board.enPassant.boardy == boardy))
            toreturn.add(Tools.toNum(boardx - 1, boardy + move));
        return toreturn;
    }

    @Override
    protected HashSet<Integer> capturableSpaces() {
        HashSet<Integer> toreturn = new HashSet<>();
        int move = side == Side.White ? -1 : 1;

        Piece piece1 = board.getPiece(boardx + 1, boardy + move);
        Piece piece2 = board.getPiece(boardx - 1, boardy + move);
        if (piece1 != null && piece1.side != this.side)
            toreturn.add(Tools.toNum(boardx + 1, boardy + move));
        if (piece2 != null && piece2.side != this.side)
            toreturn.add(Tools.toNum(boardx - 1, boardy + move));

        if (board.enPassant != null && Math.abs(board.enPassant.boardx - this.boardx) == 1 && board.enPassant.boardy == this.boardy)
            if (board.isEmpty(board.enPassant.boardx, board.enPassant.boardy + move))
                toreturn.add(Tools.toNum(board.enPassant.boardx, board.enPassant.boardy));
        return toreturn;
    }

    // PIECE SPECIFIC METHODS


    @Override
    public engine.Tools.Piece getPiece() {
        return Tools.Piece.pawn;
    }

    @Override
    public Pawn copy() {
        return new Pawn(boardx, boardy, side, board);
    }

    @Override
    public float rating() {
        return 10 + getRating();
    }

    // PROMOTING

    public Piece promote(Tools.Piece piece) throws InvalidPieceException {
        return switch (piece) {
            case queen -> new Queen(boardx, boardy, side, board);
            case rook -> new Rook(boardx, boardy, side, board);
            case bishop -> new Bishop(boardx, boardy, side, board);
            case knight -> new Knight(boardx, boardy, side, board);
            default -> throw new InvalidPieceException("The pawn is promoting to " + piece + ", which isn't allowed");
        };
    }

}
