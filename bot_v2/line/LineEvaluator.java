package bot_v2.line;

/**
 * A data structure class that can be used to decide if a number of ranged pieces are able to hit the king
 *
 * This class only evaluates a particular line, and doesn't actually store any one line data
 */
class LineEvaluator {
    /**
     * Length of the line
     */
    private final static int LENGTH = 8;

    // little endian; 8 pairs of 2 bits with 00=empty, 1=ranged piece, 2=blocking piece, 3 = king
    private boolean[] inCheck = new boolean[1<<16];
    private boolean[] isValid = new boolean[1 << 16];

    private static LineEvaluator singleton = null;

    // a mask to extract the 2 last numbers
    private final static int dual = 3;

    /**
     * Class constructor
     *
     * @implNote Also initializes all the mask evaluators
     */
    private LineEvaluator(){
        // iterate through king position
        for(int msk = 0; msk < 1<<16; ++msk){
            // count number of "king positions" in the line
            int kCnt = 0, kp = -1;
            for(int i = 0; i < 8; i++) {
                if (((msk >> (2 * i)) & dual) == 3) {
                    ++kCnt;
                    kp = i;
                }
            }

            // whether it is a legal position
            boolean mskValid = kCnt <= 1;
            // whether we're in check
            boolean inCheck = false;

            if(kCnt == 1){
                // from the current king position, if there is a ranged before blocking, then we're in check
                for(int g = kp + 1; g < 8 && !inCheck && ((msk >> (2*g) & dual) != 2); ++g) {
                    inCheck = (msk >> (2*g) & dual) == 1;
                }
                for(int g = kp - 1; g >= 0 && !inCheck && ((msk >> (2*g) & dual) != 2); --g) {
                    inCheck = (msk >> (2*g) & dual) == 1;
                }
            }

            this.inCheck[msk] = mskValid && inCheck;
            this.isValid[msk] = mskValid;
        }
    }

    /**
     * Singleton constructor
     * @return singleton line
     */
    public static LineEvaluator getLine(){
        if(singleton == null){
            return singleton = new LineEvaluator();
        }
        return singleton;
    }

    /**
     * Returns whether a line has a check
     * @param msk the mask
     * @return whether the mask is in check
     * @throws IllegalArgumentException thrown if the mask is invalid
     */
    boolean inCheck(int msk) throws IllegalArgumentException{
        if(msk < 0 || msk > isValid.length || !isValid[msk]) {
            throw new IllegalArgumentException("Invalid Mask!");
        }
        return inCheck[msk];
    }

    /**
     * Removes a piece from the mask
     * @param msk the mask
     * @param ind the index to remove a piece from
     * @return a mask without the piece there anymore
     * @throws IllegalArgumentException if the mask or index is invalid
     */
    int remPiece(int msk, int ind) throws IllegalArgumentException {
        if(msk < 0 || msk > isValid.length || !isValid[msk]){
            throw new IllegalArgumentException("Invalid Mask!");
        }
        int val = msk >> (2 * ind) & dual;
        if(val == 0) {
            throw new IllegalArgumentException("Invalid Index Removal (no piece here)");
        }

        return msk & ~(val << (2*ind));
    }

    /**
     * Adds a piece to the mask
     * @param msk the mask
     * @param ind the index to add a piece to
     *            <ul>
     *              <li>type = 1 means a ranged piece</li>
     *              <li>type = 2 means a blocking piece</li>
     *              <li>type = 3 means a king</li>
     *            </ul>
     * @return a mask with the piece type there
     * @throws IllegalArgumentException if the mask or index is invalid
     */
    int addPiece(int msk, int ind, int type) throws IllegalArgumentException {
        if(msk < 0 || msk > isValid.length || !isValid[msk] || type <= 0 || type > 3){
            throw new IllegalArgumentException("Invalid Mask!");
        }
        if((msk >> (2*ind) & dual) != 0) {
            throw new IllegalArgumentException("Invalid Piece add index (there's already a piece here)");
        }
        return msk | (type << (2*ind));
    }

}
