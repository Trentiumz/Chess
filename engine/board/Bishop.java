package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Bishop extends Piece{

	public Bishop(int x, int y, Tools.Side side, Board board) {
		super(x, y, side, board);
	}


	// GETTING WHERE IT CAN MOVE TO

	@Override
	protected HashSet<Integer> possibleMoves() {
		HashSet<Integer> toreturn = new HashSet<>();
		for (int[] direction : new int[][]{{1, 1}, {-1, -1}, {1, -1}, {-1, 1}}) {
			for (int cx = boardx + direction[0], cy = boardy + direction[1]; 0 <= cx && cx <= 7 && cy <= 7 && 0 <= cy; cx += direction[0], cy += direction[1]) {
				Piece thePiece = board.getPiece(cx, cy);
				if (thePiece == null)
					toreturn.add(Tools.toNum(cx, cy));
				else if (thePiece.side == this.side)
					break;
				else {
					toreturn.add(Tools.toNum(cx, cy));
					break;
				}
			}
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
		return Tools.Piece.bishop;
	}

	@Override
	public Bishop copy(){
		return new Bishop(boardx, boardy, side, board);
	}

	@Override
	public float rating() {
		return 30 + getRating();
	}
}
