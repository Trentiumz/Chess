package engine;

import engine.state.MenuState;
import engine.state.State;
import processing.core.PApplet;
import processing.core.PImage;

public class Main extends PApplet {

    // These can be extrapolated, but there's no need to optimize this early in development
    public final static short DEFAULT_SIZE = 800;
    public final static byte CELL_SIZE = DEFAULT_SIZE / 8;

    State currentState;


    // START METHODS

    public void settings() {
        size(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public void setup() {
        // Arrays for the pieces
        String[] pieces = {"board", "bpawn", "bbishop", "bknight", "brook", "bqueen", "bking", "wpawn", "wbishop",
                "wknight", "wrook", "wqueen", "wking"};
        PImage[] results = new PImage[14];

        // Looping through the strings, and putting the image in the result
        for (int i = 0; i < 13; ++i)
            results[i] = loadImage("sprites/" + pieces[i] + ".png");
        results[0].resize(DEFAULT_SIZE, DEFAULT_SIZE);
        for (int i = 1; i < 13; ++i)
            results[i].resize(CELL_SIZE, CELL_SIZE);
        results[13] = loadImage("background.jpg");
        results[13].resize(DEFAULT_SIZE, DEFAULT_SIZE);

        // initialize the sprites in the tools
        Tools.initialize(results, this);

        // Initialize the board
        currentState = new MenuState();

        SoundPlayer.init("data/startMusic.wav", "data/duringMusic.wav", "data/endMusic.wav");
        SoundPlayer.play(SoundPlayer.start);

    }


    // EVENT HANDLING

    public void mousePressed() {
        currentState.mousePressed(mouseX, mouseY);
    }

    public void keyPressed() {
        currentState.keyPressed(key);
    }


    // REPEATING TICKER-RENDERER FUNCTION

    public void draw() {
        currentState.tick();
        currentState.render();
    }


    // TEMPLATE MAIN FUNCTIONS

    public static void main(String[] args) {
        PApplet.main(getSketchClassName());
    }

    public static String getSketchClassName() {
        return Thread.currentThread().getStackTrace()[1].getClassName();
    }

    public void drawImage(PImage image, int x, int y) {
        this.image(image, x, y);
    }
}
