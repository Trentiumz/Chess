package engine.board;

import engine.Tools;

import java.util.ArrayList;

public class Move {

    Tools.Instruction instruction;
    Piece piece;
    int[] coords;
    ArrayList<Piece> checking;
    public Move(Tools.Instruction instruction, Piece piece, int[] coords){
        this.instruction = instruction;
        this.piece = piece;
        this.coords = coords;
    }

    public Move(Tools.Instruction instruction, ArrayList<Piece> checking){
        this.instruction = instruction;
        this.checking = checking;
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
