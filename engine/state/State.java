package engine.state;

public interface State {

    void tick();
    void render();
    void mousePressed(int x, int y);
    void keyPressed(char key);

}
