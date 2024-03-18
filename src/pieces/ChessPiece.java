package pieces;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

import game.GameBoard;

import java.awt.*;

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
    ImageIcon icon;
    int width, height;
    ChessPosition pos;
    double v_x = 0, v_y = 0, a_x = 0, a_y = .0005;
    String moveFile;

    public ChessPiece(PieceType type, boolean isInverted, String iconPath, String moveFile, ChessPosition pos, int width, int height) {
        this.type = type;
        this.icon = new ImageIcon(iconPath);
        this.moveFile = moveFile;
        this.pos = pos;
        this.width = width;
        this.height = height;

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
        g.drawImage(this.icon.getImage(), pos.x * GameBoard.PIECE_WIDTH, pos.y * GameBoard.PIECE_WIDTH, width, height, p);
    }

    public boolean isTouching(ChessPosition mouse_pos){
        return pos.x == mouse_pos.x && pos.y == mouse_pos.y;
    }

    public void changeImg(){
        try{
            AudioInputStream s = AudioSystem.getAudioInputStream(new File(this.moveFile));
            Clip c = AudioSystem.getClip();
            c.open(s);
            c.start();
            s.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String toString() {
        return type +" at " + pos.x + "," + pos.y + " with moveset " + moveSet;
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
