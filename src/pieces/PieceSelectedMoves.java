package pieces;

import java.util.ArrayList;

public class PieceSelectedMoves {
    public ArrayList<int[]> positions;
    public ArrayList<int[]> thingsToTake;
    public ChessPiece piece;

    public PieceSelectedMoves(ArrayList<int[]> positions, ArrayList<int[]> thingsToTake, ChessPiece piece) {
        this.positions = positions;
        this.thingsToTake = thingsToTake;
        this.piece = piece;
    }
}
