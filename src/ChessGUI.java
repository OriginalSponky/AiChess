import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {
    private final JButton[][] squares = new JButton[8][8];
    private Game game;
    private AI ai;
    private RealPlayer human;

    // Colori della scacchiera
    private final Color lightSquare = new Color(238, 238, 210);
    private final Color darkSquare = new Color(118, 150, 86);
    private final Color selectedColor = new Color(186, 202, 68); // Verde acido per il pezzo selezionato
    private final Color highlightColor = new Color(246, 246, 130); // Giallo tenue per l'ultima mossa

    private int selRow = -1, selCol = -1;

    // Variabili per tracciare l'ultima mossa
    private int lastMoveStartRow = -1, lastMoveStartCol = -1;
    private int lastMoveEndRow = -1, lastMoveEndCol = -1;

    private boolean isHumanTurn = true; // Iniziamo noi col Bianco

    public ChessGUI() {
        setTitle("Motore Scacchistico AI - Tocca a te");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));
        setLocationRelativeTo(null);

        ai = new AI("Computer (Nero)", false);
        human = new RealPlayer("Umano (Bianco)", true);
        game = new Game(true, ai, human);

        initializeBoardUI();
        updateBoardUI();
        setVisible(true);
    }

    private void initializeBoardUI() {
        Font pieceFont = new Font("SansSerif", Font.PLAIN, 50);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = new JButton();
                btn.setFont(pieceFont);
                btn.setFocusPainted(false);
                btn.setOpaque(true);
                btn.setBorderPainted(false);

                int finalR = r;
                int finalC = c;
                btn.addActionListener(e -> handleSquareClick(finalR, finalC));

                squares[r][c] = btn;
                add(btn);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        if (!isHumanTurn) return; // Ignora i click se sta calcolando l'AI

        Piece clickedPiece = game.board[row][col];

        // 1. Seleziono un mio pezzo
        if (clickedPiece != null && clickedPiece.team == human.starter) {
            selRow = row;
            selCol = col;
            updateBoardUI();
            squares[row][col].setBackground(selectedColor); // Illumina la selezione corrente
            return;
        }

        // 2. Muovo il pezzo selezionato in una destinazione
        if (selRow != -1 && selCol != -1) {
            String startPos = "" + (char) ('a' + selCol) + (8 - selRow);
            String endPos = "" + (char) ('a' + col) + (8 - row);

            boolean success = game.move(startPos, endPos, human);

            if (success) {
                // Salva le coordinate per l'evidenziazione gialla
                lastMoveStartRow = selRow;
                lastMoveStartCol = selCol;
                lastMoveEndRow = row;
                lastMoveEndCol = col;

                selRow = -1;
                selCol = -1;
                updateBoardUI();
                isHumanTurn = false;

                checkGameState(human.starter);
                if (!isHumanTurn) {
                    triggerAITurn();
                }
            } else {
                // Deseleziona se la mossa era illegale
                selRow = -1;
                selCol = -1;
                updateBoardUI();
            }
        }
    }

    private void triggerAITurn() {
        setTitle("Motore Scacchistico AI - Sto pensando...");

        new Thread(() -> {
            Move aiMove = ai.getBestMove(game, 5);

            SwingUtilities.invokeLater(() -> {
                if (aiMove != null) {
                    String startPos = "" + (char) ('a' + aiMove.startCol) + (8 - aiMove.startRow);
                    String endPos = "" + (char) ('a' + aiMove.endCol) + (8 - aiMove.endRow);
                    game.move(startPos, endPos, ai);

                    // Salva le coordinate della mossa dell'AI per l'evidenziazione gialla
                    lastMoveStartRow = aiMove.startRow;
                    lastMoveStartCol = aiMove.startCol;
                    lastMoveEndRow = aiMove.endRow;
                    lastMoveEndCol = aiMove.endCol;

                    updateBoardUI();
                    isHumanTurn = true;
                    setTitle("Motore Scacchistico AI - Tocca a te");

                    checkGameState(ai.starter);
                }
            });
        }).start();
    }

    private void updateBoardUI() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                // Colorazione base chiara/scura
                Color squareColor = ((r + c) % 2 == 0) ? lightSquare : darkSquare;

                // Sovrascrive con il giallo se la casella fa parte dell'ultima mossa giocata
                if ((r == lastMoveStartRow && c == lastMoveStartCol) ||
                        (r == lastMoveEndRow && c == lastMoveEndCol)) {
                    squareColor = highlightColor;
                }

                squares[r][c].setBackground(squareColor);

                // Aggiorna il testo e il colore del pezzo
                Piece p = game.board[r][c];
                if (p != null) {
                    squares[r][c].setText(p.charPiece());
                    squares[r][c].setForeground(p.team ? Color.BLACK : Color.DARK_GRAY);
                } else {
                    squares[r][c].setText("");
                }
            }
        }
    }

    private void checkGameState(boolean currentTeam) {
        if (game.isCheck(!currentTeam)) {
            setTitle(getTitle() + " (SCACCO!)");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}