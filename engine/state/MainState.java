package engine.state;

import bot_v2.*;
import bot_v2.board.Board;
import bot_v2.rater.Version1Rating;
import engine.SoundPlayer;
import engine.Tools;
import engine.board.BoardClient;

import java.util.concurrent.TimeoutException;

public class MainState implements State{

    public final BoardClient board;
    public final static Tools.Side playerSide = Tools.Side.White;
    boolean stayInState = true;

    public MainState(BoardClient board){
        this.board = board;
    }

    /**
     * Gets a result, and does the proper procedures to handle said result after a move
     * @param result the result
     */
    public void handleResult(Tools.Result result){
        if(result != null){
            Tools.setState(new FinishedState(board, result));
            SoundPlayer.stop();
            SoundPlayer.play(SoundPlayer.end);
            stayInState = false;
        }
    }

    // REPEATING METHODS

    @Override
    public void tick(){
        if(board.board.currentMove != playerSide && !board.isPromotionState()){
            Piece[][] botPieces = new Piece[8][8];
            for(int r = 0; r < 8; ++r){
                for(int c = 0; c < 8; ++c){
                    engine.board.Piece cur = board.board.getPiece(c, 7-r);
                    PieceType type = cur == null ? PieceType.Empty : switch(cur.getPiece()){
                        case king -> PieceType.King;
                        case pawn -> PieceType.Pawn;
                        case rook -> PieceType.Rook;
                        case queen -> PieceType.Queen;
                        case bishop -> PieceType.Bishop;
                        case knight -> PieceType.Knight;
                    };
                    botPieces[r][c] = new Piece(type, cur == null ? Side.Neither : cur.side == Tools.Side.White ? Side.White : Side.Black);
                }
            }
            bot_v2.board.Board botBoard = new Board(botPieces, playerSide == Tools.Side.White ? Side.Black : Side.White, new Version1Rating());
            botBoard.printConfig();
            BotMain botteu = new BotMain(botBoard, playerSide == Tools.Side.White ? Side.Black : Side.White);
            try {
                Move move = botteu.getMove();
                board.botClick(new int[]{move.sc, 7-move.sr}, new int[]{move.ec, 7-move.er});

                Tools.Result result = board.board.getBoardResult();
                if(result != null) handleResult(result);
            } catch (InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render() {
        board.render();
    }


    // EVENT HANDLING

    @Override
    public void mousePressed(int x, int y) {
        Tools.Result result = board.click(x, y, playerSide);
//        Tools.Result result = board.click(x, y, board.board.currentMove);
        handleResult(result);
        render();
    }

    @Override
    public void keyPressed(char key) {
        if(key == 'z'){
            board.board.undoLatest(Tools.opposite(playerSide));
            board.board.undoLatest(playerSide);
        }else if(key == 's'){
            board.board.printBoardConfig();
        }
    }
}
