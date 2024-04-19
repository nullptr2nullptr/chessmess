package game;

import pieces.*;

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
    private ChessPiece activeKing;
    private ChessPiece otherKing;
    JLabel score = new JLabel("White: " + whiteScore + " - Black: " + blackScore);
    JLabel statusLabel, playerLabel;

    public GameBoard() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PIECE_LENGTH * 8, (PIECE_LENGTH * 8) + offset));

        // Top panel for reset button, scores, status, and current player
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Reset button
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        resetButton.setFont(new Font("Arial", Font.BOLD, 10));
        resetButton.setPreferredSize(new Dimension(75, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 0, 10); // Add insets for spacing
        topPanel.add(resetButton, gbc);

        // Scores
        score.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        topPanel.add(score, gbc);

        // Status label
        statusLabel = new JLabel("Status: Game Normal");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 10, 0, 10); // Add insets for spacing
        topPanel.add(statusLabel, gbc);

        // Player label
        playerLabel = new JLabel("Player: White");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 10, 0, 10); // Add insets for spacing
        topPanel.add(playerLabel, gbc);

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
                        "src/res/image/Chess_qdt60.png",
                        "src/res/image/Chess_kdt60.png",
                        "src/res/image/Chess_bdt60.png",
                        "src/res/image/Chess_ndt60.png",
                        "src/res/image/Chess_rdt60.png"},
                new PieceType[]{PieceType.ROOK,
                        PieceType.KNIGHT,
                        PieceType.BISHOP,
                        PieceType.QUEEN,
                        PieceType.KING,
                        PieceType.BISHOP,
                        PieceType.KNIGHT,
                        PieceType.ROOK},
                true, 0);

        // Adding white pieces
        addMostPiecesLeftToRight(new String[]{"src/res/image/Chess_rlt60.png",
                        "src/res/image/Chess_nlt60.png",
                        "src/res/image/Chess_blt60.png",
                        "src/res/image/Chess_qlt60.png",
                        "src/res/image/Chess_klt60.png",
                        "src/res/image/Chess_blt60.png",
                        "src/res/image/Chess_nlt60.png",
                        "src/res/image/Chess_rlt60.png"},
                new PieceType[]{PieceType.ROOK,
                        PieceType.KNIGHT,
                        PieceType.BISHOP,
                        PieceType.QUEEN,
                        PieceType.KING,
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

        this.activeKing = this.pieces[7][4];
        this.otherKing = this.pieces[0][4];
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

    private String getPlayerName() {
        if (isWhiteTurn) {
            return "White";
        } else {
            return "Black";
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
                try {
                    PieceSelectedMoves moves = piece.calculateMoveset(colors, pieces, false);
                    if (this.moves == null) {
                        this.moves = moves;
                    }
                    if (moves != null && moves.isPinned) {
                        System.out.println("INFO: "+ piece + " IS PINNED TO "+getPlayerName()+"'s KING.");
                    }
                } catch (CloneNotSupportedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
        score.setText("White: " + whiteScore + " - Black: " + blackScore);
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
            boolean didAMove = false;
            for (int[] xy : this.moves.positions) {
                if (xy[0] == pos.x && xy[1] == pos.y) {
                    piece.moveCount++;
                    this.pieces[piece.pos.y][piece.pos.x] = null;
                    this.pieces[pos.y][pos.x] = piece;
                    ChessPiece.playSound(Sounds.MOVE_SOUND_FILE);
                    piece.pos = pos;
                    didAMove=true;
                }
            }
            for (int[] xy : this.moves.thingsToTake) {
                if (xy[0] == pos.x && xy[1] == pos.y) {
                    piece.moveCount++;
                    ChessPiece pieceAt = this.pieces[pos.y][pos.x];
                    System.out.println("DEBUG: took "+ pieceAt);
                    this.pieces[piece.pos.y][piece.pos.x] = null;
                    this.pieces[pos.y][pos.x] = piece;
                    ChessPiece.playSound(Sounds.LEGO_SOUND_FILE);
                    piece.pos = pos;
                    if (isWhiteTurn){
                        whiteScore += getScore(pieceAt);
                        System.out.println(whiteScore);
                    } else{
                        blackScore += getScore(pieceAt);
                    }
                    didAMove=true;
                }
            }
            if (didAMove) {
                isWhiteTurn = !isWhiteTurn; // Changing the turn once someone has moved.
                if(isWhiteTurn){
                    playerLabel.setText("Player: White");
                } else {
                    playerLabel.setText("Player: Black");
                }
                piece.tryPromoteToQueen();
                ChessPiece tmp = this.activeKing;
                this.activeKing = this.otherKing;
                this.otherKing = tmp;
                statusLabel.setText("Status: Game Normal");
                try {
                    System.out.println("DEBUG: active king is "+this.activeKing);
                    this.activeKing.drawDots();
                    PieceSelectedMoves moves = this.activeKing.calculateMoveset(new HashMap<>(), pieces, false);
                    if (moves != null && moves.isMate) {
                        statusLabel.setText("Status: " + getPlayerName() + " wins!");
                        ChessPiece.playSound(Sounds.TROMBONE_SOUND_FILE);
                    } else if (moves != null && moves.isCheck) {
                        statusLabel.setText("Status: " + getPlayerName() + " is in check");
                    }
                } catch (CloneNotSupportedException e_) {
                    // TODO Auto-generated catch block
                    e_.printStackTrace();
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
            case KING: {
                return -1337; // impossible
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

        this.moves = null;

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
