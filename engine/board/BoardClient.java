package engine.board;

import engine.Main;
import engine.Tools;


public class BoardClient {

    final public Board board;

    private Piece selectedPiece = null;

    // The different states
    public final MainState mainState;
    public final PromotionState promotionState;
    ClientState currentState;

    /**
     * Does most of the rendering for the {@code Board}, and also communicates movements from a bot/client
     */
    public BoardClient() {
        board = new Board();
        board.initialize();

        mainState = new MainState();
        promotionState = new PromotionState();
        currentState = mainState;
    }


    // HANDLING CLICKS

    public Tools.Result click(int x, int y, Tools.Side currentSide) {
        //if(board.currentMove == currentSide)
        return currentState.click(x, y);
        // return null;
    }

    public Tools.Result botClick(int[] start, int[] end) {
        return this.currentState.botClick(start, end);
    }


    // SIMPLIFICATION TOOL METHODS

    /**
     * update the move so that it's the next player's turn
     */
    private void nextMove() {
        board.nextMove();
        selectedPiece = null;

    }

    public void render() {
        currentState.render();
    }


    // THE DIFFERENT STATES

    /**
     * The typical state where the players are moving a piece
     */
    class MainState implements ClientState {
        public Tools.Result click(int x, int y) {
            x = x / Main.CELL_SIZE;
            y = y / Main.CELL_SIZE;
            if (x > 7 || x < 0 || y > 7 || y < 0)
                throw new InvalidBoardPositionException("engine.board had cell clicked at (" + x + " " + y + "); not valid!");

            // If selectedPiece != null, then we move the piece
            if (selectedPiece != null) {
                if(!board.canMove(selectedPiece, x, y))
                    selectedPiece = null;
                else {
                    board.movePiece(selectedPiece, x, y);
                    if (board.atEnd == null)
                        nextMove();
                    if (board.atEnd != null) {
                        currentState = promotionState;
                        return null;
                    }
                }
            } else {
                // Get the piece at the cell, and select it
                Piece pieceAtCell = board.getPiece(x, y);
                if (pieceAtCell != null && pieceAtCell.side == board.currentMove)
                    selectedPiece = board.getPiece(x, y);
            }
            return getBoardResult();
        }

        // For the AI to be able to click
        @Override
        public Tools.Result botClick(int[] start, int[] end) {
            Piece selectedPiece = board.getPiece(start[0], start[1]);
            if (board.enPassant != null && board.enPassant.side == board.currentMove){
                board.addUndoMove(new Move(Tools.Instruction.setEnPassant, board.enPassant, null));
                board.enPassant = null;
            }
            board.movePiece(selectedPiece, end[0], end[1]);
            if (board.atEnd != null)
                currentState = promotionState;
            // If we're gonna promote, then do the promotion first
            if (isPromotionState()) {
                int indexeu = end[2];
                board.addUndoMove(new Move(Tools.Instruction.add, board.atEnd, null));
                Piece thePromoted = board.promote(Tools.promotionOrder[indexeu]);
                board.addUndoMove(new Move(Tools.Instruction.remove, thePromoted, null));
                currentState = mainState;
            }
            nextMove();

            return getBoardResult();
        }

        public synchronized void render() {
            render(board);
        }

        public void render(Board toRender){
            Tools.drawImage(Tools.getSprite(Tools.Sprite.board), 0, 0);
            for(Piece[] column : toRender.piecePositions)
                for(Piece piece : column) {
                    if (piece == null) continue;
                    if (piece != selectedPiece)
                        piece.render();
                    else
                        piece.renderSelected();
                }
            if (selectedPiece != null)
                for (Integer c : selectedPiece.canMove())
                    Tools.drawCircle(Tools.getX(c) * Main.CELL_SIZE + Main.CELL_SIZE / 2, Tools.getY(c) * Main.CELL_SIZE + Main.CELL_SIZE / 2, Main.CELL_SIZE / 4, 0, 255, 0, 255);
        }
    }

    /**
     * This is for the user, for when we're asking if he/she wants to promote
     */
    class PromotionState implements ClientState {
        public Tools.Result click(int x, int y) {
            // TODO give the ability to undo the promotion
            if ((y / Main.CELL_SIZE) == 1) {
                int indexeu = x / Main.CELL_SIZE - 2;
                if (indexeu >= 0 && indexeu < 4) {
                    board.addUndoMove(new Move(Tools.Instruction.add, board.atEnd, null));
                    Piece thePromoted = board.promote(Tools.promotionOrder[indexeu]);
                    board.addUndoMove(new Move(Tools.Instruction.remove, thePromoted, null));
                    nextMove();
                    currentState = mainState;
                }
            }
            return getBoardResult();
        }

        @Override
        public Tools.Result botClick(int[] start, int[] end) throws bot.InvalidSideException {
            throw new bot.InvalidSideException("The promotion should happen with the normal bot click :/");
        }

        public void render() {
            mainState.render();
            for (int i = 0; i < 4; ++i) {
                Tools.drawRect(Main.CELL_SIZE * (i + 2), Main.CELL_SIZE, Main.CELL_SIZE, Main.CELL_SIZE, 255, 0, 0, 255);
                Tools.drawImage(Tools.getSprite(board.atEnd.side, Tools.promotionOrder[i]), Main.CELL_SIZE * (i + 2), Main.CELL_SIZE);
            }
        }
    }

    interface ClientState {
        Tools.Result click(int x, int y);

        Tools.Result botClick(int[] start, int[] end);

        void render();
    }


    // INFORMATION METHODS

    public boolean isMainState() {
        return currentState == mainState;
    }

    public boolean isPromotionState() {
        return currentState == promotionState;
    }

    private Tools.Result getBoardResult() {
        return board.getBoardResult();
    }
}
