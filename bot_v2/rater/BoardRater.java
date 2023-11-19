package bot_v2.rater;

import bot_v2.board.Board;
import bot_v2.Side;

public interface BoardRater {
    float rating(Board board, Side curSide);
}
