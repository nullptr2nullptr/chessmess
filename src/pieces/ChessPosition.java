package pieces;

public class ChessPosition {
    public int x,y;
    // Top left is (0,0)
    public ChessPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return x + "," + y;
    }
}
