package engine.board;

import engine.Tools;
import engine.Tools.Side;

import java.util.ArrayList;
import java.util.HashSet;

public class King extends Piece {

    boolean didMove;

    public King(int x, int y, Side side, Board board) {
        super(x, y, side, board);
    }


    // MOVING

    @Override
    public boolean doesBlock(int x, int y) throws InvalidPieceException{
        throw new InvalidPieceException("You called a method which assumes that this king at (" + boardx + ", " + boardy + ") is able to check the other king, which is theoretically impossible!");
    }

    public void move(int nx, int ny) throws AbleToMoveException {
        if (Math.abs(nx - boardx) == 2) {
            int rx = nx > boardx ? 7 : 0;
            Piece r = board.getPiece(rx, boardy);
            board.changePosition(board.getPiece(rx, boardy), nx > boardx ? 5 : 3, boardy);
            board.addUndoMove(new Move(Tools.Instruction.move, r, new int[] {rx, boardy}));
        }
        Piece pieceAtPos = board.getPiece(nx, ny);
        if (pieceAtPos != null)
            if (pieceAtPos.side == this.side)
                throw new AbleToMoveException("Somehow, this piece was able to collide into another piece on its side...");
            else{
                if(pieceAtPos == board.enPassant)
                    board.addUndoMove(new Move(Tools.Instruction.setEnPassant, pieceAtPos, null));
                board.removePiece(pieceAtPos);
                board.addUndoMove(new Move(Tools.Instruction.add, pieceAtPos, null));
            }

        if(!didMove)
            board.addUndoMove(new Move(Tools.Instruction.kingUnMoved, this, null));

        board.addUndoMove(new Move(Tools.Instruction.move, this, new int[]{boardx, boardy}));
        board.changePosition(this, nx, ny);
        didMove = true;
    }

    @Override
    public HashSet<Integer> canMove() {
        HashSet<Integer> toreturn = this.possibleMoves();
        ArrayList<Integer> toremove = new ArrayList<>();
        for (Integer number : toreturn) {
            board.doMove(this, new int[]{Tools.getX(number), Tools.getY(number)});
            board.nextMove();
            if (board.otherSideCanGet(side, this.boardx, this.boardy)) {
                toremove.add(number);
            }
            board.undoLatest(side);
        }
        toreturn.removeAll(toremove);
        return toreturn;
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
        if(moveNumForSpaces != board.moveNum){
            capturableSpaces = new HashSet<>();
            for (int[] direction : new int[][]{{1, 1}, {-1, -1}, {1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int cx = boardx + direction[0];
                int cy = boardy + direction[1];
                Piece thePiece = board.getPiece(cx, cy);
                if ((thePiece == null || thePiece.side != this.side) && board.inBounds(cx, cy))
                    capturableSpaces.add(Tools.toNum(cx, cy));
            }
        }
        return capturableSpaces;
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

    @Override
    public float rating() {
        return (this.side == Tools.Side.White ? 1 : -1) * (900 + getRating());
    }
}
