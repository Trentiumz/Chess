package bot;

public class UnableToMoveException extends RuntimeException{
    public UnableToMoveException(String message){
        super(message);
    }
}
