package engine.board;

import java.util.*;

import bot.InvalidSideException;
import engine.Tools;
import org.jetbrains.annotations.NotNull;

public class Board implements Copyable {

    public Piece[][] piecePositions = new Piece[8][8];

    King whiteKing;
    King blackKing;
    // the current side which is moving; the opposite of the last side that moved
    public Tools.Side currentMove;

    public Pawn atEnd;
    public Pawn enPassant;

    // The current move number
    int moveNum = 0;

    // This is the stack for undoing
    /**
     * {command}
     * if there's a piece, then we have {command, piece}
     * These are all numbers corresponding to the enums of Tools.Instruction & Tools.Piece
     * The moves are also in chronological order; we undo by doing the last instruction first
     */
    public final ArrayList<ArrayList<Move>> undoMoves;

    // This is for debugging
    public static int time = 0;


    public Board() {
        undoMoves = new ArrayList<>() {{
            add(new ArrayList<>());
        }};
    }


    // MOVE HANDLERS

    /**
     * Tries to move the piece
     *
     * @param piece the piece to be moved
     * @param nx    the next x coordinate
     * @param ny    the next y coordinate
     */
    public void movePiece(@NotNull Piece piece, int nx, int ny) {
        undoMoves.add(new ArrayList<>());
        if (this.enPassant != null && this.enPassant.side == currentMove) {
            assert undoMoves.get(undoMoves.size() - 1) != null;
            undoMoves.get(undoMoves.size() - 1).add(new Move(Tools.Instruction.setEnPassant, enPassant, null));
            enPassant = null;
        }
        piece.move(nx, ny);
    }

    public boolean canMove(Piece piece, int nx, int ny) {
        return piece.canMoveTo(nx, ny);
    }

    public boolean canDoMove(@NotNull Piece piece, int[] instruction) {
        return canMove(piece, instruction[0], instruction[1]);
    }

    public void doMove(@NotNull Piece piece, int[] instruction) {
        movePiece(piece, instruction[0], instruction[1]);
        if (piece == atEnd)
            atEnd.promote(Tools.promotionOrder[instruction[2]]);
    }

    // BOARD EDITTING

    public void pawnEnPassant(Pawn pawn) {
        enPassant = pawn;
    }

    public Piece promote(Tools.Piece piece) {
        Piece toreturn = atEnd.promote(piece);
        removePiece(atEnd);
        this.addPiece(toreturn);
        atEnd = null;
        return toreturn;
    }

    public void removePiece(int nx, int ny) {
        Piece thePiece = piecePositions[nx][ny];
        piecePositions[nx][ny] = null;
        if (enPassant == thePiece)
            enPassant = null;
        if (atEnd == thePiece)
            atEnd = null;
    }

    public void removePiece(Piece piece) {
        removePiece(piece.boardx, piece.boardy);
    }

    public void addPiece(Piece piece) throws InvalidPieceException {
        if (piecePositions[piece.boardx][piece.boardy] != null)
            throw new InvalidPieceException("The piece at (" + piece.boardx + " " + piece.boardy + ") is occupied already - remove it first!");
        piecePositions[piece.boardx][piece.boardy] = piece;
    }

    public void changePosition(Piece piece, int nx, int ny){
        piecePositions[piece.boardx][piece.boardy] = null;
        piecePositions[nx][ny] = piece;
        piece.setPosition(nx, ny);
    }

    public void addKing(Tools.Side side, King king) {
        setKing(side, king);
        this.addPiece(king);
    }

    public void setKing(Tools.Side side, King king) {
        switch (side) {
            case White -> this.whiteKing = king;
            case Black -> this.blackKing = king;
        }
    }

    public void undoLatest(Tools.Side side) throws InvalidSideException {
        if (currentMove == side)
            throw new InvalidSideException("The one who called to undo is currently supposed to move " + side + " " + currentMove);
        ArrayList<Move> last = undoMoves.remove(undoMoves.size() - 1);
        for (int i = last.size() - 1; i >= 0; --i) {
            Move m = last.get(i);
            switch (m.instruction) {
                case move -> changePosition(m.piece, m.coords[0], m.coords[1]);
                case add -> addPiece(m.piece);
                case remove -> removePiece(m.piece);
                case kingUnMoved -> ((King) m.piece).didMove = false;
                case rookUnMoved -> ((Rook) m.piece).isMoved = false;
                case resetEnPassant -> enPassant = null;
                case resetAtEnd -> atEnd = null;
                case setEnPassant -> enPassant = (Pawn) m.piece;
                case setAtEnd -> atEnd = (Pawn) m.piece;
            }
        }
        currentMove = opposite();
        --moveNum;
    }

