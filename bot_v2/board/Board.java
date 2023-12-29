package bot_v2.board;

import bot_v2.Move;
import bot_v2.Piece;
import bot_v2.PieceType;
import bot_v2.Side;
import bot_v2.rater.BoardRater;
import bot_v2.rater.Version1Rating;
import engine.board.King;

import java.util.*;

/**
 * A board that stores game information :thumbsup:
 */
public class Board {
    // TODO draw mechanics, rook castle checking
    private final static boolean DEBUG = false;

    public final CheckDetection white, black;
    public Piece[][] board;

    private final ArrayDeque<Move> moves;
    private final ArrayDeque<Side> sides;
    private ArrayDeque<Board> copies;
    private final BoardRater rater;
    private Side curMove;

    public Board(Board copy) {
        this.white = new CheckDetection(copy.white);
        this.black = new CheckDetection(copy.black);
        this.board = new Piece[copy.board.length][];
        for (int i = 0; i < copy.board.length; i++) {
            this.board[i] = Arrays.copyOf(copy.board[i], copy.board[i].length);
        }
        this.moves = copy.moves.clone();
        this.sides = copy.sides.clone();
        if (DEBUG) this.copies = copy.copies.clone();
        this.rater = copy.rater.copy();
        this.curMove = copy.curMove;
    }

    public Board(Piece[][] board, Side curMove, BoardRater rater) {
        // if the board is null, then set it up with default values
        if (board == null) {
            board = new Piece[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board[i][j] = Piece.EMPTY;
                }
            }
            for (int i = 0; i < 8; i++) {
                board[1][i] = new Piece(PieceType.Pawn, Side.White);
                board[6][i] = new Piece(PieceType.Pawn, Side.Black);
            }
            board[0][0] = board[0][7] = new Piece(PieceType.Rook, Side.White);
            board[0][1] = board[0][6] = new Piece(PieceType.Knight, Side.White);
            board[0][2] = board[0][5] = new Piece(PieceType.Bishop, Side.White);
            board[0][3] = new Piece(PieceType.Queen, Side.White);
            board[0][4] = new Piece(PieceType.King, Side.White);

            board[7][0] = board[7][7] = new Piece(PieceType.Rook, Side.Black);
            board[7][1] = board[7][6] = new Piece(PieceType.Knight, Side.Black);
            board[7][2] = board[7][5] = new Piece(PieceType.Bishop, Side.Black);
            board[7][3] = new Piece(PieceType.Queen, Side.Black);
            board[7][4] = new Piece(PieceType.King, Side.Black);
        }

        // copy the preset board in
        this.board = new Piece[board.length][];
        for (int i = 0; i < board.length; i++) {
            this.board[i] = Arrays.copyOf(board[i], board[i].length);
        }

        // initialize rating system
        this.rater = rater;
        rater.setBoard(this.board);

        // create check detection boards
        white = new CheckDetection(board, Side.White);
        black = new CheckDetection(board, Side.Black);

        // create list of moves
        moves = new ArrayDeque<>();
        sides = new ArrayDeque<>();
        if (DEBUG) copies = new ArrayDeque<>();

