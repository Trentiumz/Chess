package bot_v2;

import bot_v2.line.Line;

public class Piece {
    public final PieceType type;
    public final Side side;
    private final Line.CellType curSideType, enemyDiagType, enemyCardinalType;

    public final static Piece EMPTY = new Piece(PieceType.Empty, Side.Neither);
    public final static int[][] knightMoves = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
    public final static int[][] adjMoves = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    public final static int[][] cardinalMoves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    public final static int[][] diagonalMoves = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
    public final static PieceType[] promotable = {PieceType.Queen, PieceType.Bishop, PieceType.Knight, PieceType.Rook};

    public Piece(PieceType type, Side side) {
        this.type = type;
        this.side = side;

        this.curSideType = curSidePieceType();
        this.enemyCardinalType = enemyCardinalPieceType();
        this.enemyDiagType = enemyDiagonalPieceType();
    }

    /**
     * The piece type for the current side
     *
     * @return the piece type
     * @throws IllegalArgumentException if the piece is not on the current side
     */
    private Line.CellType curSidePieceType() throws IllegalArgumentException {
        return switch (this.type) {
            case King -> Line.CellType.King;
            case Empty -> Line.CellType.Empty;
            default -> Line.CellType.Blocking;
        };
    }

    /**
     * Returns the line equivalent type in a diagonal
     *
     * @return the piece type with relation to a line
     */
    private Line.CellType enemyDiagonalPieceType() {
        return switch (type) {
            case Bishop, Queen -> Line.CellType.Ranged;
            case Empty -> Line.CellType.Empty;
            default -> Line.CellType.Blocking;
        };
    }

    /**
     * Returns the Line equivalent type
     *
     * @return the piece type with relation to a Line
     */
    private Line.CellType enemyCardinalPieceType() {
        return switch (type) {
            case Rook, Queen -> Line.CellType.Ranged;
            case Empty -> Line.CellType.Empty;
            default -> Line.CellType.Blocking;
        };
    }

    public Line.CellType diagonalPieceType(Side curSide){
        return side == curSide ? curSideType : enemyDiagType;
    }

    public Line.CellType cardinalPieceType(Side curSide){
        return side == curSide ? curSideType : enemyCardinalType;
    }

    public boolean equals(Piece other){
        return other.type == type && other.side == side || type == PieceType.Empty && other.type == PieceType.Empty;
    }

    public boolean isEmpty(){
        return equals(EMPTY);
    }
}
