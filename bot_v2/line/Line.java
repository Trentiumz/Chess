package bot_v2.line;

public class Line {

    public enum CellType{
        Empty,
        Ranged,
        Blocking,
        King
    }

    private int mask;
    private final LineEvaluator evaluator;
    private int kCnt;

    public Line(){
        mask = 0;
        evaluator = LineEvaluator.getLine();
    }

    public Line(Line copy){
        this.mask = copy.mask;
        this.evaluator = copy.evaluator;
        this.kCnt = copy.kCnt;
    }

    public Line(CellType[] beginning) throws IllegalArgumentException{
        this();
        if(beginning.length > 8) throw new IllegalArgumentException("Can't have a line of length over 8!");
        for(int i = 0; i < beginning.length; i++){
            addPiece(i, beginning[i]);
            if(beginning[i] == CellType.King) ++kCnt;
        }
        if(kCnt > 1) throw new IllegalArgumentException("Too many initial kings!");
        for(int i = beginning.length; i < 8; i++){
            addPiece(i, CellType.Blocking);
        }
    }

    /**
     * @return if mask is currently in check
     */
    public boolean inCheck(){
        return evaluator.inCheck(mask);
    }

    /**
     * Adds a piece to the line
     * @param ind index of piece
     * @param piece piece type
     * @throws IllegalArgumentException if the piece and index are not valid
     */
    public void addPiece(int ind, CellType piece) throws IllegalArgumentException{
        if(piece == CellType.King){
            ++kCnt;
            if(kCnt > 1) throw new IllegalArgumentException("Cannot have multiple kings!");
        }
        mask = evaluator.addPiece(mask, ind, switch(piece){
            case Ranged: yield 1;
            case Blocking: yield 2;
            case King: yield 3;
            case Empty: throw new IllegalArgumentException("Cannot add an empty piece");
        });
    }

    /**
     * Removes a piece from the line
     * @param ind the index to remove from
     * @throws IllegalArgumentException if removal is invalid
     */
    public void remPiece(int ind) throws IllegalArgumentException {
        if((mask >> (2 * ind) & 3) == 3) kCnt -= 1;
        mask = evaluator.remPiece(mask, ind);
    }

    public boolean equals(Line other){
        return this.mask == other.mask;
    }
}
