package engine.board;

import engine.Tools;

public class Move {

    Tools.Instruction instruction;
    Piece piece;
    int[] coords;
    public Move(Tools.Instruction instruction, Piece piece, int[] coords){
        this.instruction = instruction;
        this.piece = piece;
        this.coords = coords;
    }

    public boolean isEqual(Move other){
        boolean toreturn = instruction == other.instruction && piece == other.piece && coords.length == other.coords.length;
        if(!toreturn)
            return false;
        for(int i = 0; i < coords.length; ++i)
            toreturn = toreturn && coords[i] == other.coords[i];
        return toreturn;
    }

}