    public void nextMove() {
        currentMove = opposite();
        ++moveNum;
    }

    public void initialize() {
        for (Tools.Side side : new Tools.Side[]{Tools.Side.Black, Tools.Side.White}) {
            int pawnLayer = side == Tools.Side.Black ? 1 : 6;
            int backLayer = side == Tools.Side.Black ? 0 : 7;
            for (int i = 0; i < 8; ++i)
                addPiece(new Pawn(i, pawnLayer, side, this));
            addPiece(new Rook(0, backLayer, side, this));
            addPiece(new Rook(7, backLayer, side, this));
            addPiece(new Knight(1, backLayer, side, this));
            addPiece(new Knight(6, backLayer, side, this));
            addPiece(new Bishop(2, backLayer, side, this));
            addPiece(new Bishop(5, backLayer, side, this));
            addPiece(new Queen(3, backLayer, side, this));
        }
        addKing(Tools.Side.White, new King(4, 7, Tools.Side.White, this));
        addKing(Tools.Side.Black, new King(4, 0, Tools.Side.Black, this));
        currentMove = Tools.Side.White;
    }


    // INFORMATION METHODS

    /**
     * Returns the "rating" of the board
     *
     * @param currentSide the side of the player
     * @return the "rating" of the board in favor of the player; higher number means the player is doing better
     */
    public int rating(Tools.Side currentSide) {
        int rating = 0;
        for (int x = 0; x < piecePositions.length; ++x)
            for (int y = 0; y < piecePositions[x].length; ++y) {
                if(getPiece(x, y) == null)
                    continue;
                if (getPiece(x, y).side == currentSide)
                    rating += getPiece(x, y).rating();
                else
                    rating -= getPiece(x, y).rating();
            }
        Tools.Result result = getBoardResult();
        if (result == switch (currentSide) {
            case White -> Tools.Result.WhiteWon;
            case Black -> Tools.Result.BlackWon;
        }) {
            return 9000;
        } else if (result == Tools.Result.Draw)
            return 0;
        else if (result != null)
            // Here, if it isn't a draw, and he didn't win, and there was a result, then the other side won
            return -9000;
        return rating;
    }

    public int rating() {
        return rating(currentMove);
    }

    /**
     * This is mainly for the bot, it returns the possible moves
     *
     * @param side is the side of the player/bot
     * @return is done in the format {{sx, sy, fx, fy}, {sx, sy, fx, fy, pi}, ...}, the first is for normal moving, the second for when you move into a promotion
     */
    public int[][][] getMoves(Tools.Side side) {
        ArrayList<int[][]> possibleMoves = new ArrayList<>();
        for(Piece[] column : piecePositions)
            for(Piece p : column){
                if(p != null && p.side == side){
                    for (Integer finalCoord : p.canMove()) {
                        int x = Tools.getX(finalCoord);
                        int y = Tools.getY(finalCoord);
                        if (p instanceof Pawn && (y == 0 || y == 7))
                            // p is a pawn, and it's gonna reach the end; so we gotta promote it
                            for (int i = 0; i < 4; ++i)
                                possibleMoves.add(new int[][]{{p.boardx, p.boardy}, {x, y, i}});
                        else
                            possibleMoves.add(new int[][]{{p.boardx, p.boardy}, {x, y}});
                    }
                }
            }
        int[][][] toreturn = new int[possibleMoves.size()][2][];
        for (int i = 0; i < possibleMoves.size(); ++i)
            toreturn[i] = possibleMoves.get(i);
        return toreturn;
    }

    public boolean isRookMoved(int x, int y) {
        for(Piece[] column : piecePositions)
            for(Piece piece : column){
                if(piece == null) continue;
                if (piece.boardx == x && piece.boardy == y && piece instanceof Rook)
                    return ((Rook) piece).isMoved;
            }
        return false;
    }

    public King getKing(Tools.Side side) {
        return switch (side) {
            case White -> this.whiteKing;
            case Black -> this.blackKing;
        };
    }

    public boolean inCheck(Tools.Side side) {
        King piece = switch (side) {
            case White -> whiteKing;
            case Black -> blackKing;
        };
        return otherSideCanGet(side, piece.boardx, piece.boardy);
    }