        this.curMove = curMove;
    }

    public Board(BoardRater rater) {
        this(null, Side.White, rater);
    }


    /**
     * removes a piece from consideration
     *
     * @param r the row of the piece
     * @param c the column of the piece
     */
    private void remPiece(int r, int c) {
        white.remPiece(r, c);
        black.remPiece(r, c);
        rater.remPiece(r, c);

        board[r][c] = Piece.EMPTY;
    }

    /**
     * Adds a piece to the board
     *
     * @param r     the row
     * @param c     the column
     * @param toAdd the piece to add
     */
    private void addPiece(int r, int c, Piece toAdd) {
        white.addPiece(r, c, toAdd);
        black.addPiece(r, c, toAdd);
        rater.addPiece(toAdd, r, c);

        board[r][c] = toAdd;
    }

    /**
     * consider a normal move (only consisting of a piece moving to another cell, no augmentations)
     * This method tests if the boundaries are valid and the final position is not friendly - if so, it will add the piece
     *
     * @param sr    the starting row
     * @param sc    the starting column
     * @param nr    the ending row
     * @param nc    the ending column
     * @param addTo the list to add the move to
     */
    private void addNormalMove(int sr, int sc, int nr, int nc, List<Move> addTo, Side curMove) {
        if (0 <= nr && nr < 8 && 0 <= nc && nc < 8 && (board[nr][nc].isEmpty() || board[nr][nc].side != curMove)) {
            addTo.add(new Move(sr, sc, nr, nc, board[nr][nc]));
        }
    }

    /**
     * @return a list of moves for the current side
     */
    public List<Move> getMoves() {
        List<Move> ret = new ArrayList<>();
        int pawnMove = curMove == Side.White ? 1 : -1;
        Side otherSide = curMove == Side.White ? Side.Black : Side.White;

        // begin with all moves without considering checks
        for (int r = 0; r < 8; ++r) {
            for (int c = 0; c < 8; ++c) {
                if (board[r][c].side == curMove) {
                    switch (board[r][c].type) {
                        case Pawn -> {
                            // single move row
                            int sr = r + pawnMove;

                            // double move
                            if ((curMove == Side.White && r == 1 || curMove == Side.Black && r == 6) &&
                                    board[r + pawnMove][c].isEmpty() && board[r + 2 * pawnMove][c].isEmpty()) {
                                ret.add(new Move(r, c, r + 2 * pawnMove, c, Piece.EMPTY));
                            }

                            // single move that stays within boundaries
                            if (0 < sr && sr < 7) {
                                if (board[sr][c].isEmpty()) {
                                    ret.add(new Move(r, c, sr, c, Piece.EMPTY));
                                }
                            } else if (0 == sr || sr == 7) {
                                if (board[sr][c].isEmpty()) {
                                    // single move reaches boundaries; promotion
                                    for (PieceType promoteTo : Piece.promotable) {
                                        ret.add(new Move(r, c, r + pawnMove, c, Piece.EMPTY, promoteTo));
                                    }
                                }
                            }

                            // diagonal snatch!
                            if (0 <= sr && sr < 8) {
                                for (int cc : new int[]{-1, 1}) {
                                    int nc = c + cc;
                                    if (0 <= nc && nc < 8 && !board[sr][nc].isEmpty() && board[sr][nc].side == otherSide) {
                                        if (0 < sr && sr < 7) {
                                            ret.add(new Move(r, c, sr, nc, board[sr][nc]));
                                        } else {
                                            for (PieceType promoteTo : Piece.promotable) {
                                                ret.add(new Move(r, c, sr, nc, board[sr][nc], promoteTo));
                                            }
                                        }
                                    }
                                }
                            }

                            // en passant
                            Move lastMove = moves.peekLast();
                            if (lastMove != null && lastMove.ec == lastMove.sc && Math.abs(lastMove.sc - c) == 1 && board[lastMove.er][lastMove.ec].type == PieceType.Pawn &&
                                    (curMove == Side.White && r == 4 && lastMove.sr == 6 && lastMove.er == 4 ||
                                            curMove == Side.Black && r == 3 && lastMove.sr == 1 && lastMove.er == 3) && board[sr][lastMove.ec].isEmpty()) {
                                ret.add(new Move(r, c, sr, lastMove.ec, Piece.EMPTY, lastMove.er, lastMove.ec));
                            }
                        }
                        case King -> {
                            for (int[] i : Piece.adjMoves) {
                                addNormalMove(r, c, r + i[0], c + i[1], ret, curMove);
                            }

                            // TODO check if king or rook moved before
                            if ((curMove == Side.White && r == 0 || curMove == Side.Black && r == 7) && c == 4) {
                                if (board[r][7].type == PieceType.Rook && board[r][7].side == curMove && board[r][5].isEmpty() && board[r][6].isEmpty()) {
                                    ret.add(new Move(r, 4, r, 6, true));
                                }
                                if (board[r][0].type == PieceType.Rook && board[r][0].side == curMove && board[r][1].isEmpty() && board[r][2].isEmpty() && board[r][3].isEmpty()) {
                                    ret.add(new Move(r, 4, r, 2, true));
                                }
                            }
                        }
                        case Rook -> {
                            for (int[] i : Piece.cardinalMoves) addRangedMoves(ret, r, c, i);
                        }
                        case Bishop -> {
                            for (int[] i : Piece.diagonalMoves) addRangedMoves(ret, r, c, i);
                        }
                        case Queen -> {
                            for (int[] i : Piece.adjMoves) addRangedMoves(ret, r, c, i);
                        }
                        case Knight -> {
                            for (int[] i : Piece.knightMoves) {
                                addNormalMove(r, c, r + i[0], c + i[1], ret, curMove);
                            }
                        }
                    }
                }
            }
        }

        // the current side (note that the sides are going to swap when you simulate)
        Side movingSide = curMove;
        // filter out moves that will result in a check for this side
        ret.removeIf(move -> {
            makeMove(curMove, move);
            boolean inCheck = (movingSide == Side.White ? white : black).inCheck();
            undoLastMove();

            // in a castle, the entire path cannot have a check!
            if(move.castle) {
                CheckDetection curChecker = curMove == Side.White ? white : black;
                inCheck = inCheck || curChecker.inCheck();

                makeMove(curMove, new Move(move.sr, move.sc, move.sr, move.sc + Integer.signum(move.ec - move.sc), Piece.EMPTY));
                inCheck = inCheck || curChecker.inCheck();
                undoLastMove();
            }
            return inCheck;
        });

        return ret;
    }

    /**
     * @return whether the current position is lost for the current player
     */
    public boolean isLost() {
        if (!(curMove == Side.White ? white : black).inCheck()) return false;
        return getMoves().isEmpty();
    }

    private void addRangedMoves(List<Move> ret, int r, int c, int[] i) {
        for (int nr = r + i[0], nc = c + i[1]; 0 <= nr && nr < 8 && 0 <= nc && nc < 8 && board[nr][nc].side != curMove;
             nr += i[0], nc += i[1]) {
            addNormalMove(r, c, nr, nc, ret, curMove);
            if (!board[nr][nc].isEmpty()) break;
        }
    }

    /**
     * makes a move, updating the internal variables
     *
     * @param side the side that moved
     * @param move the move to make
     */
    public void makeMove(Side side, Move move) throws IllegalArgumentException {
        if (side != curMove || board[move.sr][move.sc].side != side)
            throw new IllegalArgumentException("The move must act on a piece of side " + side);
        if (!board[move.er][move.ec].equals(move.consume))
            throw new IllegalArgumentException("The move must consume the piece at the right position: " + move);
        Piece curPiece = board[move.sr][move.sc];

        // eating another piece
        if (move.consume.type != PieceType.Empty) remPiece(move.er, move.ec);
        if (move.enPassant) remPiece(move.ar, move.ac);

        // remove the piece at the current location
        remPiece(move.sr, move.sc);

        // add the current piece at the new location
        if (move.promote) {
            addPiece(move.er, move.ec, new Piece(move.augment, side));
        } else {
            addPiece(move.er, move.ec, curPiece);
        }

        // if we're castling, then move the rook to position
        if (move.castle) {
            int rookSC, rookEC;
            if (move.ec > move.sc) {
                rookSC = 7;
                rookEC = 5;
            } else {
                rookSC = 0;
                rookEC = 3;
            }
            if (board[move.sr][rookSC].type != PieceType.Rook)
                throw new IllegalArgumentException("No rook at (" + move.sr + " " + rookSC + ")");
            Piece rook = board[move.sr][rookSC];
            remPiece(move.sr, rookSC);
            addPiece(move.sr, rookEC, rook);
        }

        moves.addLast(move);
        sides.addLast(side);

        curMove = curMove == Side.White ? Side.Black : Side.White;

        // get the rater to move
        rater.makeMove(move);

        if (DEBUG) {
            copies.add(new Board(this));
            white.inCheck();
            black.inCheck();
        }
    }

    /**
     * Undoes the last move
     */
    public void undoLastMove() throws IllegalStateException {
        if (moves.isEmpty()) {
            throw new IllegalStateException("There are no moves to undo!");
        }
        Move lastMove = moves.removeLast();
        Side lastSide = sides.removeLast();
        if (DEBUG) copies.removeLast();
        Piece movedPiece = board[lastMove.er][lastMove.ec];

        // remove the piece at the new position
        remPiece(lastMove.er, lastMove.ec);

        // add the piece in the original position
        if (lastMove.promote) {
            addPiece(lastMove.sr, lastMove.sc, new Piece(PieceType.Pawn, lastSide));
        } else {
            addPiece(lastMove.sr, lastMove.sc, movedPiece);
        }

        // add back any captured pieces
        if (lastMove.consume != null && lastMove.consume.type != PieceType.Empty)
            addPiece(lastMove.er, lastMove.ec, lastMove.consume);
        if (lastMove.enPassant)
            addPiece(lastMove.ar, lastMove.ac, new Piece(PieceType.Pawn, lastSide == Side.White ? Side.Black : Side.White));

        // move the rook back in castling
        if (lastMove.castle) {
            int rookSC, rookEC;
            if (lastMove.ec > lastMove.sc) {
                rookSC = 5;
                rookEC = 7;
            } else {
                rookSC = 3;
                rookEC = 0;
            }
            if (board[lastMove.sr][rookSC].type != PieceType.Rook)
                throw new IllegalArgumentException("Undoing a move: no rook at (" + lastMove.sr + " " + rookSC + ")");
            Piece rook = board[lastMove.sr][rookSC];
            remPiece(lastMove.sr, rookSC);
            addPiece(lastMove.sr, rookEC, rook);
        }

        curMove = curMove == Side.White ? Side.Black : Side.White;

        // get the rater to move
        rater.undoLastMove();
    }

    /**
     * Returns the rating of the board
     *
     * @return The rating of the board
     * @implNote The rating is done such that a higher rating means white is winning, while a lower rating means black advantage
     */
    public float rating() {
        if (isLost()) return curMove == Side.White ? -9000 : 9000;

        return rater.rating();
    }

    public Side getCurMove() {
        return curMove;
    }

    public boolean checkLines() {
        CheckDetection white = new CheckDetection(this.board, Side.White);
        CheckDetection black = new CheckDetection(this.board, Side.Black);
        return white.equals(this.white) && black.equals(this.black);
    }

    public void printConfig() {
        System.out.println("{");
        for (int r = 0; r < 8; ++r) {
            System.out.print("{");
            for (int c = 0; c < 8; ++c) {
                System.out.printf("new Piece(PieceType.%s, Side.%s)", board[r][c].type.name(), board[r][c].side.name());
                if (c != 7) System.out.print(", ");
            }
            System.out.println("},");
        }
        System.out.println("}");
    }

    public void printBoard() {
        for (int r = 7; r >= 0; --r) {
            for (int c = 0; c < 8; ++c) {
                System.out.print(board[r][c].type.name().substring(0, 1) + board[r][c].side.name().charAt(0) + " ");
            }
            System.out.println();
        }
    }

    /**
     * @return a hash of the board in readable string format
     */
    public String boardHash(){
        StringBuilder sb = new StringBuilder();
        for(int r = 0; r < 8; ++r) for(int c = 0; c < 8; ++c) {
            int val = switch(board[r][c].type){
                case King-> 1;
                case Queen -> 2;
                case Bishop -> 3;
                case Knight -> 4;
                case Rook -> 5;
                case Pawn -> 6;
                case Empty -> 0;
            };
            if(board[r][c].type != PieceType.Empty && board[r][c].side == Side.Black) val += 6;
            sb.append(val);
            if(r < 7 || c < 7) sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * compares two boards
     * @param other the other board
     * @return if the two boards are equal
     * @implNote this method does not compare the 'copies' instance variables
     * @throws IllegalStateException if an illegal state is reached somehow...
     */
    public boolean equals(Board other) throws IllegalStateException{
        if(!this.white.equals(other.white) || !this.black.equals(other.black)) return false;
        for(int r = 0; r < 8; r++) for(int c = 0; c < 8; c++) if(!this.board[r][c].equals(other.board[r][c])) return false;

        if(this.moves.size() != this.sides.size()) throw new IllegalStateException("Somehow the move and sides history isn't the same...");
        if(this.moves.size() != other.moves.size() || this.sides.size() != other.sides.size()) return false;
        for(Iterator<Move> a = this.moves.iterator(), b = other.moves.iterator(); a.hasNext() && b.hasNext(); ){
            if(!a.next().equals(b.next())) return false;
        }
        for(Iterator<Side> a = this.sides.iterator(), b = other.sides.iterator(); a.hasNext() && b.hasNext(); ){
            if(a.next() != b.next()) return false;
        }
        if(this.curMove != other.curMove) return false;
        return this.rater.equals(other.rater);
    }
}
