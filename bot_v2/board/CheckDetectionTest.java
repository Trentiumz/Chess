package bot_v2.board;

import bot_v2.Piece;
import bot_v2.PieceType;
import bot_v2.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckDetectionTest {
    @Test
    void basicTests(){
        Piece[][] board = new Piece[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
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

        CheckDetection chk = new CheckDetection(board, Side.White);
        assertFalse(chk.inCheck());
        chk.addPiece(5, 4, new Piece(PieceType.Queen, Side.Black));
        assertFalse(chk.inCheck());
        chk.remPiece(1, 4);
        assertTrue(chk.inCheck());
        chk.addPiece(1, 4, chk.board[0][6]);
        chk.remPiece(0, 6);
        assertFalse(chk.inCheck());

        chk.remPiece(0, 4);
        assertThrows(IllegalStateException.class, chk::inCheck);
        chk.addPiece(3, 6, new Piece(PieceType.King, Side.White));
        assertTrue(chk.inCheck());
        chk.remPiece(5, 4);
        assertFalse(chk.inCheck());
        chk.addPiece(4, 4, new Piece(PieceType.Queen, Side.Black));
        assertFalse(chk.inCheck());
    }

    @Test
    void bishopTest(){
        Piece[][] board = new Piece[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = Piece.EMPTY;
            }
        }
        board[5][5] = new Piece(PieceType.Bishop, Side.Black);
        board[2][2] = new Piece(PieceType.King, Side.White);

        CheckDetection chk = new CheckDetection(board, Side.White);
        assertTrue(chk.inCheck());
        chk.remPiece(5, 5);
        chk.addPiece(5, 2, new Piece(PieceType.Bishop, Side.Black));
        assertFalse(chk.inCheck());
    }
}