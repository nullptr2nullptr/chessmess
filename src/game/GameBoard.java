package game;

import pieces.ChessPiece;
import pieces.ChessPosition;
import pieces.PieceSelectedMoves;
import pieces.PieceType;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import java.io.IOException;
import java.util.HashMap;

public class GameBoard extends JPanel implements ActionListener, ItemListener, MouseListener, MouseMotionListener {
    public static int PIECE_LENGTH = 60;
    private final ChessPiece[][] pieces = new ChessPiece[8][8];
    private int mouse_x = 0, mouse_y = 0;
    private boolean isWhiteTurn = true; // White goes first
    private PieceSelectedMoves moves = null;
    private int blackScore = 0;
    private int whiteScore = 0;
    public static int offset = 26;
    JLabel score = new JLabel("White Score: " + whiteScore + " - Black Score: " + blackScore);

    public GameBoard() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PIECE_LENGTH * 8, (PIECE_LENGTH * 8) + offset));

        // Top panel for reset button and scores
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        resetButton.setFont(new Font("Arial", Font.BOLD, 10));
        resetButton.setPreferredSize(new Dimension(75, 20));
        topPanel.add(resetButton);
        topPanel.add(Box.createHorizontalGlue());
        score.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(score);
        add(topPanel, BorderLayout.NORTH);

        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);

        initializePieces();
    }

    private void initializePieces() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // Adding black pieces
        addMostPiecesLeftToRight(new String[]{"src/res/image/Chess_rdt60.png",
                        "src/res/image/Chess_ndt60.png",
                        "src/res/image/Chess_bdt60.png",
                        "src/res/image/Chess_kdt60.png",
                        "src/res/image/Chess_qdt60.png",
                        "src/res/image/Chess_bdt60.png",
                        "src/res/image/Chess_ndt60.png",
                        "src/res/image/Chess_rdt60.png"},
                new PieceType[]{PieceType.ROOK,
                        PieceType.KNIGHT,
                        PieceType.BISHOP,
                        PieceType.KING,
                        PieceType.QUEEN,
                        PieceType.BISHOP,
                        PieceType.KNIGHT,
                        PieceType.ROOK},
                true, 0);

        // Adding white pieces
        addMostPiecesLeftToRight(new String[]{"src/res/image/Chess_rlt60.png",
                        "src/res/image/Chess_nlt60.png",
                        "src/res/image/Chess_blt60.png",
                        "src/res/image/Chess_klt60.png",
                        "src/res/image/Chess_qlt60.png",
                        "src/res/image/Chess_blt60.png",
                        "src/res/image/Chess_nlt60.png",
                        "src/res/image/Chess_rlt60.png"},
                new PieceType[]{PieceType.ROOK,
                        PieceType.KNIGHT,
                        PieceType.BISHOP,
                        PieceType.KING,
                        PieceType.QUEEN,
                        PieceType.BISHOP,
                        PieceType.KNIGHT,
                        PieceType.ROOK},
                false, 7);

        // Adding black pawns
        addMostPiecesLeftToRight(new String[]{"src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png",
                        "src/res/image/Chess_pdt60.png"},
                new PieceType[]{PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN},
                true, 1);

        // Adding white pawns
        addMostPiecesLeftToRight(new String[]{"src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png",
                        "src/res/image/Chess_plt60.png"},
                new PieceType[]{PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN,
                        PieceType.PAWN},
                false, 6);
    }

    private void addMostPiecesLeftToRight(String[] icons, PieceType[] types, boolean isInverted, int row) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        for (int col = 0; col < 8; col++) {
            this.pieces[row][col] = (new ChessPiece(types[col],
                    isInverted,
                    icons[col],
                    new ChessPosition(col, row),
                    PIECE_LENGTH,
                    PIECE_LENGTH));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        HashMap<ChessPosition, Color> colors = new HashMap<>();
        for (ChessPiece[] pieces2 : pieces) {
            for (ChessPiece piece : pieces2) {
                if (piece == null) {
                    continue;
                }
                PieceSelectedMoves moves = piece.preparePaint(colors, pieces);
                if (this.moves == null) {
                    this.moves = moves;
                }
            }
        }
        drawCheckerboard(g, colors);
        for (ChessPiece[] pieces2 : pieces) {
            for (ChessPiece piece : pieces2) {
                if (piece == null) {
                    continue;
                }
                try {
                    piece.paint(g, this);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        score.setText("White Score: " + whiteScore + " - Black Score: " + blackScore);
    }

    private void drawCheckerboard(Graphics g, HashMap<ChessPosition, Color> colors) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                ChessPosition p = new ChessPosition(column, row);
                boolean override = false;
                for (ChessPosition key : colors.keySet()) {
                    if (p.x == key.x && p.y == key.y) {
                        g.setColor(colors.get(key));
                        g.fillRect(column * PIECE_LENGTH, row * PIECE_LENGTH + offset, PIECE_LENGTH, PIECE_LENGTH);
                        override = true;
                        break;
                    }
                }
                if (override) {
                    continue;
                }
                if ((row + column) % 2 == 0) g.setColor(new Color(240, 217, 181));
                else g.setColor(new Color(181, 136, 99));
                g.fillRect(column * PIECE_LENGTH, row * PIECE_LENGTH + offset, PIECE_LENGTH, PIECE_LENGTH);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
        ChessPosition pos = new ChessPosition(mouse_x / PIECE_LENGTH, (mouse_y - offset) / PIECE_LENGTH);
        if (this.moves != null) {
            ChessPiece piece = this.moves.piece;
            for (int[] xy : this.moves.positions) {
                if (xy[0] == pos.x && xy[1] == pos.y) {
                    piece.moveCount++;
                    this.pieces[piece.pos.y][piece.pos.x] = null;
                    this.pieces[pos.y][pos.x] = piece;
                    piece.pos = pos;
                    isWhiteTurn = !isWhiteTurn;
                    piece.tryPromoteToQueen();
                }
            }
            for (int[] xy : this.moves.thingsToTake) {
                if (xy[0] == pos.x && xy[1] == pos.y) {
                    piece.moveCount++;
                    ChessPiece pieceAt = this.pieces[pos.y][pos.x];
                    System.out.println(pieceAt);
                    this.pieces[piece.pos.y][piece.pos.x] = null;
                    this.pieces[pos.y][pos.x] = piece;
                    piece.pos = pos;
                    if (isWhiteTurn){
                        whiteScore += getScore(pieceAt);
                        System.out.println(whiteScore);
                    } else{
                        blackScore += getScore(pieceAt);
                    }
                    isWhiteTurn = !isWhiteTurn;
                    piece.tryPromoteToQueen();
                }
            }
            this.moves = null;
        } else {
            for (ChessPiece[] pieces2 : pieces) {
                for (ChessPiece p : pieces2) {
                    if (p == null) {
                        continue;
                    }
                    if (p.isTouching(pos) && (!p.isInverted() && isWhiteTurn || p.isInverted() && !isWhiteTurn)) {
                        handlePieceClick(p);
                        break;
                    }
                }
            }
        }
        repaint();
    }

    private int getScore(ChessPiece piece) {
        switch (piece.getType()) {
            case PAWN: {
                return 1;
            }
            case KNIGHT: { 
                return 3;
            }
            case BISHOP: { 
                return 3;
            }
            case ROOK: {
                return 5;
            }
            case QUEEN: {
                return 9;
            }
        }
        return 0;
    }

    private void resetGame() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        // Reset scores
        whiteScore = 0;
        blackScore = 0;

        // Remove all pieces from the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = null;
            }
        }

        // Reinitialize pieces
        initializePieces();

        // Set white to go first
        isWhiteTurn = true;

        // Repaint panel
        repaint();
    }

    private void handlePieceClick(ChessPiece p) {
        p.drawDots();
    }

    // Mouse Listener Other Functions
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    // Mouse Motion Listener Other Functions
    public void mouseDragged(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    public void mouseMoved(MouseEvent e) {
        mouse_x = e.getX();
        mouse_y = e.getY();
    }

    // Action Listener Other Function
    public void actionPerformed(ActionEvent event) {
        try {
            resetGame();
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    // Item Listener Other Function
    public void itemStateChanged(ItemEvent e) {
    }
}
