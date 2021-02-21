package engine.state;

import engine.Main;
import engine.Tools;

public class FadeState implements State{

    final State nextState;
    final State lastState;
    public static final short frameLength = 30;
    public static final short maxBlack = 255;

    private short currentFrame;

    public FadeState(State lastState, State nextState){
        this.lastState = lastState;
        this.nextState = nextState;
        currentFrame = 0;
    }


    // REPEATING FUNCTIONS

    @Override
    public void tick() {

    }

    @Override
    public void render() {
        if(currentFrame > 2 * frameLength)
            Tools.setState(nextState);
        else if(currentFrame > frameLength){
            nextState.render();
            Tools.drawRect(0, 0, Main.DEFAULT_SIZE, Main.DEFAULT_SIZE, 0, 0, 0, (maxBlack) * (2 * frameLength - currentFrame) / frameLength);
        }else{
            lastState.render();
            Tools.drawRect(0, 0, Main.DEFAULT_SIZE, Main.DEFAULT_SIZE, 0, 0, 0, (maxBlack) * (currentFrame) / frameLength);
        }

        ++currentFrame;
    }


    // EVENT HANDLING

    @Override
    public void mousePressed(int x, int y) {

    }

    @Override
    public void keyPressed(char key) {

    }
}
