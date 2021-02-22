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
        for (Tools.Side side : new Tools.Side[]{Tools.Side.Black, Tools.Side.White}) {
            int pawnLayer = side == Tools.Side.Black ? 1 : 6;
            int backLayer = side == Tools.Side.Black ? 0 : 7;
            for (int i = 0; i < 8; ++i)
                board.addPiece(new Pawn(i, pawnLayer, side, board));
            board.addPiece(new Rook(0, backLayer, side, board));
            board.addPiece(new Rook(7, backLayer, side, board));
            board.addPiece(new Knight(1, backLayer, side, board));
            board.addPiece(new Knight(6, backLayer, side, board));
            board.addPiece(new Bishop(2, backLayer, side, board));
            board.addPiece(new Bishop(5, backLayer, side, board));
            board.addPiece(new Queen(3, backLayer, side, board));
        }
        board.addKing(Tools.Side.White, new King(4, 7, Tools.Side.White, board));
        board.addKing(Tools.Side.Black, new King(4, 0, Tools.Side.Black, board));
        board.currentMove = Tools.Side.White;

        mainState = new MainState();
        promotionState = new PromotionState();
        currentState = mainState;
    }


    // HANDLING CLICKS

    public Tools.Result click(int x, int y, Tools.Side currentSide) {
        if(board.currentMove == currentSide)
            return currentState.click(x, y);
        return null;
    }

    public Tools.Result botClick(int[] start, int[] end) {
        return this.currentState.botClick(start, end);
    }


    // SIMPLIFICATION TOOL METHODS

    /**
     * update the move so that it's a new move
     */
    private void nextMove() {
        board.currentMove = board.opposite();
        ++board.moveNum;
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
            cellClicked(x / Main.CELL_SIZE, y / Main.CELL_SIZE);
            return getBoardResult();
        }

        // For the AI to be able to click
        @Override
        public Tools.Result botClick(int[] start, int[] end) {
            Piece selectedPiece = board.getPiece(start[0], start[1]);
            if (board.enPassant != null && board.enPassant.side == board.currentMove)
                board.enPassant = null;
            board.movePiece(selectedPiece, end[0], end[1]);
            if (board.atEnd != null)
                currentState = promotionState;
            nextMove();
            // If we're gonna promote, then do the promotion first
            if (isPromotionState()) {
                int indexeu = end[2];
                board.promote(Tools.promotionOrder[indexeu]);
                currentState = mainState;
            }

            return getBoardResult();
        }

        public synchronized void render() {
            Tools.drawImage(Tools.getSprite(Tools.Sprite.board), 0, 0);
            for (Piece piece : board.pieces)
                if (piece != selectedPiece)
                    piece.render();
                else
                    piece.renderSelected();
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
            if ((y / Main.CELL_SIZE) == 1) {
                int indexeu = x / Main.CELL_SIZE - 2;
                if (indexeu >= 0 && indexeu < 4) {
                    board.promote(Tools.promotionOrder[indexeu]);
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


    // MAIN CELL CLICKED METHOD

    private void cellClicked(int x, int y) {
        if (x > 7 || x < 0 || y > 7 || y < 0)
            throw new InvalidBoardPositionException("engine.board had cell clicked at (" + x + " " + y + "); not valid!");

        // If selectedPiece != null, then we move the piece
        if (selectedPiece != null) {
            // TODO Fix the enPassant bug; when you double forward the pawn, ai moves, then you click the pawn and click out, the enPassant gets removed
            // TODO somehow make it so that when a piece stops being enPassant, that the undo makes it an enPassant again
            if (board.enPassant != null && board.enPassant.side == board.currentMove)
                board.enPassant = null;
            if (board.movePiece(selectedPiece, x, y) && board.atEnd == null){
                nextMove();
            }
            else
                selectedPiece = null;
            if (board.atEnd != null) {
                currentState = promotionState;
            }
        } else {
            // Get the piece at the cell, and select it
            Piece pieceAtCell = board.getPiece(x, y);
            if (pieceAtCell != null && pieceAtCell.side == board.currentMove)
                selectedPiece = board.getPiece(x, y);
        }
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
