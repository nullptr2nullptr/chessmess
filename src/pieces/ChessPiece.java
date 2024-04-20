package pieces;

/*
 * This code is very complicated. Do not _ever_ touch it - it will break. It is a fragile eggshell...
 */

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.swing.ImageIcon;

import game.GameBoard;

import java.awt.Graphics;
import java.awt.Color;

import static game.GameBoard.offset;

public class ChessPiece implements Cloneable {
    // Constants representing different move directions and types of chess pieces
    static int BASE = 0b1000000000000000000000000000000;
    static int UP = BASE;
    static int RIGHT = BASE >> 1;
    static int DIAG = BASE >> 2;
    static int LEFT_KNIGHT = BASE >> 3;
    static int RIGHT_KNIGHT = BASE >> 4;
    static int DOWN = BASE >> 5;
    static int LEFT = BASE >> 6;
    static int DOWN_DIAG = BASE >> 7;
    static int DOWN_LEFT_KNIGHT = BASE >> 8;
    static int DOWN_RIGHT_KNIGHT = BASE >> 9;
    static int IS_ONE = BASE >> 10;
    static int IS_KNIGHT_UP = BASE >> 11;
    static int LEFT_DIAG = BASE >> 12;
    static int DOWN_LEFT_DIAG = BASE >> 13;

    static int UP_INV = BASE >> 16;
    static int RIGHT_INV = BASE >> 1 >> 16;
    static int DIAG_INV = BASE >> 2 >> 16;
    static int LEFT_KNIGHT_INV = BASE >> 3 >> 16;
    static int RIGHT_KNIGHT_INV = BASE >> 4 >> 16;
    static int DOWN_INV = BASE >> 5 >> 16;
    static int LEFT_INV = BASE >> 6 >> 16;
    static int DOWN_DIAG_INV = BASE >> 7 >> 16;
    static int DOWN_LEFT_KNIGHT_INV = BASE >> 8 >> 16;
    static int DOWN_RIGHT_KNIGHT_INV = BASE >> 9 >> 16;
    static int IS_ONE_INV = BASE >> 10 >> 16;
    static int IS_KNIGHT_UP_INV = BASE >> 11 >> 16;
    static int LEFT_DIAG_INV = BASE >> 12 >> 16;
    static int DOWN_LEFT_DIAG_INV = BASE >> 13 >> 16;

    static int QUEEN_MOVES = UP | DOWN | LEFT | RIGHT | DIAG | DOWN_DIAG | LEFT_DIAG | DOWN_LEFT_DIAG;
    static int KING_MOVES = QUEEN_MOVES | IS_ONE;
    static int ROOK_MOVES = UP | DOWN | LEFT | RIGHT;
    static int PAWN_MOVES = UP | IS_ONE;
    static int KNIGHT_MOVES = LEFT_KNIGHT | RIGHT_KNIGHT | DOWN_LEFT_KNIGHT | DOWN_RIGHT_KNIGHT | IS_KNIGHT_UP;
    static int BISHOP_MOVES = DIAG | DOWN_DIAG | LEFT_DIAG | DOWN_LEFT_DIAG;

    PieceType type;
    int moveSet;
    ImageIcon icon;
    int width, height;
    public ChessPosition pos;
    double v_x = 0, v_y = 0, a_x = 0, a_y = .0005;
    boolean isDrawingDots;
    boolean isInverted;
    public int moveCount;
    boolean isKing;

    public ChessPiece(PieceType type, boolean isInverted, String iconPath, ChessPosition pos, int width, int height) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        this.type = type;
        this.icon = new ImageIcon(iconPath);
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.isDrawingDots = false;
        this.isInverted = isInverted;
        this.isKing = false;

        // Move sets
        switch (type) {
            case ROOK:
                this.moveSet = ROOK_MOVES;
                break;
            case PAWN:
                this.moveSet = PAWN_MOVES;
                break;
            case KING:
                this.moveSet = KING_MOVES;
                this.isKing = true;
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
        // Move inversion check
        if (isInverted) {
            this.invertMoveSet();
        }
    }

    public PieceType getType() {
        return this.type;
    }

    // Logic for random moves
    public void setRandomMoveSet() {
        int[] moveSets = {KNIGHT_MOVES, PAWN_MOVES, QUEEN_MOVES, KING_MOVES, BISHOP_MOVES, ROOK_MOVES};
        int randomIndex = (int) (Math.random() * moveSets.length);
        this.moveSet = moveSets[randomIndex];
        System.out.println("DEBUG: new move set is " + randomIndex);
        if (isInverted) {
            this.invertMoveSet();
        }
    }

