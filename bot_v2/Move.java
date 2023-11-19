package bot_v2;

/**
 * Class to store a particular move in a data structure
 */
public class Move {
    /**
     * Piece movements
     */
    public int sr, sc, er, ec;
    public Piece consume;

    public boolean promote = false;
    /**
     * augment the move (pawn promotion)
     */
    public PieceType augment;

    public boolean enPassant = false;
    /**
     * augment the move (en passant)
     */
    public int ar, ac;

    public boolean castle = false;

    public Move(int sr, int sc, int er, int ec, Piece consume) {
        this.sr = sr;
        this.sc = sc;
        this.er = er;
        this.ec = ec;
        this.consume = consume;
    }

    public Move(int sr, int sc, int er, int ec, Piece consume, PieceType augment) {
        this(sr, sc, er, ec, consume);
        this.promote = false;
        this.augment = augment;
    }

    public Move(int sr, int sc, int er, int ec, Piece consume, int ar, int ac) {
        this(sr, sc, er, ec, consume);
        this.enPassant = true;
        this.ar = ar;
        this.ac = ac;
    }

    /**
     * constructor for castling
     *
     * @param sr     king r
     * @param sc     king c
     * @param er     final king c
     * @param ec     final king c
     * @param castle whether or not you want to castle
     */
    public Move(int sr, int sc, int er, int ec, boolean castle) {
        this(sr, sc, er, ec, Piece.EMPTY);
        this.castle = castle;
    }

    public boolean equals(Move other) {
        return this.sc == other.sc && this.sr == other.sr && this.ec == other.ec && this.er == other.er &&
                this.consume.equals(other.consume) && this.promote == other.promote && this.augment == other.augment &&
                this.enPassant == other.enPassant && this.ar == other.ar && this.ac == other.ac && this.castle == other.castle;
    }

    public String toString() {
        String cur = String.format("(%d, %d) -> (%d, %d)", sr, sc, er, ec);
        if (promote) cur += "; " + augment;
        if (enPassant) cur += String.format("; (%d, %d)", ar, ac);
        return "[" + cur + "]";
    }

}
