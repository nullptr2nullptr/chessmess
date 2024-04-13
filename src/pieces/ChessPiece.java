package pieces;

/*
 * This code is very complicated. Do not _ever_ touch it - it will break. It is a fragile eggshell...
 */

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

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

public class ChessPiece {
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

    public ChessPiece(PieceType type, boolean isInverted, String iconPath, ChessPosition pos, int width, int height) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        this.type = type;
        this.icon = new ImageIcon(iconPath);
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.isDrawingDots = false;
        this.isInverted = isInverted;

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
        System.out.println("Move: " + randomIndex);
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

    public PieceSelectedMoves preparePaint(HashMap<ChessPosition, Color> colors, ChessPiece[][] pieces){
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
                positions.add(new int[]{pos.x, pos.y - 2});
            }
            else if (this.moveSet == PAWN_MOVES >> 16 && this.moveCount == 0) {
                positions.add(new int[]{pos.x, pos.y + 1});
                positions.add(new int[]{pos.x, pos.y + 2});
            }
            else if (this.moveSet == PAWN_MOVES && this.moveCount != 0) {
                if (pieces[pos.y-1][pos.x] == null) {
                    positions.add(new int[]{pos.x, pos.y - 1});
                }
            }
            else if (this.moveSet == PAWN_MOVES >> 16 && this.moveCount != 0) {
                if (pieces[pos.y+1][pos.x+1] == null) {
                    positions.add(new int[]{pos.x, pos.y + 1});
                }
            }
            if (this.moveSet == PAWN_MOVES) {
                if (pieces[pos.y-1][pos.x-1] != null && pieces[pos.y-1][pos.x-1].isInverted() != this.isInverted()) {
                    thingsToTake.add(new int[]{pos.x - 1, pos.y - 1});
                }
                if (pieces[pos.y-1][pos.x+1] != null && pieces[pos.y-1][pos.x+1].isInverted() != this.isInverted()) {
                    thingsToTake.add(new int[]{pos.x + 1, pos.y - 1});
                }
            }
            else if (this.moveSet == PAWN_MOVES >> 16) {
                if (pieces[pos.y+1][pos.x-1] != null && pieces[pos.y+1][pos.x-1].isInverted() != this.isInverted()) {
                    thingsToTake.add(new int[]{pos.x - 1, pos.y + 1});
                }
                if (pieces[pos.y+1][pos.x+1] != null && pieces[pos.y+1][pos.x+1].isInverted() != this.isInverted()) {
                    thingsToTake.add(new int[]{pos.x + 1, pos.y + 1});
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
        ArrayList<int[]> new_positions = new ArrayList<>();
        ArrayList<int[]> new_thingsToTake = new ArrayList<>();
        for (int[] xy: positions) {
            if (xy[1] >= 8 || xy[1] < 0 || xy[0] >= 8 || xy[0] < 0) {
                continue;
            }
            if (pieces[xy[1]][xy[0]] != null && pieces[xy[1]][xy[0]].isInverted() != this.isInverted()) {
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
        isDrawingDots = false;
        return new PieceSelectedMoves(new_positions, new_thingsToTake, this);
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

    public void update(int delay, int width, int height){
        //Note delay is 16 ms
        /*y += v_y * delay;
        x += v_x * delay;
        v_y += a_y * delay;
        v_x += a_x * delay;

        if(x <= 0){
            v_x = -1 * v_x;
            x = 0;
        }else if((x + this.width) >= width){
            v_x = -1 * v_x;
            x = width - this.width;
        }
        if(y <= 0){
            v_y = -1 * v_y;
            y = 0;
        }else if((y + this.height) >= height){
            v_y = -1 * v_y;
            y = height - this.height;
        }*/
    }

    interface Negator {
        int negate();
    }

    interface IsDone {
        boolean isDone(int x);
    }
}
