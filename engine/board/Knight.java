package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Knight extends Piece{

	public Knight(int x, int y, Tools.Side side, Board board) {
		super(x, y, side, board);
	}


	// GETTING POSSIBLE MOVES

	@Override
	protected HashSet<Integer> possibleMoves() {
		HashSet<Integer> toreturn = new HashSet<>();
		for(int[] direction : new int[][]{{2, 1}, {2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {-2, 1}, {-2, -1}}){
			int cx = boardx + direction[0];
			int cy = boardy + direction[1];
			Piece thePiece = board.getPiece(cx, cy);
			if((thePiece == null || thePiece.side != this.side) && board.inBounds(cx, cy))
				toreturn.add(Tools.toNum(cx, cy));
		}
		return toreturn;
	}

	@Override
	protected HashSet<Integer> capturableSpaces() {
		return possibleMoves();
	}


	// PIECE SPECIFIC METHODS

	@Override
	public engine.Tools.Piece getPiece() {
		return Tools.Piece.knight;
	}

	@Override
	public Piece copy(){
		return new Knight(boardx, boardy, side, board);
	}

}
