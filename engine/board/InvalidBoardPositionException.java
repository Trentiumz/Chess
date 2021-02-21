package engine.board;

public class InvalidBoardPositionException extends RuntimeException{

    public InvalidBoardPositionException(String message){
        super(message);
    }

}
