package engine.board;

public class InvalidPieceException extends RuntimeException{
    public InvalidPieceException(String message){
        super(message);
    }
}
