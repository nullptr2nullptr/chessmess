package game;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import pieces.ChessPiece;
import pieces.PieceType;

import java.util.TreeSet;

public class GameBoard extends JPanel implements ActionListener, ItemListener, MouseListener, MouseMotionListener, KeyListener{

    static int PIECE_WIDTH = 60;
    private ChessPiece leftLightRook = new ChessPiece(PieceType.ROOK,
                                        false,
                                        "src/res/image/Chess_rlt60.png",
                                        "src/res/sound/sound1.wav",
                                        0,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece leftLightKnight = new ChessPiece(PieceType.KNIGHT,
                                        false,
                                        "src/res/image/Chess_nlt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece leftLightBishop = new ChessPiece(PieceType.BISHOP,
                                        false,
                                        "src/res/image/Chess_blt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH*2,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece leftLightKing = new ChessPiece(PieceType.KING,
                                        false,
                                        "src/res/image/Chess_klt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH*3,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece rightLightQueen = new ChessPiece(PieceType.QUEEN,
                                        false,
                                        "src/res/image/Chess_qlt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH*4,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece rightLightBishop = new ChessPiece(PieceType.BISHOP,
                                        false,
                                        "src/res/image/Chess_blt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH*5,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece rightLightKnight = new ChessPiece(PieceType.KNIGHT,
                                        false,
                                        "src/res/image/Chess_nlt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH*6,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private ChessPiece rightLightRook = new ChessPiece(PieceType.ROOK,
                                        false,
                                        "src/res/image/Chess_rlt60.png",
                                        "src/res/sound/sound1.wav",
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH*7,
                                        PIECE_WIDTH,
                                        PIECE_WIDTH);
    private Timer clock = new Timer(16, this);
    private TreeSet<Integer> keycodes = new TreeSet<Integer>();
    private int mouse_x = 0, mouse_y = 0;
    private int delay, width, height;

    public GameBoard(int delay) {
        setFocusable(true);
        this.width = PIECE_WIDTH * 8;
        this.height = PIECE_WIDTH * 8;
        this.delay = delay;
        setPreferredSize(new Dimension(this.width, this.height));
        clock.start();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        leftLightRook.paint(g, this);
        leftLightKnight.paint(g, this);
        leftLightBishop.paint(g, this);
        leftLightKing.paint(g, this);
        rightLightQueen.paint(g, this);
        rightLightBishop.paint(g, this);
        rightLightKnight.paint(g, this);
        rightLightRook.paint(g, this);
    }

    //Mouse Listener Stuff
    public void mouseClicked(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mousePressed(MouseEvent e){

    }

    public void mouseReleased(MouseEvent e){
        repaint();
    }

    //Mouse Motion Listener stuff
    public void mouseDragged(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    public void mouseMoved(MouseEvent e){
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    //Key Listener Stuff
    public void keyTyped(KeyEvent e){}

    public void keyPressed(KeyEvent e){
        keycodes.add(e.getKeyCode());
    }
    
    public void keyReleased(KeyEvent e){
        keycodes.remove(e.getKeyCode());
    }

    //Action Listener Stuff
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == clock){
            // TODO
        }
        repaint();
    }

    //Item Listener Stuff
    public void itemStateChanged(ItemEvent e){}
}