    public boolean otherSideCanGet(Tools.Side side, int x, int y) {
        // TODO basically this method runs tens to hundreds of times more than the other
        for(Piece[] column : piecePositions)
            for(Piece piece : column){
                if(piece == null) continue;
                if (piece.side != side && piece.capturableSpaces().contains(Tools.toNum(x, y))) {
                    return true;
                }
            }

        return false;
    }

    public Board copy() {
        Board toreturn = new Board();
        for(int x = 0; x < 8; ++x)
            for(int y = 0; y < 8; ++y){
                Piece p = piecePositions[x][y];
                if(p == null) continue;
                if (p != whiteKing && p != blackKing && p != atEnd && p != enPassant)
                    toreturn.addPiece(p.copy());
            }
        toreturn.addKing(Tools.Side.White, whiteKing.copy());
        toreturn.addKing(Tools.Side.Black, blackKing.copy());
        toreturn.currentMove = currentMove;
        if (atEnd != null) {
            toreturn.atEnd = atEnd.copy();
            toreturn.addPiece(toreturn.atEnd);
        }
        if (enPassant != null) {
            toreturn.enPassant = enPassant.copy();
            toreturn.addPiece(toreturn.enPassant);
        }
        for(Piece[] column : toreturn.piecePositions)
            for(Piece p : column) {
                if (p == null) continue;
                p.board = toreturn;
            }
        return toreturn;
    }

    public boolean isEmpty(int x, int y) {
        return getPiece(x, y) == null;
    }

    public Tools.Side opposite() {
        return currentMove == Tools.Side.Black ? Tools.Side.White : Tools.Side.Black;
    }

    public boolean inBounds(int cx, int cy) {
        return cx < 8 && cx >= 0 && cy < 8 && cy >= 0;
    }

    /**
     * Returns the piece at (x, y)
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the piece at (x,y), or null if no piece exists
     */
    public Piece getPiece(int x, int y) {
        if(x < 0 || x >= 8 || y < 0 || y >= 8)
            return null;
        return piecePositions[x][y];
    }

    public Tools.Result getBoardResult() {
        // If the other side doesn't have any moves
        boolean otherSideHasMoves = false;
        outer:
        for(Piece[] column : piecePositions)
            for(Piece piece : column) {
                if (piece == null) continue;
                if (piece.side == this.currentMove && piece.canMove().size() > 0) {
                    otherSideHasMoves = true;
                    break outer;
                }
            }
        if (!otherSideHasMoves) {
            // Then we see if the king is in check
            for(Piece[] column : piecePositions)
                for(Piece piece : column) {
                    if (piece != null && piece.side == this.opposite() && inCheck(currentMove))
                        return this.currentMove == Tools.Side.Black ? Tools.Result.WhiteWon : Tools.Result.BlackWon;
                }
            return Tools.Result.Draw;
        }
        if(undoMoves.size() >= 6){
            // If all of the last 6 moves were just all only moving pieces
            if(undoMoves.get(undoMoves.size() - 1).size() == 1 && undoMoves.get(undoMoves.size() - 1).get(0).instruction == Tools.Instruction.move &&
                undoMoves.get(undoMoves.size() - 2).size() == 1 && undoMoves.get(undoMoves.size() - 2).get(0).instruction == Tools.Instruction.move &&
                undoMoves.get(undoMoves.size() - 3).size() == 1 && undoMoves.get(undoMoves.size() - 3).get(0).instruction == Tools.Instruction.move &&
                undoMoves.get(undoMoves.size() - 4).size() == 1 && undoMoves.get(undoMoves.size() - 4).get(0).instruction == Tools.Instruction.move &&
                undoMoves.get(undoMoves.size() - 5).size() == 1 && undoMoves.get(undoMoves.size() - 5).get(0).instruction == Tools.Instruction.move &&
                undoMoves.get(undoMoves.size() - 6).size() == 1 && undoMoves.get(undoMoves.size() - 6).get(0).instruction == Tools.Instruction.move){
                if(undoMoves.get(undoMoves.size() - 1).get(0).isEqual(undoMoves.get(undoMoves.size() - 5).get(0)) &&
                    undoMoves.get(undoMoves.size() - 2).get(0).isEqual(undoMoves.get(undoMoves.size() - 6).get(0)))
                    return Tools.Result.Draw;
            }
        }
        return null;
    }

    public void addUndoMove(Move toAdd) {
        assert this.undoMoves.get(undoMoves.size() - 1) != null;
        this.undoMoves.get(undoMoves.size() - 1).add(toAdd);
    }

}
