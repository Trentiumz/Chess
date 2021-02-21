package engine.state;

import engine.Main;
import engine.SoundPlayer;
import engine.Tools;
import engine.board.BoardClient;

public class FinishedState implements State {

    final BoardClient board;
    final Tools.Result result;

    public static final int fontSize = 70;

    public FinishedState(BoardClient board, Tools.Result result) {
        this.board = board;
        this.result = result;
    }


    // REPEATING FUNCTIONS

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        board.render();
        Tools.drawRect(0, 0, Main.DEFAULT_SIZE, Main.DEFAULT_SIZE, 0, 0, 0, 150);
        switch (result) {
            case Draw -> Tools.drawText("Draw!", fontSize, Main.CELL_SIZE * 2, Main.CELL_SIZE * 2, 255, 255, 255);
            case WhiteWon -> Tools.drawText("White Won!", fontSize, Main.CELL_SIZE * 2, Main.CELL_SIZE * 2, 255, 255, 255);
            case BlackWon -> Tools.drawText("Black Won!", fontSize, Main.CELL_SIZE * 2, Main.CELL_SIZE * 2, 255, 255, 255);
        }

        Tools.drawText("Press S to return to Menu", fontSize / 2, Main.CELL_SIZE, Main.CELL_SIZE * 5, 255, 255, 255);
    }


    // EVENT HANDLING

    @Override
    public void mousePressed(int x, int y) {

    }

    @Override
    public void keyPressed(char key) {
        if (key == 's') {
            Tools.setState(new FadeState(this, new MenuState()));
            SoundPlayer.stop();
            SoundPlayer.play(SoundPlayer.start);
        }
    }
}
