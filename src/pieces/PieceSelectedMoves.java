package pieces;

import java.util.HashSet;

public class PieceSelectedMoves {
    public HashSet<int[]> positions;
    public HashSet<int[]> thingsToTake;
    public ChessPiece piece;
    public boolean isMate, isPinned, isCheck;
    public String msg;
 
    public PieceSelectedMoves(HashSet<int[]> positions, HashSet<int[]> thingsToTake, ChessPiece piece, boolean isCheck, boolean isMate, String message, boolean isPinned) {
        this.positions=positions;
        this.thingsToTake=thingsToTake;
        this.piece=piece;
        this.msg = message;
        this.isMate = isMate;
        this.isCheck = isCheck;
        this.isPinned = isPinned;
    }
}
