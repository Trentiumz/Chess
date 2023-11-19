package bot_v2.board;

import bot_v2.Move;
import bot_v2.Piece;
import bot_v2.PieceType;
import bot_v2.Side;
import bot_v2.rater.Version1Rating;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void moveNumberTest() {
        Board board = new Board(Version1Rating.getRater());
        assertEquals(board.getMoves().size(), 20);
    }

    @Test
    void moveUndoTest() {
        Board board = new Board(Version1Rating.getRater());
        board.makeMove(Side.White, new Move(1, 3, 3, 3, Piece.EMPTY));
        assertEquals(board.getMoves().size(), 20);
        assertTrue(board.checkLines());

        board.undoLastMove();
        assertEquals(board.getMoves().size(), 20);
        assertTrue(board.checkLines());

        board.makeMove(Side.White, new Move(1, 3, 3, 3, Piece.EMPTY));
        assertTrue(board.checkLines());
        board.makeMove(Side.Black, new Move(6, 3, 4, 3, Piece.EMPTY));
        assertEquals(14 + 5 + 2 + 5 + 1, board.getMoves().size());
        assertTrue(board.checkLines());
    }

    @Test
    void ratingTest() {
        Piece[][] template;
        Board board;
        template = new Piece[][]{
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Queen, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither)},
        };
        System.out.println(new Board(template, Side.Black, Version1Rating.getRater()).rating());


        template = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                template[i][j] = Piece.EMPTY;
            }
        }
        template[0][0] = new Piece(PieceType.King, Side.White);
        template[7][0] = new Piece(PieceType.King, Side.Black);
        board = new Board(template, Side.White, Version1Rating.getRater());
        assertEquals(0, board.rating(), 0.05f);

        Piece[][] template1 = {
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Rook, Side.Black)},
        };
        board = new Board(template1, Side.Black, Version1Rating.getRater());
        System.out.println(board.rating());
    }

    @Test
    void type1Test() {
        Piece[][] template = {
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
        };
        Board board = new Board(template, Side.Black, Version1Rating.getRater());
        board.getMoves();
    }

    @Test
    void castleTest() {
        Piece[][] template = null;
        Board board = null;
        List<Move> moves = null;

        template = new Piece[][]{
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
        };
        board = new Board(template, Side.White, Version1Rating.getRater());
        moves = board.getMoves();
        assertEquals(1, moves.stream().filter(move -> move.castle).count());
        Board copy1 = new Board(board);
        board.makeMove(Side.White, moves.stream().filter(move -> move.castle).collect(Collectors.toList()).get(0));
        board.undoLastMove();
        Board copy2 = new Board(board);
        assertTrue(copy2.equals(copy1));

        // now the queen checks!
        template = new Piece[][]{
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
        };
        board = new Board(template, Side.White, Version1Rating.getRater());
        moves = board.getMoves();
        assertEquals(0, moves.stream().filter(move -> move.castle).count());
    }

}