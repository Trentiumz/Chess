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

}
