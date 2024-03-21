package pieces;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

import game.GameBoard;

import java.awt.*;

public class ChessPiece {
    static int BASE = 0b1000000000000000000000000000000;
    static int UP = BASE >> 1;
    static int RIGHT = BASE >> 2;
    static int DIAG = BASE >> 3;
    static int LEFT_KNIGHT = BASE >> 4;
    static int RIGHT_KNIGHT = BASE >> 5;
    static int DOWN = BASE >> 6;
    static int LEFT = BASE >> 7;
    static int DOWN_DIAG = BASE >> 8;
    static int DOWN_LEFT_KNIGHT = BASE >> 9;
    static int DOWN_RIGHT_KNIGHT = BASE >> 10;
    static int IS_ONE = BASE >> 11;
    static int IS_KNIGHT_UP = BASE >> 12;
    static int LEFT_DIAG = BASE >> 14;
    static int DOWN_LEFT_DIAG = BASE >> 15;
    static int UP_INV = BASE >> 1 >> 16;
    static int RIGHT_INV = BASE >> 2 >> 16;
    static int DIAG_INV = BASE >> 3 >> 16;
    static int LEFT_KNIGHT_INV = BASE >> 4 >> 16;
    static int RIGHT_KNIGHT_INV = BASE >> 5 >> 16;
    static int DOWN_INV = BASE >> 6 >> 16;
    static int LEFT_INV = BASE >> 7 >> 16;
    static int DOWN_DIAG_INV = BASE >> 8 >> 16;
    static int DOWN_LEFT_KNIGHT_INV = BASE >> 9 >> 16;
    static int DOWN_RIGHT_KNIGHT_INV = BASE >> 10 >> 16;
    static int IS_ONE_INV = BASE >> 11 >> 16;
    static int IS_KNIGHT_UP_INV = BASE >> 12 >> 16;
    static int LEFT_DIAG_INV = BASE >> 14 >> 16;
    static int DOWN_LEFT_DIAG_INV = BASE >> 15 >> 16;

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
    ChessPosition pos;
    double v_x = 0, v_y = 0, a_x = 0, a_y = .0005;
    String moveFile;
    boolean isDrawingDots;

    public ChessPiece(PieceType type, boolean isInverted, String iconPath, String moveFile, ChessPosition pos, int width, int height) {
        this.type = type;
        this.icon = new ImageIcon(iconPath);
        this.moveFile = moveFile;
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.isDrawingDots = false;

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

    public void paint(Graphics g, GameBoard p){
        playSound();
        g.drawImage(this.icon.getImage(), pos.x * GameBoard.PIECE_WIDTH, pos.y * GameBoard.PIECE_WIDTH, width, height, p);
    }

    public void preparePaint(HashMap<ChessPosition, Color> colors, ChessPiece[][] pieces){
        ArrayList<int[]> positions = new ArrayList<>();
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
                return x <= 8;
            };
        } else if (isInverted() && isDrawingDots) {
            negate = () -> {
                return 1;
            };
            doner = (x) -> {
                return x <= 8;
            };
            doner_opp = (x) -> {
                return x >= 0;
            };
        } else {
            // IMPOSSIBLE
            return;
        }

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
        