    // Pawn promotion to Queen
    public void tryPromoteToQueen() {
        if (this.isPromotable()) {
            this.moveSet = QUEEN_MOVES;
            if (isInverted) {
                this.icon = new ImageIcon("src/res/image/Chess_qdt60.png");
                this.invertMoveSet();
            } else {
                this.icon = new ImageIcon("src/res/image/Chess_qlt60.png");
            }
        }
    }

    public boolean isPromotable() {
        if (this.type == PieceType.PAWN) {
            if (((pos.y == 0) && (!this.isInverted)) || ((pos.y == 7) && (this.isInverted))) {
            return true;
            }
        } 
        return false;
        // return moveSet == PAWN_MOVES || moveSet == PAWN_MOVES >> 16;
    }

    public void invertMoveSet() {
        this.moveSet = this.moveSet >> 16;
    }

    public boolean isInverted() {
        return this.isInverted; // (this.moveSet & 0b00000000000000001111111111111111) == this.moveSet;
    }

    public void paint(Graphics g, GameBoard p)  {
        g.drawImage(this.icon.getImage(), pos.x * GameBoard.PIECE_LENGTH, pos.y * GameBoard.PIECE_LENGTH + offset, width, height, p);
    }

    // The GLORIOUS FUNCTION
    public PieceSelectedMoves calculateMoveset(HashMap<ChessPosition, Color> colors, ChessPiece[][] pieces, boolean scouting) throws CloneNotSupportedException{
        ArrayList<int[]> positions = new ArrayList<>();
        ArrayList<int[]> thingsToTake = new ArrayList<>();
        Negator negate;
        IsDone doner;
        IsDone doner_opp;
        if (!isInverted() && isDrawingDots) {
            negate = () -> {
                return -1;
            };
            doner = (x) -> {
                return x >= 0;
            };
            doner_opp = (x) -> {
                return x < 8;
            };
        } else if (isInverted() && isDrawingDots) {
            negate = () -> {
                return 1;
            };
            doner = (x) -> {
                return x < 8;
            };
            doner_opp = (x) -> {
                return x >= 0;
            };
        } else {
            // IMPOSSIBLE
            return null;
        }

        if (this.moveSet == PAWN_MOVES || this.moveSet == PAWN_MOVES >> 16) {
            // Move set positions
            if (this.moveSet == PAWN_MOVES && this.moveCount == 0) {
                positions.add(new int[]{pos.x, pos.y - 1});
                if (pieces[pos.y-1][pos.x] == null) { // Don't jump over
                    positions.add(new int[]{pos.x, pos.y - 2});
                }
            }
            else if (this.moveSet == PAWN_MOVES >> 16 && this.moveCount == 0) {
                positions.add(new int[]{pos.x, pos.y + 1});
                if (pieces[pos.y+1][pos.x] == null) { // Don't jump over
                    positions.add(new int[]{pos.x, pos.y + 2});
                }
            }
            else if (this.moveSet == PAWN_MOVES && this.moveCount != 0) {
                if (pos.y > 0 && pieces[pos.y-1][pos.x] == null) {
                    positions.add(new int[]{pos.x, pos.y - 1});
                }
            }
            else if (this.moveSet == PAWN_MOVES >> 16 && this.moveCount != 0) {
                if (pos.y+1 < 8 && pos.x+1 < 8 && pieces[pos.y+1][pos.x+1] == null) {
                    positions.add(new int[]{pos.x, pos.y + 1});
                }
            }
            if (this.moveSet == PAWN_MOVES) {
                if (pos.x > 0) {
                    if (pieces[pos.y-1][pos.x-1] != null && pieces[pos.y-1][pos.x-1].isInverted() != this.isInverted()) {
                        thingsToTake.add(new int[]{pos.x - 1, pos.y - 1});
                    }
                }
                if (pos.x < 7) {
                    if (pieces[pos.y-1][pos.x+1] != null && pieces[pos.y-1][pos.x+1].isInverted() != this.isInverted()) {
                        thingsToTake.add(new int[]{pos.x + 1, pos.y - 1});
                    }
                }
            }
            else if (this.moveSet == PAWN_MOVES >> 16) {
                if (pos.x > 0) {
                    if (pieces[pos.y+1][pos.x-1] != null && pieces[pos.y+1][pos.x-1].isInverted() != this.isInverted()) {
                        thingsToTake.add(new int[]{pos.x - 1, pos.y + 1});
                    }
                }
                if (pos.x < 7) {
                    if (pieces[pos.y+1][pos.x+1] != null && pieces[pos.y+1][pos.x+1].isInverted() != this.isInverted()) {
                        thingsToTake.add(new int[]{pos.x + 1, pos.y + 1});
                    }
                }
            }
        }
        else {
            if (!isInverted()) {
                if ((this.moveSet & LEFT_KNIGHT) != 0) {
                    positions.add(new int[]{pos.x-2,pos.y-1});
                }
                if ((this.moveSet & RIGHT_KNIGHT) != 0) {
                    positions.add(new int[]{pos.x+2,pos.y-1});
                }
                if ((this.moveSet & DOWN_LEFT_KNIGHT) != 0) {
                    positions.add(new int[]{pos.x-2,pos.y+1});
                }
                if ((this.moveSet & DOWN_RIGHT_KNIGHT) != 0) {
                    positions.add(new int[]{pos.x+2,pos.y+1});
                }
                if ((this.moveSet & LEFT_KNIGHT) != 0 && (this.moveSet & IS_KNIGHT_UP) != 0) {
                    positions.add(new int[]{pos.x-1,pos.y-2});
                }
                if ((this.moveSet & RIGHT_KNIGHT) != 0 && (this.moveSet & IS_KNIGHT_UP) != 0) {
                    positions.add(new int[]{pos.x+1,pos.y-2});
                }
                if ((this.moveSet & DOWN_LEFT_KNIGHT) != 0 && (this.moveSet & IS_KNIGHT_UP) != 0) {
                    positions.add(new int[]{pos.x-1,pos.y+2});
                }
                if ((this.moveSet & DOWN_RIGHT_KNIGHT) != 0 && (this.moveSet & IS_KNIGHT_UP) != 0) {
                    positions.add(new int[]{pos.x+1,pos.y+2});
                }
            } else {
                if ((this.moveSet & LEFT_KNIGHT_INV) != 0) {
                    positions.add(new int[]{pos.x+2,pos.y+1});
                }
                if ((this.moveSet & RIGHT_KNIGHT_INV) != 0) {
                    positions.add(new int[]{pos.x-2,pos.y+1});
                }
                if ((this.moveSet & DOWN_LEFT_KNIGHT_INV) != 0) {
                    positions.add(new int[]{pos.x+2,pos.y-1});
                }
                if ((this.moveSet & DOWN_RIGHT_KNIGHT_INV) != 0) {
                    positions.add(new int[]{pos.x-2,pos.y-1});
                }
                if ((this.moveSet & LEFT_KNIGHT_INV) != 0 && (this.moveSet & IS_KNIGHT_UP_INV) != 0) {
                    positions.add(new int[]{pos.x+1,pos.y+2});
                }
                if ((this.moveSet & RIGHT_KNIGHT_INV) != 0 && (this.moveSet & IS_KNIGHT_UP_INV) != 0) {
                    positions.add(new int[]{pos.x-1,pos.y+2});
                }
                if ((this.moveSet & DOWN_LEFT_KNIGHT_INV) != 0 && (this.moveSet & IS_KNIGHT_UP_INV) != 0) {
                    positions.add(new int[]{pos.x+1,pos.y-2});
                }
                if ((this.moveSet & DOWN_RIGHT_KNIGHT_INV) != 0 && (this.moveSet & IS_KNIGHT_UP_INV) != 0) {
                    positions.add(new int[]{pos.x-1,pos.y-2});
                }
            }
            
            if (((this.moveSet & IS_ONE) != 0 && !isInverted()) || ((this.moveSet & IS_ONE_INV) != 0 && isInverted())) {
                if (((this.moveSet & UP) != 0 && !isInverted()) || ((this.moveSet & UP_INV) != 0 && isInverted())) {
                    if (!(pos.y + negate.negate() < 0 || pos.y + negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x, pos.y + negate.negate()});
                    }
                }
                if (((this.moveSet & DOWN) != 0 && !isInverted()) || ((this.moveSet & DOWN_INV) != 0 && isInverted())) {
                    if (!(pos.y + -negate.negate() < 0 || pos.y + -negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x, pos.y + -negate.negate()});
                    }
                }
                if (((this.moveSet & RIGHT) != 0 && !isInverted()) || ((this.moveSet & RIGHT_INV) != 0 && isInverted())) {
                    if (!(pos.x + -negate.negate() < 0 || pos.x + -negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x + -negate.negate(), pos.y});
                    }
                }
                if (((this.moveSet & LEFT) != 0 && !isInverted()) || ((this.moveSet & LEFT_INV) != 0 && isInverted())) {
                    if (!(pos.x + negate.negate() < 0 || pos.x + negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x + negate.negate(), pos.y});
                    }
                }
                if (((this.moveSet & DIAG) != 0 && !isInverted()) || ((this.moveSet & DIAG_INV) != 0 && isInverted())) {
                    if (!(pos.x + -negate.negate() < 0 || pos.x + -negate.negate() >= 8 || pos.y + negate.negate() < 0 || pos.y + negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x + -negate.negate(), pos.y + negate.negate()});
                    }
                }
                if (((this.moveSet & LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & LEFT_DIAG_INV) != 0 && isInverted())) {
                    positions.add(new int[]{pos.x + negate.negate(), pos.y + negate.negate()});
                    if (!(pos.x + negate.negate() < 0 || pos.x + negate.negate() >= 8 || pos.y + negate.negate() < 0 || pos.y + negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x + negate.negate(), pos.y + negate.negate()});
                    }
                }
                if (((this.moveSet & DOWN_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_DIAG_INV) != 0 && isInverted())) {
                    if (!(pos.x + -negate.negate() < 0 || pos.x + -negate.negate() >= 8 || pos.y + -negate.negate() < 0 || pos.y + -negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x + -negate.negate(), pos.y + -negate.negate()});
                    }
                }
                if (((this.moveSet & DOWN_LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_LEFT_DIAG_INV) != 0 && isInverted())) {
                    if (!(pos.x + negate.negate() < 0 || pos.x + negate.negate() >= 8 || pos.y + -negate.negate() < 0 || pos.y + -negate.negate() >= 8)) {
                        positions.add(new int[]{pos.x + negate.negate(), pos.y + -negate.negate()});
                    }
                }
            } else {
                if (((this.moveSet & UP) != 0 && !isInverted()) || ((this.moveSet & UP_INV) != 0 && isInverted())) {
                    for (int row = pos.y + negate.negate(); doner.isDone(row); row = row + negate.negate()) {
                        if (row < 0 || row >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[row][pos.x];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{pos.x, row});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{pos.x, row});
                    }
                }
                if (((this.moveSet & DOWN) != 0 && !isInverted()) || ((this.moveSet & DOWN_INV) != 0 && isInverted())) {
                    for (int row = pos.y + -negate.negate(); doner_opp.isDone(row); row = row + -negate.negate()) {
                        if (row < 0 || row >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[row][pos.x];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{pos.x, row});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{pos.x, row});
                    }
                }
                if (((this.moveSet & RIGHT) != 0 && !isInverted()) || ((this.moveSet & RIGHT_INV) != 0 && isInverted())) {
                    for (int col = pos.x + -negate.negate(); doner_opp.isDone(col); col = col + -negate.negate()) {
                        if (col < 0 || col >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[pos.y][col];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{col, pos.y});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{col, pos.y});
                    }
                }
                if (((this.moveSet & LEFT) != 0 && !isInverted()) || ((this.moveSet & LEFT_INV) != 0 && isInverted())) {
                    for (int col = pos.x + negate.negate(); doner.isDone(col); col = col + negate.negate()) {
                        if (col < 0 || col >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[pos.y][col];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{col, pos.y});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{col, pos.y});
                    }
                }
                if (((this.moveSet & DIAG) != 0 && !isInverted()) || ((this.moveSet & DIAG_INV) != 0 && isInverted())) {
                    int row = pos.y + negate.negate();
                    for (int col = pos.x + -negate.negate(); doner_opp.isDone(col); col = col + -negate.negate()) {
                        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[row][col];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{col,row});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{col,row});
                        row = row + negate.negate();
                    }
                }
                if (((this.moveSet & LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & LEFT_DIAG_INV) != 0 && isInverted())) {
                    int row = pos.y + negate.negate();
                    for (int col = pos.x + negate.negate(); doner.isDone(col); col = col + negate.negate()) {
                        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[row][col];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{col,row});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{col,row});
                        row = row + negate.negate();
                    }
                }
                if (((this.moveSet & DOWN_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_DIAG_INV) != 0 && isInverted())) {
                    int row = pos.y - negate.negate();
                    for (int col = pos.x + -negate.negate(); doner_opp.isDone(col); col = col + -negate.negate()) {
                        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[row][col];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{col,row});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{col,row});
                        row = row - negate.negate();
                    }
                }
                if (((this.moveSet & DOWN_LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_LEFT_DIAG_INV) != 0 && isInverted())) {
                    int row = pos.y - negate.negate();
                    for (int col = pos.x + negate.negate(); doner.isDone(col); col = col + negate.negate()) {
                        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
                            continue;
                        }
                        ChessPiece p = pieces[row][col];
                        if (p != null && p.isInverted() != this.isInverted()) {
                            thingsToTake.add(new int[]{col,row});
                            break;
                        }
                        if (p != null && p.isInverted() == this.isInverted()) {
                            break;
                        }
                        positions.add(new int[]{col,row});
                        row = row - negate.negate();
                    }
                }
            }
        }
        HashSet<int[]> new_positions = new HashSet<>();
        HashSet<int[]> new_thingsToTake = new HashSet<>();
        for (int[] xy: positions) {
            if (xy[1] >= 8 || xy[1] < 0 || xy[0] >= 8 || xy[0] < 0) {
                continue;
            }
            if (pieces[xy[1]][xy[0]] != null && pieces[xy[1]][xy[0]].isInverted() != this.isInverted() && !(this.moveSet == PAWN_MOVES || this.moveSet == PAWN_MOVES >> 16)) {
                new_thingsToTake.add(xy);
                continue;
            } else if (pieces[xy[1]][xy[0]] == null) {
                new_positions.add(xy);
            }
        }
        for (int[] xy: thingsToTake) {
            if (xy[1] >= 8 || xy[1] < 0 || xy[0] >= 8 || xy[0] < 0) {
                continue;
            }
            else {
                new_thingsToTake.add(xy);
            }
        }

        for (int[] xy: new_positions) {
            boolean skip = false;
            for (ChessPiece[] row: pieces) {
                for (ChessPiece p: row) {
                    if (p==null) {
                        continue;
                    }
                    if ((p.pos.x == xy[0] && p.pos.y == xy[1]) && p.isInverted() == this.isInverted() && p != this) {
                        skip=true;
                        break;
                    }
                }
                if (skip) {
                    break;
                }
            }
            if (!skip) {
                colors.put(new ChessPosition(xy[0], xy[1]), new Color(175, 215, 250));
            }
        }

        for (int[] xy: new_thingsToTake) {
            // Taking pieces
            boolean skip = false;
            for (ChessPiece[] row: pieces) {
                for (ChessPiece p: row) {
                    if (p==null) {
                        continue;
                    }
                    if ((p.pos.x == xy[0] && p.pos.y == xy[1]) && p.isInverted() == this.isInverted() && p != this) {
                        skip=true;
                        break;
                    }
                }
                if (skip) {
                    break;
                }
            }
            if (!skip) {
                colors.put(new ChessPosition(xy[0], xy[1]), new Color(240, 155, 129));
            }
        }

        boolean finalInCheck = false;
        boolean finalInCheckmate = false;
        boolean finalPinned = false;
        String message = null;
        if (!scouting) {
            // If WE are a king, then check if we are in check/checkmate.
            if (isKing) {
                // Calculate the attackers: the pieces whose TTT == us
                ArrayList<ChessPiece> attackers = new ArrayList<>();
                for (ChessPiece[] row: pieces) {
                    for (ChessPiece piece: row) {
                        if (piece == null) {
                            continue;
                        }
                        if (!piece.isKing && piece.isInverted() != this.isInverted()) {
                            piece.isDrawingDots = true;
                            PieceSelectedMoves moves = piece.calculateMoveset(new HashMap<>(), pieces, true);
                            piece.isDrawingDots = false;
                            if (moves == null) {
                                continue;
                            }
                            for (int[] pos: moves.thingsToTake) {
                                if (pos[0] == this.pos.x && pos[1] == this.pos.y) {
                                    attackers.add(piece);
                                }
                            }
                        }
                    }
                }
                
                HashSet<int[]> final_positions = new HashSet<>();
                HashSet<int[]> final_takings = new HashSet<>();
                colors.clear();
                // Update our possible positions based on their attacking spots (if their positions == us)
                for (int[] test_pos: new_positions) {
                    for (ChessPiece attacker: attackers) {
                        attacker.isDrawingDots = true;
                        PieceSelectedMoves moves = attacker.calculateMoveset(new HashMap<>(), pieces, true);
                        attacker.isDrawingDots = false;
                        boolean being_attacked = false;
                        for (int[] pos: moves.positions) {
                            if (pos[0] == test_pos[0] && pos[1] == test_pos[1]) {
                                being_attacked = true;
                            }
                        }
                        if (!being_attacked) {
                            colors.put(new ChessPosition(test_pos[0], test_pos[1]), new Color(175, 215, 250));
                            final_positions.add(test_pos);
                        }
                    }
                }
                // Update our possible positions or where we can take based on their attacking/defended spots
                /// (if their TTT = us, after we take one of the pieces we can)
                for (int[] test_pos: new_thingsToTake) {
                    for (ChessPiece attacker: attackers) {
                        ChessPiece[][] pieces_copy = new ChessPiece[8][8];
                        for (int r=0; r<8; r++){
                            for (int c=0; c<8; c++) {
                                if (pieces[r][c] == null) {
                                    continue;
                                }
                                pieces_copy[r][c] = (ChessPiece)(pieces[r][c].clone());
                            }
                        }
                        pieces_copy[test_pos[1]][test_pos[0]] = pieces_copy[this.pos.y][this.pos.x];
                        pieces_copy[test_pos[1]][test_pos[0]].pos = new ChessPosition(test_pos[0], test_pos[1]);
                        
                        // Would taking that keep us under attack?
                        attacker.isDrawingDots = true;
                        PieceSelectedMoves moves = attacker.calculateMoveset(new HashMap<>(), pieces_copy, true);
                        attacker.isDrawingDots = false;
                        boolean being_attacked = false;
                        for (int[] pos: moves.thingsToTake) {
                            if (pos[0] == test_pos[0] && pos[1] == test_pos[1]) {
                                being_attacked = true;
                            }
                        }
                        // Would taking that bring us under attack by anything else?
                        for (int r=0; r<8; r++){
                            for (int c=0; c<8; c++) {
                                // Only care about opponent's pieces
                                if (pieces_copy[r][c] == null || pieces_copy[r][c].isInverted() == this.isInverted() || pieces_copy[r][c] == attacker) {
                                    continue;
                                }
                                pieces_copy[r][c].isDrawingDots = true;
                                PieceSelectedMoves m = pieces_copy[r][c].calculateMoveset(new HashMap<>(), pieces_copy, true);
                                pieces_copy[r][c].isDrawingDots = false;
                                for (int[] pos: m.thingsToTake) {
                                    if (pos[0] == test_pos[0] && pos[1] == test_pos[1]) {
                                        being_attacked = true;
                                    }
                                }
                            }
                        }
                        if (!being_attacked) {
                            colors.put(new ChessPosition(test_pos[0], test_pos[1]), new Color(240, 155, 129));
                            final_takings.add(test_pos);
                        }
                    }
                }
                if (!attackers.isEmpty()) {
                    finalInCheck = true;
                    message = "You are in check.";
                }
                // Make sure we're in checkmate
                if (!attackers.isEmpty() && final_positions.isEmpty() && final_takings.isEmpty()) {
                    boolean inCheckmate = true;
                    for (ChessPiece[] row: pieces) {
                        for (ChessPiece piece: row) {
                            if (!inCheckmate) {
                                break;
                            }
                            // Only care about our pieces
                            if (piece == null || piece.isInverted() != this.isInverted() || piece == this) {
                                continue;
                            }

                            piece.isDrawingDots = true;
                            PieceSelectedMoves moves = piece.calculateMoveset(new HashMap<>(), pieces, true);
                            piece.isDrawingDots = false;
                            if (moves == null) {
                                continue;
                            }
                            for (int[] pos: moves.positions) {
                                ChessPiece[][] pieces_copy = new ChessPiece[8][8];
                                for (int r=0; r<8; r++){
                                    for (int c=0; c<8; c++) {
                                        if (pieces[r][c] == null) {
                                            continue;
                                        }
                                        pieces_copy[r][c] = (ChessPiece)(pieces[r][c].clone());
                                    }
                                }
                                // Move one of our pieces to that spot
                                pieces_copy[pos[1]][pos[0]] = pieces_copy[piece.pos.y][piece.pos.x];
                                pieces_copy[pos[1]][pos[0]].pos = new ChessPosition(pos[0], pos[1]);
                                ArrayList<ChessPiece> still_attacking = new ArrayList<>();
                                // Check if they are still attacking us
                                for (ChessPiece[] r: pieces_copy) {
                                    for (ChessPiece p: r) {
                                        if (p == null) {
                                            continue;
                                        }
                                        if (!p.isKing && p.isInverted() != this.isInverted()) {
                                            p.isDrawingDots = true;
                                            PieceSelectedMoves still_attackers = p.calculateMoveset(new HashMap<>(), pieces_copy, true);
                                            p.isDrawingDots = false;
                                            if (still_attackers == null) {
                                                continue;
                                            }
                                            for (int[] pos2: still_attackers.thingsToTake) {
                                                if (pos2[0] == this.pos.x && pos2[1] == this.pos.y) {
                                                    still_attacking.add(p);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (still_attacking.isEmpty()) {
                                    inCheckmate = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (inCheckmate) {
                        finalInCheckmate = true;
                        message = "You are in checkmate";
                    }
                }
                new_positions = final_positions;
                new_thingsToTake = final_takings;
            } else { // We are not a king, so check if we are pinned to king, or can take that piece, or need to block
                ChessPiece ourKing = null;
                
                // Find our king
                for (ChessPiece[] row: pieces) {
                    for (ChessPiece piece: row) {
                        // Only care about opp. pieces, not king = optimization
                        if (piece == null || piece.isInverted() != this.isInverted() || !piece.isKing) {
                            continue;
                        }
                        ourKing = piece;
                    }
                }

                // Check if we are in check   
                ArrayList<ChessPiece> attackers_baseline = new ArrayList<>();        
                for (ChessPiece[] row: pieces) {
                    for (ChessPiece piece: row) {
                        if (piece == null) {
                            continue;
                        }
                        if (!piece.isKing && piece.isInverted() != this.isInverted()) {
                            piece.isDrawingDots = true;
                            PieceSelectedMoves moves = piece.calculateMoveset(new HashMap<>(), pieces, true);
                            piece.isDrawingDots = false;
                            for (int[] pos: moves.thingsToTake) {
                                if (pos[0] == ourKing.pos.x && pos[1] == ourKing.pos.y) {
                                    attackers_baseline.add(piece);
                                }
                            }
                        }
                    }
                }
                if (!attackers_baseline.isEmpty()) { // see if we can block the check
                    boolean blocked_it = false;
                    for (int[] p: new_positions) {
                        ChessPiece[][] pieces_copy = new ChessPiece[8][8];
                        for (int r=0; r<8; r++){
                            for (int c=0; c<8; c++) {
                                if (pieces[r][c] == null) {
                                    continue;
                                }
                                pieces_copy[r][c] = (ChessPiece)(pieces[r][c].clone());
                            }
                        }
                        // Move "ourselves" there
                        pieces_copy[p[1]][p[0]] = pieces_copy[this.pos.y][this.pos.x];
                        pieces_copy[p[1]][p[0]].pos = new ChessPosition(p[0], p[1]);
                        // Are there any more attackers?
                        ArrayList<ChessPiece> attackers = new ArrayList<>(); 
                        for (ChessPiece[] row: pieces_copy) {
                            for (ChessPiece piece: row) {
                                if (piece == null) {
                                    continue;
                                }
                                if (!piece.isKing && piece.isInverted() != this.isInverted()) {
                                    piece.isDrawingDots = true;
                                    PieceSelectedMoves moves = piece.calculateMoveset(new HashMap<>(), pieces_copy, true);
                                    piece.isDrawingDots = false;
                                    for (int[] pos: moves.thingsToTake) {
                                        if (pos[0] == ourKing.pos.x && pos[1] == ourKing.pos.y) {
                                            attackers.add(piece);
                                        }
                                    }
                                }
                            }
                        }
                        if (attackers.isEmpty()) {
                            new_positions.clear();
                            new_thingsToTake.clear();
                            colors.clear();
                            blocked_it=true;
                            colors.put(new ChessPosition(p[0], p[1]), new Color(175, 215, 250));
                            new_positions.add(p);
                            break;
                        }               
                    }

                    // Check if we can take the attacker
                    ///// just like below START
                    ArrayList<ChessPiece> attackers = new ArrayList<>();
                    ChessPiece[][] pieces_copy = new ChessPiece[8][8];
                    for (int r=0; r<8; r++){
                        for (int c=0; c<8; c++) {
                            if (pieces[r][c] == null) {
                                continue;
                            }
                            pieces_copy[r][c] = (ChessPiece)(pieces[r][c].clone());
                        }
                    }
        
                    // Eliminate ourselves
                    pieces_copy[this.pos.y][this.pos.x] = null;
        
                    // Check for pieces who would be attacking
                    for (ChessPiece[] row: pieces_copy) {
                        for (ChessPiece piece: row) {
                            if (piece == null) {
                                continue;
                            }
                            if (!piece.isKing && piece.isInverted() != this.isInverted()) {
                                piece.isDrawingDots = true;
                                PieceSelectedMoves moves = piece.calculateMoveset(new HashMap<>(), pieces_copy, true);
                                piece.isDrawingDots = false;
                                for (int[] pos: moves.thingsToTake) {
                                    if (pos[0] == ourKing.pos.x && pos[1] == ourKing.pos.y) {
                                        attackers.add(piece);
                                    }
                                }
                            }
                        }
                    }
                    // Check if we can take the attacker
                    PieceSelectedMoves moves = calculateMoveset(new HashMap<>(), pieces, true);
                    HashSet<int[]> ttt = moves.thingsToTake;
                    HashSet<int[]> final_ttt = new HashSet<>();
                    HashMap<ChessPosition, Color> final_colors = new HashMap<>();
                    for (int[] pos: ttt) {
                        for (ChessPiece attacker: attackers) {
                            if (attacker.pos.x == pos[0] && attacker.pos.y == pos[1]) {
                                final_ttt.add(pos);
                                final_colors.put(attacker.pos, new Color(240, 155, 129));
                            }
                        }
                    }
                    ///// just like below END

                    if (!final_ttt.isEmpty()) {
                        new_positions.clear();
                        new_thingsToTake.clear();
                        new_thingsToTake.addAll(final_ttt);
                        colors.clear();
                        colors.putAll(final_colors);
                    }
                    else if (!blocked_it) {
                        new_positions.clear();
                        new_thingsToTake.clear();
                        colors.clear();
                        blocked_it=true;
                        finalInCheck = true;
                        message = "You are in check.";
                    }
                }
                else { // see if we are pinned
                    ArrayList<ChessPiece> attackers = new ArrayList<>();
                    ChessPiece[][] pieces_copy = new ChessPiece[8][8];
                    for (int r=0; r<8; r++){
                        for (int c=0; c<8; c++) {
                            if (pieces[r][c] == null) {
                                continue;
                            }
                            pieces_copy[r][c] = (ChessPiece)(pieces[r][c].clone());
                        }
                    }
        
                    // Eliminate ourselves
                    pieces_copy[this.pos.y][this.pos.x] = null;
        
                    // Check for pieces who would be attacking
                    for (ChessPiece[] row: pieces_copy) {
                        for (ChessPiece piece: row) {
                            if (piece == null) {
                                continue;
                            }
                            if (!piece.isKing && piece.isInverted() != this.isInverted()) {
                                piece.isDrawingDots = true;
                                PieceSelectedMoves moves = piece.calculateMoveset(new HashMap<>(), pieces_copy, true);
                                piece.isDrawingDots = false;
                                for (int[] pos: moves.thingsToTake) {
                                    if (pos[0] == ourKing.pos.x && pos[1] == ourKing.pos.y) {
                                        attackers.add(piece);
                                    }
                                }
                            }
                        }
                    }
                    
                    // Check if we can take the attacker
                    PieceSelectedMoves moves = calculateMoveset(new HashMap<>(), pieces, true);
                    HashSet<int[]> ttt = moves.thingsToTake;
                    HashSet<int[]> final_ttt = new HashSet<>();
                    HashMap<ChessPosition, Color> final_colors = new HashMap<>();
                    for (int[] pos: ttt) {
                        for (ChessPiece attacker: attackers) {
                            if (attacker.pos.x == pos[0] && attacker.pos.y == pos[1]) {
                                final_ttt.add(pos);
                                final_colors.put(attacker.pos, new Color(240, 155, 129));
                            }
                        }
                    }
                    if (!final_ttt.isEmpty()) {
                        new_positions.clear();
                        new_thingsToTake.clear();
                        new_thingsToTake.addAll(final_ttt);
                        colors.clear();
                        colors.putAll(final_colors);
                    }
                    else if (!attackers.isEmpty()) {
                        message = "You must move another piece because you are pinned";
                        new_positions.clear();
                        new_thingsToTake.clear();
                        colors.clear();
                        finalPinned = true;
                    }
                }
            }
        }
        isDrawingDots = false;
        return new PieceSelectedMoves(new_positions, new_thingsToTake, this, finalInCheck, finalInCheckmate, message, finalPinned);
    }

    public boolean isTouching(ChessPosition mouse_pos){
        return pos.x == mouse_pos.x && pos.y == mouse_pos.y;
    }

    // Audio
    public static void playSound(String sound){
        try{
            AudioInputStream s = AudioSystem.getAudioInputStream(new File(sound));
            Clip c = AudioSystem.getClip();
            c.open(s);
            c.start();
            s.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    // Strings and drawing potential moves
    public String toString() {
        return type +" at " + pos.x + "," + pos.y + " with moveset " + moveSet;
    }

    public void drawDots() {
        this.isDrawingDots = !this.isDrawingDots;
    }

    interface Negator {
        int negate();
    }

    interface IsDone {
        boolean isDone(int x);
    }
}
