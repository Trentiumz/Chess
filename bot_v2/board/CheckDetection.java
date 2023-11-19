package bot_v2.board;

import bot_v2.Piece;
import bot_v2.PieceType;
import bot_v2.Side;
import bot_v2.line.Line;

import java.util.Arrays;

/**
 * Detects a check for a particular side
 */
public class CheckDetection {
    /**
     * check detection on columns, rows, bottom-left to top-right, bottom-right to top-left diagonals
     *
     * @implNote indexing for diagonals will always start from the bottommost and leftmost diagonal
     */
    private final Line[] columns, rows, diag1, diag2;
    /**
     * the current side
     */
    private final Side curSide;
    /**
     * coordinates of the king
     */
    private boolean hasKing;
    public int kr, kc;

    /**
     * Bottom left to top right, piece is denoted by board[r][c]
     */
    public Piece[][] board;

    public CheckDetection(CheckDetection copy){
        this.columns = Arrays.stream(copy.columns).map(Line::new).toArray(Line[]::new);
        this.rows = Arrays.stream(copy.rows).map(Line::new).toArray(Line[]::new);
        this.diag1 = Arrays.stream(copy.diag1).map(Line::new).toArray(Line[]::new);
        this.diag2 = Arrays.stream(copy.diag2).map(Line::new).toArray(Line[]::new);
        this.curSide = copy.curSide;
        this.hasKing = copy.hasKing;
        this.kr = copy.kr;
        this.kc = copy.kc;
        this.board = new Piece[copy.board.length][];
        for(int i = 0; i < copy.board.length; i++){
            board[i] = Arrays.copyOf(copy.board[i], copy.board[i].length);
        }
    }

    public CheckDetection(Piece[][] preset, Side curSide) throws IllegalArgumentException {
        // copy the board
        hasKing = false;
        this.board = new Piece[preset.length][];
        for(int i = 0; i < preset.length; i++){
            board[i] = Arrays.copyOf(preset[i], preset[i].length);
        }

        this.curSide = curSide;
        this.columns = new Line[8];
        this.rows = new Line[8];
        this.diag1 = new Line[15];
        this.diag2 = new Line[15];

        // look for the current position of the king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c].type == PieceType.King && board[r][c].side == curSide) {
                    kr = r;
                    kc = c;
                    // already found? can't have multiple
                    if (hasKing) throw new IllegalArgumentException("Multiple kings for side " + curSide);
                    hasKing = true;
                }
            }
        }

        // add each column
        for (int c = 0; c < 8; ++c) {
            columns[c] = new Line();
            for (int r = 0; r < 8; ++r) {
                if (board[r][c].type != PieceType.Empty) {
                    columns[c].addPiece(r, board[r][c].cardinalPieceType(curSide));
                }
            }
        }

        // add each row
        for (int r = 0; r < 8; ++r) {
            rows[r] = new Line();
            for (int c = 0; c < 8; ++c) {
                if (board[r][c].type != PieceType.Empty) {
                    rows[r].addPiece(c, board[r][c].cardinalPieceType(curSide));
                }
            }
        }

        // add bottom-left to top-right diagonal
        for (int d = 0; d < 15; ++d) {
            diag1[d] = new Line();
            for (int r = 0; r < 8; ++r) {
                int c = d - 7 + r;
                if (0 <= c && c < 8 && board[r][c].type != PieceType.Empty) {
                    diag1[d].addPiece(r, board[r][c].diagonalPieceType(curSide));
                }
            }
        }

        // add bottom right to top left diagonal
        for (int d = 0; d < 15; ++d) {
            diag2[d] = new Line();
            for (int r = 0; r < 8; ++r) {
                int c = d - r;
                if (0 <= c && c < 8 && board[r][c].type != PieceType.Empty) {
                    diag2[d].addPiece(r, board[r][c].diagonalPieceType(curSide));
                }
            }
        }
    }

    /**
     * removes a piece from consideration
     *
     * @param r the row of the piece
     * @param c the column of the piece
     */
    public void remPiece(int r, int c) {
        columns[c].remPiece(r);
        rows[r].remPiece(c);
        diag1[c - r + 7].remPiece(r);
        diag2[r + c].remPiece(r);

        if(board[r][c].side == curSide && board[r][c].type == PieceType.King) hasKing = false;
        board[r][c] = Piece.EMPTY;
    }

    /**
     * Adds a piece to the board
     *
     * @param r     the row
     * @param c     the column
     * @param toAdd the piece to add
     */
    public void addPiece(int r, int c, Piece toAdd) {
        columns[c].addPiece(r, toAdd.cardinalPieceType(curSide));
        rows[r].addPiece(c, toAdd.cardinalPieceType(curSide));
        diag1[c - r + 7].addPiece(r, toAdd.diagonalPieceType(curSide));
        diag2[r + c].addPiece(r, toAdd.diagonalPieceType(curSide));

        if(toAdd.side == curSide && toAdd.type == PieceType.King){
            hasKing = true;
            kr = r;
            kc = c;
        }
        board[r][c] = toAdd;
    }

    /**
     * @return whether the current side's king is in check
     */
    public boolean inCheck() throws IllegalStateException{
        if(!hasKing){
            throw new IllegalStateException("The King was removed...");
        }

        // check if any queens, bishops, or rooks have the king in range
        if (columns[kc].inCheck() || rows[kr].inCheck() || diag1[kc - kr + 7].inCheck() || diag2[kc + kr].inCheck())
            return true;

        // can a knight reach us?
        for (int[] i : Piece.knightMoves) {
            int cr = kr + i[0], cc = kc + i[1];
            if (0 <= cr && cr < 8 && 0 <= cc && cc < 8 && board[cr][cc].side != curSide && board[cr][cc].type == PieceType.Knight) {
                return true;
            }
        }

        // can the enemy king or an enemy pawn reach us?
        for (int[] i : Piece.adjMoves) {
            int cr = kr + i[0], cc = kc + i[1];
            if (0 <= cr && cr < 8 && 0 <= cc && cc < 8 && board[cr][cc].side != curSide) {
                int pawnCheck = curSide == Side.White ? 1 : -1;
                if (board[cr][cc].type == PieceType.King || (i[1] == -1 || i[1] == 1) && i[0] == pawnCheck && board[cr][cc].type == PieceType.Pawn) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean equals(CheckDetection other){
        boolean good = this.hasKing == other.hasKing && this.kr == other.kr && this.kc == other.kc && this.curSide == other.curSide;
        for(int i = 0; i < 8 && good; i++){
            good = this.columns[i].equals(other.columns[i]) && this.rows[i].equals(other.rows[i]);
        }
        for(int i = 0; i < 15 && good; i++){
            good = this.diag1[i].equals(other.diag1[i]) && this.diag2[i].equals(other.diag2[i]);
        }
        return good;
    }

}
