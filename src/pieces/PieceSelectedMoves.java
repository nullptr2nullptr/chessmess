package pieces;

import java.util.HashSet;

public class PieceSelectedMoves {
    public HashSet<int[]> positions;
    public HashSet<int[]> thingsToTake;
    public ChessPiece p;
    public CheckState check;

    public enum CheckState {
        CHECK,
        MATE,
        SAFE,
    }

    public PieceSelectedMoves(HashSet<int[]> positions, HashSet<int[]> thingsToTake, ChessPiece p, CheckState c) {
        this.positions=positions;
        this.thingsToTake=thingsToTake;
        this.p=p;
        this.check=c;
    }
}
