package game;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import pieces.ChessPiece;
import pieces.PieceType;

import java.util.ArrayList;
import java.util.TreeSet;

public class GameBoard extends JPanel implements ActionListener, ItemListener, MouseListener, MouseMotionListener, KeyListener{

    static int PIECE_WIDTH = 60;
    private ArrayList<ChessPiece> pieces = new ArrayList<>();
    private Timer clock = new Timer(16, this);
    private TreeSet<Integer> keycodes = new TreeSet<Integer>();
    private int mouse_x = 0, mouse_y = 0;
    private int delay, width, height;
    private String MOVE_SOUND_FILE = "src/res/sound/sound1.wav";

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

        addBottomLtPieces();
        addTopDkPieces();
    }

    private void addBottomLtPieces() {
        pieces.add(new ChessPiece(PieceType.ROOK,
            false,
            "src/res/image/Chess_rlt60.png",
            MOVE_SOUND_FILE,
            0,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.KNIGHT,
            false,
            "src/res/image/Chess_nlt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.BISHOP,
            false,
            "src/res/image/Chess_blt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*2,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.KING,
            false,
            "src/res/image/Chess_klt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*3,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.QUEEN,
            false,
            "src/res/image/Chess_qlt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*4,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.BISHOP,
            false,
            "src/res/image/Chess_blt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*5,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.KNIGHT,
            false,
            "src/res/image/Chess_nlt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*6,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.ROOK,
            false,
            "src/res/image/Chess_rlt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*7,
            PIECE_WIDTH*7,
            PIECE_WIDTH,
            PIECE_WIDTH));
    }

    private void addTopDkPieces() {
        pieces.add(new ChessPiece(PieceType.ROOK,
            false,
            "src/res/image/Chess_rdt60.png",
            MOVE_SOUND_FILE,
            0,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.KNIGHT,
            false,
            "src/res/image/Chess_ndt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.BISHOP,
            false,
            "src/res/image/Chess_bdt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*2,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.KING,
            false,
            "src/res/image/Chess_kdt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*3,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.QUEEN,
            false,
            "src/res/image/Chess_qdt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*4,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.BISHOP,
            false,
            "src/res/image/Chess_bdt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*5,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.KNIGHT,
            false,
            "src/res/image/Chess_ndt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*6,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
        pieces.add(new ChessPiece(PieceType.ROOK,
            false,
            "src/res/image/Chess_rdt60.png",
            MOVE_SOUND_FILE,
            PIECE_WIDTH*7,
            0,
            PIECE_WIDTH,
            PIECE_WIDTH));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (ChessPiece piece: pieces) {
            piece.paint(g, this);
        }
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
