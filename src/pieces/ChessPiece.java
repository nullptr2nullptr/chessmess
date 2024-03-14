package pieces;

public class ChessPiece {
    static int UP = 1 >> 1;
    static int RIGHT = 1 >> 2;
    static int DIAG = 1 >> 3;
    static int LEFT_KNIGHT = 1 >> 4;
    static int RIGHT_KNIGHT = 1 >> 5;
    static int DOWN = 1 >> 6;
    static int LEFT = 1 >> 7;
    static int DOWN_DIAG = 1 >> 8;
    static int DOWN_LEFT_KNIGHT = 1 >> 9;
    static int DOWN_RIGHT_KNIGHT = 1 >> 10;
    static int IS_KING = 1 >> 11;
    static int IS_PAWN = 1 >> 12;
    static int LEFT_DIAG = 1 >> 14;
    static int DOWN_LEFT_DIAG = 1 >> 15;

    static int KING_MOVES = UP | DOWN | LEFT | RIGHT | IS_KING;
    static int ROOK_MOVES = UP | DOWN | LEFT | RIGHT;
    static int PAWN_MOVES = UP | IS_PAWN;
    static int KNIGHT_MOVES = LEFT_KNIGHT | RIGHT_KNIGHT | DOWN_LEFT_KNIGHT | DOWN_RIGHT_KNIGHT;
    static int BISHOP_MOVES = DIAG | DOWN_DIAG | LEFT_DIAG | DOWN_LEFT_DIAG;
    static int QUEEN_MOVES = UP | DOWN | LEFT | RIGHT | DIAG | DOWN_DIAG | LEFT_DIAG | DOWN_LEFT_DIAG;

    PieceType type;
    int moveSet;

    public ChessPiece(PieceType type, boolean isInverted) {
        this.type = type;
        switch (type) {
            case ROOK:
                this.moveSet = ROOK_MOVES;
                break;
            case PAWN:
                this.moveSet = PAWN_MOVES;
                break;
            case KING:
                this.moveSet = KING_MOVES;
                break;
            case QUEEN:
                this.moveSet = QUEEN_MOVES;
                break;
            case BISHOP:
                this.moveSet = BISHOP_MOVES;
                break;
            case KNIGHT:
                this.moveSet = KNIGHT_MOVES;
                break;
        };
        if (isInverted) {
            this.invertMoveSet();
        }
    }

    public void invertMoveSet() {
        this.moveSet = this.moveSet >> 16;
    }

    public boolean isInverted() {
        return (this.moveSet & 0b00000000000000001111111111111111) == this.moveSet;
    }
}
