package bot_v2.ai_export;

import bot_v2.board.Board;
import bot_v2.rater.Version1Rating;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class aiFunctionTest {

    @Test
    void boardHashTest(){
        Board board = new Board(new Version1Rating());
        String val = board.boardHash();
        assertEquals("5 4 3 2 1 3 4 5 6 6 6 6 6 6 6 6 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 12 12 12 12 12 12 12 12 11 10 9 8 7 9 10 11", val);
    }
}
