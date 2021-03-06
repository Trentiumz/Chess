package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Bishop extends Piece{

	public Bishop(int x, int y, Tools.Side side, Board board) {
		super(x, y, side, board);
	}


	// GETTING WHERE IT CAN MOVE TO

	@Override
	public boolean doesBlock(int x, int y) {
		if(x == boardx && y == boardy)
			return true;
		// If it's not on a diagonal from this piece, this this is impossible
		if(Math.abs(x - boardx) != Math.abs(y - boardy))
			return false;
		King otherKing = board.getKing(Tools.opposite(side));
		// If the other king's on the same "side"(quadrant) as this piece, and if the piece is closer to this piece than the "other king"
		if(Math.signum(otherKing.boardx - boardx) == Math.signum(x - boardx) && Math.signum(otherKing.boardy - boardy) == Math.signum(y - boardy) && Math.abs(x - boardx) < Math.abs(otherKing.boardx - boardx))
			return true;
		else
			return false;
	}

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
		if(moveNumForSpaces != board.moveNum)
			capturableSpaces = possibleMoves();
		else
			System.out.println("wooo");
		return capturableSpaces;
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
		return (this.side == Tools.Side.White ? 1 : -1) * (30 + getRating());
	}
}
