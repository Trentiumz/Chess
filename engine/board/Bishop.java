package engine.board;

import engine.Tools;

import java.util.HashSet;

public class Bishop extends Piece{

	public Bishop(int x, int y, Tools.Side side, Board board) {
		super(x, y, side, board);
	}

	public Bishop(Bishop other){
		super(other);
	}


	// GETTING WHERE IT CAN MOVE TO

	@Override
	public boolean otherKingInRange() {
		King otherKing = board.getKing(Tools.opposite(side));
		inPath.clear();
		if(Math.abs(otherKing.boardx - boardx) == Math.abs(otherKing.boardy - boardy)){
			// Get all pieces in between this and the other king
			int xDiff = (int) Math.signum(otherKing.boardx - boardx);
			int yDiff = (int) Math.signum(otherKing.boardy - boardy);
			for(int x = boardx + xDiff, y = boardy + yDiff; board.getPiece(x, y) != otherKing; x += xDiff, y += yDiff){
				if(board.getPiece(x, y) != null)
					inPath.add(board.getPiece(x, y));
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean willCheck(Piece toMove, int newX, int newY) {
		// will check if nothing's in the path and it's not being blocked; or if there's one thing in the path but it moves away; or if the piece captures this piece
		if(newX == boardx && newY == boardy)
			return false;
		return (inPath.size() == 0 || inPath.size() == 1 && toMove == inPath.get(0)) && !doesBlock(newX, newY);
	}

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
		return new Bishop(this);
	}

	@Override
	public float rating() {
		return (this.side == Tools.Side.White ? 1 : -1) * (30 + getRating());
	}
}
