package pieces;

import java.util.ArrayList;

public class PieceSelectedMoves {
    public ArrayList<int[]> positions;
    public ArrayList<int[]> thingsToTake;
    public ChessPiece p;

    public PieceSelectedMoves(ArrayList<int[]> positions, ArrayList<int[]> thingsToTake, ChessPiece p) {
        this.positions=positions;
        this.thingsToTake=thingsToTake;
        this.p=p;
    }
}
