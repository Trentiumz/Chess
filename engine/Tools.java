package engine;

import java.util.HashMap;

import engine.state.State;
import processing.core.PImage;

public class Tools {

    private Tools() {
    }

    // SPRITE TO IMAGE
    public static final HashMap<Sprite, PImage> pieces = new HashMap<>();

    // MAIN ENUMS
    public enum Sprite {
        board, bpawn, bbishop, bknight, brook, bqueen, bking,
        wpawn, wbishop, wknight, wrook, wqueen, wking, wallpaper
    }

    public enum Side {
        White, Black
    }

    public enum Piece {
        pawn, bishop, knight, rook, queen, king
    }

    public enum Result {
        WhiteWon, BlackWon, Draw
    }

    // TODO DEBUGGING
    public static int lastNum;

    // HAS THE PROMOTION ORDER
    public static final Tools.Piece[] promotionOrder = {Tools.Piece.queen, Tools.Piece.rook, Tools.Piece.bishop, Tools.Piece.knight};

    // INSTANCE OF THE MAIN FUNCTION
    private static Main owner;


    // STATE SET FUNCTION

    public static void setState(State toSet) {
        owner.currentState = toSet;
    }


    // SPRITE FUNCTIONS

    /**
     * Initialize the images with various sprites
     *
     * @implNote {board, bpawn, bbishop, bknight, brook, bqueen, bking, wpawn, wbishop, wknight, wrook, wqueen, wking, wallpaper}
     */
    public static void initialize(PImage[] sprites, Main main) {
        pieces.put(Sprite.board, sprites[0]);
        pieces.put(Sprite.bpawn, sprites[1]);
        pieces.put(Sprite.bbishop, sprites[2]);
        pieces.put(Sprite.bknight, sprites[3]);
        pieces.put(Sprite.brook, sprites[4]);
        pieces.put(Sprite.bqueen, sprites[5]);
        pieces.put(Sprite.bking, sprites[6]);

        pieces.put(Sprite.wpawn, sprites[7]);
        pieces.put(Sprite.wbishop, sprites[8]);
        pieces.put(Sprite.wknight, sprites[9]);
        pieces.put(Sprite.wrook, sprites[10]);
        pieces.put(Sprite.wqueen, sprites[11]);
        pieces.put(Sprite.wking, sprites[12]);

        pieces.put(Sprite.wallpaper, sprites[13]);
        owner = main;
    }

    // Draw Images
    public static void drawImage(PImage toDraw, int x, int y) {
        owner.drawImage(toDraw, x, y);
    }

    public static void drawImage(Sprite toDraw, int x, int y) {
        owner.drawImage(getSprite(toDraw), x, y);
    }

    // get image
    public static PImage getSprite(Sprite toGet) {
        return pieces.get(toGet);
    }

    /**
     * This is a way to simplify getting the proper sprites; you only need the side and type of piece
     *
     * @param side  The side that the piece is on (Black/White)
     * @param piece The type of piece that it is (Pawn/Bishop/Knight etc.)
     * @return The sprite which corresponds to the piece
     */
    public static Sprite getSprite(Side side, Piece piece) {
        return switch (side) {
            case White -> switch (piece) {
                case pawn -> Sprite.wpawn;
                case knight -> Sprite.wknight;
                case bishop -> Sprite.wbishop;
                case rook -> Sprite.wrook;
                case queen -> Sprite.wqueen;
                case king -> Sprite.wking;
            };
            case Black -> switch (piece) {
                case pawn -> Sprite.bpawn;
                case knight -> Sprite.bknight;
                case bishop -> Sprite.bbishop;
                case rook -> Sprite.brook;
                case queen -> Sprite.bqueen;
                case king -> Sprite.bking;
            };
        };
    }


    // DRAWING FUNCTIONS

    /**
     * Draws a rectangle on the screen
     *
     * @param sx      starting x position
     * @param sy      starting y position
     * @param w       width of the rectangle
     * @param h       height of rectangle
     * @param r       color-red value
     * @param g       color-green value
     * @param b       color-blue value
     * @param opacity the opacity
     */
    public static void drawRect(int sx, int sy, int w, int h, int r, int g, int b, int opacity) {
        owner.fill(r, g, b, opacity);
        owner.rect(sx, sy, w, h);
    }

    public static void drawText(String s, int fontSize, int x, int y, int r, int g, int b) {
        owner.fill(r, g, b);
        owner.textFont(owner.createFont("Arial Bold", fontSize));
        owner.text(s, x, y);
    }

    public static void drawCircle(int x, int y, int rad, int r, int g, int b, int opacity) {
        owner.fill(r, g, b, opacity);
        owner.circle(x, y, rad);
    }


    // SIMPLE CONVERSION FUNCTIONS

    /**
     * Converts a coordinate on the chess board into one integer
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the "hashed" number: x * 8 + y
     */
    public static int toNum(int x, int y) {
        return toNum(x, y, 8);
    }

    public static int toNum(int x, int y, int k) {
        return x * k + y;
    }

    // Get the x and y coordinates of the "hashed" number
    public static int getX(int num) {
        return toCoord(num)[0];
    }

    public static int getY(int num) {
        return toCoord(num)[1];
    }

    /**
     * Converts the single number into a coordinate on the chess board
     *
     * @param num the "hashed" number(is of form x * 8 + y)
     * @return the coordinate {x, y}
     */
    public static int[] toCoord(int num) {
        return toCoord(num, 8);
    }

    public static int[] toCoord(int num, int k) {
        int x = num / k;
        return new int[]{x, num - x * k};
    }

    public static Tools.Side opposite(Tools.Side side) {
        return side == Side.White ? Side.Black : Side.White;
    }
}
