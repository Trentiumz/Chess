package engine.board;

import java.util.ArrayList;
import java.util.HashMap;

import engine.Tools;
import org.jetbrains.annotations.NotNull;

public class Board implements Copyable {

    final ArrayList<Piece> pieces;
    King whiteKing;
    King blackKing;
    public Tools.Side currentMove;

    public Pawn atEnd;
    public Pawn enPassant;

    int moveNum = 0;
    public final static byte drawMoveCount = 100;
    public static HashMap<Tools.Piece, Integer> scores = new HashMap<>() {{
        put(Tools.Piece.queen, 100);
        put(Tools.Piece.king, 900);
        put(Tools.Piece.rook, 50);
        put(Tools.Piece.bishop, 30);
        put(Tools.Piece.knight, 30);
        put(Tools.Piece.pawn, 10);
    }};

    public Board() {
        pieces = new ArrayList<>();
    }


    // MOVE HANDLERS

    /**
     * Tries to move the piece
     *
     * @param piece the piece to be moved
     * @param nx    the next x coordinate
     * @param ny    the next y coordinate
     * @return true if the piece was successful in moving, false if that move wasn't valid
     */
    public boolean movePiece(@NotNull Piece piece, int nx, int ny) {
        return piece.move(nx, ny);
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

    public void promote(Tools.Piece piece) {
        this.addPiece(atEnd.promote(piece));
        removePiece(atEnd);
        atEnd = null;
    }

    public void removePiece(Piece toremove) {
        pieces.remove(toremove);
        if(enPassant == toremove)
            enPassant = null;
        if(atEnd == toremove)
            atEnd = null;
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public void addKing(Tools.Side side, King king) {
        setKing(side, king);
        this.pieces.add(king);
    }

    public void setKing(Tools.Side side, King king) {
        switch (side) {
            case White -> this.whiteKing = king;
            case Black -> this.blackKing = king;
        }
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
        for (Piece p : pieces) {
            if (p.side == currentSide)
                rating += scores.get(p.getPiece());
            else
                rating -= scores.get(p.getPiece());
        }
        if (getBoardResult() == switch (currentSide) {
            case White -> Tools.Result.WhiteWon;
            case Black -> Tools.Result.BlackWon;
        }) {
            return 900;
        } else if (getBoardResult() == Tools.Result.Draw)
            return 0;
        else if (getBoardResult() != null)
            // Here, if it isn't a draw, and he didn't win, and there was a result, then the other side won
            return -900;
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
        for (Piece p : pieces) {
            if (p.side == side) {
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
        for (Piece piece : pieces) {
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
        boolean isChecked = false;
        for (Piece piece : pieces)
            if (piece.side != side && piece.capturableSpaces().contains(Tools.toNum(x, y))) {
                isChecked = true;
                break;
            }
        return isChecked;
    }

    public Board copy() {
        Board toreturn = new Board();
        for (Piece p : pieces)
            if (p != whiteKing && p != blackKing && p != atEnd && p != enPassant)
                toreturn.addPiece(p.copy());
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
        for (Piece p : toreturn.pieces)
            p.board = toreturn;
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
        for (Piece piece : pieces)
            if (piece.boardx == x && piece.boardy == y)
                return piece;
        return null;
    }

    public Tools.Result getBoardResult() {
        // Check for perpetual
        if (moveNum >= drawMoveCount)
            return Tools.Result.Draw;

        // If the other side doesn't have any moves
        boolean otherSideHasMoves = false;
        for (Piece piece : this.pieces)
            if (piece.side == this.currentMove && piece.canMove().size() > 0) {
                otherSideHasMoves = true;
                break;
            }
        if (!otherSideHasMoves) {
            // Then we see if the king is in check
            King otherKing = this.getKing(this.currentMove);
            for (Piece piece : this.pieces)
                if (piece.side == this.opposite() && piece.canMove().contains(Tools.toNum(otherKing.boardx, otherKing.boardy)))
                    return this.currentMove == Tools.Side.Black ? Tools.Result.WhiteWon : Tools.Result.BlackWon;
            return Tools.Result.Draw;
        }
        return null;
    }
}
