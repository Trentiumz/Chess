package bot_v2;

import bot_v2.board.Board;
import bot_v2.rater.Version1Rating;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BotMainErrorTesting {

    /*
    * Benchmarking
    * Revision 41576711c7dba201e7b640d2e916e0f3d17298f0, test1 at 5.866 seconds
    * Revision c1d0403211bfed15bb3bf1be0d93b42549f448f8, test1 at 4.929 seconds
     */
    @Test
    void test1() throws InterruptedException, TimeoutException {
        Piece[][] template = null;
        Board board = null;
        BotMain bot = null;

        template = new Piece[][]{
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Rook, Side.Black)},
        };
        board = new Board(template, Side.Black, new Version1Rating());
        bot = new BotMain(board, Side.Black);
        System.out.println(bot.getMove());
        System.out.println(bot.counter);

        template = new Piece[][]{
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
        };

        board = new Board(template, Side.Black, new Version1Rating());
        bot = new BotMain(board, Side.Black);
        System.out.println(bot.getMove());
        System.out.println(bot.counter);
    }

    @Test
    void testLogic() throws InterruptedException, TimeoutException {
        Piece[][] template = null;
        Board board = null;
        BotMain bot = null;

        template = new Piece[][]{
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither)},
        };
        board = new Board(template, Side.Black, new Version1Rating());
        bot = new BotMain(board, Side.Black);
        board.printBoard();
        System.out.println(bot.getMove());
        System.out.println(bot.counter);

        template = new Piece[][]{
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Rook, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Empty, Side.Neither)},
        };
        board = new Board(template, Side.Black, new Version1Rating());
        board.printBoard();
        bot = new BotMain(board, Side.Black);
        System.out.println(bot.getMove());

        template = new Piece[][]{
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
        };
        board = new Board(template, Side.Black, new Version1Rating());
        bot = new BotMain(board, Side.Black);
        System.out.println(bot.getMove());
    }

    @Test
    void test2() throws InterruptedException, TimeoutException {
        Piece[][] template = new Piece[][]{
                {new Piece(PieceType.Rook, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Queen, Side.White), new Piece(PieceType.King, Side.White), new Piece(PieceType.Bishop, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.White)},
                {new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.White), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Knight, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither)},
                {new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black), new Piece(PieceType.Pawn, Side.Black)},
                {new Piece(PieceType.Rook, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Queen, Side.Black), new Piece(PieceType.King, Side.Black), new Piece(PieceType.Bishop, Side.Black), new Piece(PieceType.Empty, Side.Neither), new Piece(PieceType.Rook, Side.Black)},
        };

        Board board = new Board(template, Side.Black, new Version1Rating());
        BotMain bot = new BotMain(board, Side.Black);
        Move bes = bot.getMove();
    }
}