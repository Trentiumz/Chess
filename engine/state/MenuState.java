package engine.state;

import engine.Main;
import engine.SoundPlayer;
import engine.Tools;
import engine.board.BoardClient;

public class MenuState implements State {

    public final short x = Main.DEFAULT_SIZE / 2 - Main.CELL_SIZE * 2;
    public final short y = Main.DEFAULT_SIZE / 2 - Main.CELL_SIZE / 2;
    public final short w = 4 * Main.CELL_SIZE;
    public final short h = Main.CELL_SIZE;


    // REPEATING FUNCTIONS

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        Tools.drawImage(Tools.Sprite.wallpaper, 0, 0);
        Tools.drawRect(x, y, w, h, 222, 184, 135, 255);
        Tools.drawText("START", Main.CELL_SIZE, x + w / 11, y + h - h / 7, 0, 0, 0);
    }


    // EVENT HANDLING

    @Override
    public void mousePressed(int x, int y) {
        if (x > this.x && x < this.x + this.w && y > this.y && y < this.y + this.h) {
            Tools.setState(new FadeState(this, new MainState(new BoardClient())));
            SoundPlayer.stop();
            SoundPlayer.play(SoundPlayer.during);
        }
    }

    @Override
    public void keyPressed(char key) {

    }
}
