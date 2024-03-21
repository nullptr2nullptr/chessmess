package pieces;

import java.io.File;
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
    static int IS_KING = BASE >> 11;
    static int IS_PAWN = BASE >> 12;
    static int LEFT_DIAG = BASE >> 14;
    static int DOWN_LEFT_DIAG = BASE >> 15;

    static int KING_MOVES = UP | DOWN | LEFT | RIGHT | IS_KING;
    static int ROOK_MOVES = UP | DOWN | LEFT | RIGHT;
    static int PAWN_MOVES = UP | IS_PAWN;
    static int KNIGHT_MOVES = LEFT_KNIGHT | RIGHT_KNIGHT | DOWN_LEFT_KNIGHT | DOWN_RIGHT_KNIGHT;
    static int BISHOP_MOVES = DIAG | DOWN_DIAG | LEFT_DIAG | DOWN_LEFT_DIAG;
    static int QUEEN_MOVES = UP | DOWN | LEFT | RIGHT | DIAG | DOWN_DIAG | LEFT_DIAG | DOWN_LEFT_DIAG;

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

    public void preparePaint(HashMap<ChessPosition, Color> colors){
        if (!isInverted() && isDrawingDots) {
            colors.put(new ChessPosition(pos.x, pos.y-1), Color.BLUE);
        } else if (isInverted() && isDrawingDots) {
            colors.put(new ChessPosition(pos.x, pos.y+1), Color.BLUE);
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
}