        if (((this.moveSet & IS_ONE) != 0 && !isInverted()) || ((this.moveSet & IS_ONE_INV) != 0 && isInverted())) {
            if (((this.moveSet & UP) != 0 && !isInverted()) || ((this.moveSet & UP_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x, pos.y + negate.negate()});
            }
            if (((this.moveSet & DOWN) != 0 && !isInverted()) || ((this.moveSet & DOWN_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x, pos.y + -negate.negate()});
            }
            if (((this.moveSet & RIGHT) != 0 && !isInverted()) || ((this.moveSet & RIGHT_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x + -negate.negate(), pos.y});
            }
            if (((this.moveSet & LEFT) != 0 && !isInverted()) || ((this.moveSet & LEFT_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x + negate.negate(), pos.y});
            }
            if (((this.moveSet & DIAG) != 0 && !isInverted()) || ((this.moveSet & DIAG_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x + -negate.negate(), pos.y + negate.negate()});
            }
            if (((this.moveSet & LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & LEFT_DIAG_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x + negate.negate(), pos.y + negate.negate()});
            }
            if (((this.moveSet & DOWN_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_DIAG_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x + -negate.negate(), pos.y + -negate.negate()});
            }
            if (((this.moveSet & DOWN_LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_LEFT_DIAG_INV) != 0 && isInverted())) {
                positions.add(new int[]{pos.x + negate.negate(), pos.y + -negate.negate()});
            }
        } else {
            if (((this.moveSet & UP) != 0 && !isInverted()) || ((this.moveSet & UP_INV) != 0 && isInverted())) {
                for (int row = pos.y + negate.negate(); doner.isDone(row); row = row + negate.negate()) {
                    positions.add(new int[]{pos.x, row});
                }
            }
            if (((this.moveSet & DOWN) != 0 && !isInverted()) || ((this.moveSet & DOWN_INV) != 0 && isInverted())) {
                for (int row = pos.y + -negate.negate(); doner_opp.isDone(row); row = row + -negate.negate()) {
                    positions.add(new int[]{pos.x, row});
                }
            }
            if (((this.moveSet & RIGHT) != 0 && !isInverted()) || ((this.moveSet & RIGHT_INV) != 0 && isInverted())) {
                for (int col = pos.x + -negate.negate(); doner_opp.isDone(col); col = col + -negate.negate()) {
                    positions.add(new int[]{col, pos.y});
                }
            }
            if (((this.moveSet & LEFT) != 0 && !isInverted()) || ((this.moveSet & LEFT_INV) != 0 && isInverted())) {
                for (int col = pos.x + negate.negate(); doner.isDone(col); col = col + negate.negate()) {
                    positions.add(new int[]{col, pos.y});
                }
            }
            if (((this.moveSet & DIAG) != 0 && !isInverted()) || ((this.moveSet & DIAG_INV) != 0 && isInverted())) {
                int row = pos.y + negate.negate();
                for (int col = pos.x + -negate.negate(); doner_opp.isDone(col); col = col + -negate.negate()) {
                    positions.add(new int[]{col,row});
                    row = row + negate.negate();
                }
            }
            if (((this.moveSet & LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & LEFT_DIAG_INV) != 0 && isInverted())) {
                int row = pos.y + negate.negate();
                for (int col = pos.x + negate.negate(); doner.isDone(col); col = col + negate.negate()) {
                    positions.add(new int[]{col,row});
                    row = row + negate.negate();
                }
            }
            if (((this.moveSet & DOWN_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_DIAG_INV) != 0 && isInverted())) {
                int row = pos.y - negate.negate();
                for (int col = pos.x + -negate.negate(); doner_opp.isDone(col); col = col + -negate.negate()) {
                    positions.add(new int[]{col,row});
                    row = row - negate.negate();
                }
            }
            if (((this.moveSet & DOWN_LEFT_DIAG) != 0 && !isInverted()) || ((this.moveSet & DOWN_LEFT_DIAG_INV) != 0 && isInverted())) {
                int row = pos.y - negate.negate();
                for (int col = pos.x + negate.negate(); doner.isDone(col); col = col + negate.negate()) {
                    positions.add(new int[]{col,row});
                    row = row - negate.negate();
                }
            }
        }

        for (int[] xy: positions) {
            colors.put(new ChessPosition(xy[0], xy[1]), new Color(175, 215, 250));
        }
        isDrawingDots = false;
    }

    public boolean isTouching(ChessPosition mouse_pos){
        return pos.x == mouse_pos.x && pos.y == mouse_pos.y;
    }

    public void playSound(){
        /*try{
            AudioInputStream s = AudioSystem.getAudioInputStream(new File(this.moveFile));
            Clip c = AudioSystem.getClip();
            c.open(s);
            c.start();
            s.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }*/
    }

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
