package pieces;

import java.util.ArrayList;

public class PieceSelectedMoves {
    ArrayList<int[]> positions;
    ArrayList<int[]> thingsToTake;

    public PieceSelectedMoves(ArrayList<int[]> positions, ArrayList<int[]> thingsToTake) {
        this.positions=positions;
        this.thingsToTake=thingsToTake;
    }
}
