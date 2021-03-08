package engine.state;

import bot.BotMain;
import engine.SoundPlayer;
import engine.Tools;
import engine.board.BoardClient;

public class MainState implements State{

    public final BoardClient board;
    public final static Tools.Side playerSide = Tools.Side.White;
    private final BotMain botteu;
    boolean stayInState = true;

    public MainState(BoardClient board){
        this.board = board;
        botteu = new BotMain(board, playerSide == Tools.Side.White ? Tools.Side.Black : Tools.Side.White);
    }

    /**
     * Gets a result, and does the proper procedures to handle said result after a move
     * @param result the result
     */
    public void handleResult(Tools.Result result){
        if(result != null){
            Tools.setState(new FinishedState(board, result));
            SoundPlayer.stop();
            SoundPlayer.play(SoundPlayer.end);
            stayInState = false;
        }
    }

    // REPEATING METHODS

    @Override
    public void tick(){
        if(board.board.currentMove != playerSide && !board.isPromotionState()){
            Tools.Result result = botteu.mainMove();
            handleResult(result);
        }
    }

    @Override
    public void render() {
        board.render();
    }


    // EVENT HANDLING

    @Override
    public void mousePressed(int x, int y) {
        Tools.Result result = board.click(x, y, playerSide);
//        Tools.Result result = board.click(x, y, board.board.currentMove);
        handleResult(result);
        render();
    }

    @Override
    public void keyPressed(char key) {
        if(key == 'z'){
            board.board.undoLatest(Tools.opposite(playerSide));
            board.board.undoLatest(playerSide);
        }
    }
}
