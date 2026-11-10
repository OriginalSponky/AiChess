import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AI extends Player {

    // --- MATRICI DI POSIZIONE (PST) ---
    private static final int[][] pawnPST = {
            { 0,  0,  0,  0,  0,  0,  0,  0}, {50, 50, 50, 50, 50, 50, 50, 50}, {10, 10, 20, 30, 30, 20, 10, 10},
            { 5,  5, 10, 25, 25, 10,  5,  5}, { 0,  0,  0, 20, 20,  0,  0,  0}, { 5, -5,-10,  0,  0,-10, -5,  5},
            { 5, 10, 10,-20,-20, 10, 10,  5}, { 0,  0,  0,  0,  0,  0,  0,  0}
    };
    private static final int[][] knightPST = {
            {-50,-40,-30,-30,-30,-30,-40,-50}, {-40,-20,  0,  0,  0,  0,-20,-40}, {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30}, {-30,  0, 15, 20, 20, 15,  0,-30}, {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  5,  5,  0,-20,-40}, {-50,-40,-30,-30,-30,-30,-40,-50}
    };
    private static final int[][] bishopPST = {
            {-20,-10,-10,-10,-10,-10,-10,-20}, {-10,  0,  0,  0,  0,  0,  0,-10}, {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10}, {-10,  0, 10, 10, 10, 10,  0,-10}, {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  5,  0,  0,  0,  0,  5,-10}, {-20,-10,-10,-10,-10,-10,-10,-20}
    };
    private static final int[][] rookPST = {
            {  0,  0,  0,  0,  0,  0,  0,  0}, {  5, 10, 10, 10, 10, 10, 10,  5}, { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5}, { -5,  0,  0,  0,  0,  0,  0, -5}, { -5,  0,  0,  0,  0,  0,  0, -5},
            { -5,  0,  0,  0,  0,  0,  0, -5}, {  0,  0,  0,  5,  5,  0,  0,  0}
    };
    private static final int[][] kingPST = {
            {-30,-40,-40,-50,-50,-40,-40,-30}, {-30,-40,-40,-50,-50,-40,-40,-30}, {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30}, {-20,-30,-30,-40,-40,-30,-30,-20}, {-10,-20,-20,-20,-20,-20,-20,-10},
            { 20, 20,  0,  0,  0,  0, 20, 20}, { 20, 30, 10,  0,  0, 10, 30, 20}
    };

    // --- TRANSPOSITION TABLE (ZOBRIST HASHING) ---
    private static final long[][][] zobristTable = new long[2][6][64];
    private static final long zobristTurnMask;
    private final Map<Long, TTEntry> transpositionTable = new HashMap<>();

    static {
        Random rnd = new Random(12345); // Seme fisso per generare sempre gli stessi hash
        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 6; j++)
                for(int k = 0; k < 64; k++)
                    zobristTable[i][j][k] = rnd.nextLong();
        zobristTurnMask = rnd.nextLong();
    }

    private static class TTEntry {
        int depth;
        int value;
        int flag; // 0 = EXACT, 1 = UPPERBOUND (Alpha), 2 = LOWERBOUND (Beta)
    }

    // --- COSTRUTTORE E INIZIALIZZAZIONE ---
    public AI(String name, boolean starter) {
        super(name, starter);
    }

    public Move getBestMove(Game game, int depth) {
        transpositionTable.clear(); // Pulisce la memoria all'inizio del turno
        List<Move> allMoves = generateLegalMoves(game, this.starter);

        Move bestMove = null;
        int bestScore = this.starter ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : allMoves) {
            game.makeMove(move);
            int score = minimax(game, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !this.starter);
            game.undoMove(move);

            if (this.starter) {
                if (score > bestScore) { bestScore = score; bestMove = move; }
            } else {
                if (score < bestScore) { bestScore = score; bestMove = move; }
            }
        }
        return bestMove;
    }

    // --- MINIMAX CON TRANSPOSITION TABLES ---
    private int minimax(Game game, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        int alphaOrig = alpha;

        // 1. Controllo se ho già calcolato questa esatta scacchiera (Transposition Table)
        long boardHash = computeHash(game, isMaximizingPlayer);
        TTEntry ttEntry = transpositionTable.get(boardHash);
        if (ttEntry != null && ttEntry.depth >= depth) {
            if (ttEntry.flag == 0) return ttEntry.value; // Valore esatto
            if (ttEntry.flag == 1 && ttEntry.value <= alpha) return ttEntry.value; // Taglio superiore
            if (ttEntry.flag == 2 && ttEntry.value >= beta) return ttEntry.value; // Taglio inferiore
        }

        // 2. Se arrivo al limite della profondità, INIZIA LA QUIESCENCE SEARCH!
        if (depth == 0) {
            return quiescenceSearch(game, alpha, beta, isMaximizingPlayer);
        }

        List<Move> allMoves = generateLegalMoves(game, isMaximizingPlayer);

        if (allMoves.isEmpty()) {
            if (game.isCheck(isMaximizingPlayer)) return isMaximizingPlayer ? -99999 : 99999;
            else return 0; // Stallo
        }

        int bestVal = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (isMaximizingPlayer) {
            for (Move move : allMoves) {
                game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, false);
                game.undoMove(move);

                bestVal = Math.max(bestVal, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Alpha-Beta Pruning
            }
        } else {
            for (Move move : allMoves) {
                game.makeMove(move);
                int eval = minimax(game, depth - 1, alpha, beta, true);
                game.undoMove(move);

                bestVal = Math.min(bestVal, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Alpha-Beta Pruning
            }
        }

        // 3. Salvo il risultato in memoria (Transposition Table) per non ricalcolarlo in futuro
        TTEntry newEntry = new TTEntry();
        newEntry.depth = depth;
        newEntry.value = bestVal;
        if (bestVal <= alphaOrig) newEntry.flag = 1; // Upperbound
        else if (bestVal >= beta) newEntry.flag = 2; // Lowerbound
        else newEntry.flag = 0; // Exact
        transpositionTable.put(boardHash, newEntry);

        return bestVal;
    }

    // --- QUIESCENCE SEARCH (Oltre l'orizzonte) ---
    private int quiescenceSearch(Game game, int alpha, int beta, boolean isMaximizingPlayer) {
        int standPat = evaluateBoard(game); // Punteggio della scacchiera se decidiamo di fermarci ora

        if (isMaximizingPlayer) {
            if (standPat >= beta) return beta; // Se non fare nulla ci dà già il massimo, fermiamoci
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat <= alpha) return alpha;
            if (standPat < beta) beta = standPat;
        }

        // Nella quiescenza, generiamo mosse normali ma filtriamo SOLO le catture o promozioni
        List<Move> allMoves = generateLegalMoves(game, isMaximizingPlayer);
        List<Move> captures = new ArrayList<>();
        for (Move m : allMoves) {
            if (m.capturedPiece != null || m.promotionType != null) captures.add(m);
        }

        if (isMaximizingPlayer) {
            int maxEval = standPat;
            for (Move move : captures) {
                game.makeMove(move);
                int eval = quiescenceSearch(game, alpha, beta, false);
                game.undoMove(move);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = standPat;
            for (Move move : captures) {
                game.makeMove(move);
                int eval = quiescenceSearch(game, alpha, beta, true);
                game.undoMove(move);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    // --- GENERATORE DI MOSSE CON ORDINAMENTO MVV-LVA ---
    private List<Move> generateLegalMoves(Game game, boolean team) {
        List<Move> moves = new ArrayList<>();

        for (int startRow = 0; startRow < 8; startRow++) {
            for (int startCol = 0; startCol < 8; startCol++) {
                Piece piece = game.board[startRow][startCol];
                if (piece != null && piece.team == team) {
                    for (int endRow = 0; endRow < 8; endRow++) {
                        for (int endCol = 0; endCol < 8; endCol++) {

                            if (game.isValidMove(piece, endRow, endCol)) {
                                Piece capturedPiece = game.board[endRow][endCol];

                                if (piece.type == Type.pawn && (endRow == 0 || endRow == 7)) {
                                    Type[] promoTypes = {Type.queen, Type.knight, Type.rook, Type.bishop};
                                    for (Type promo : promoTypes) {
                                        Move m = new Move(startRow, startCol, endRow, endCol, piece, capturedPiece);
                                        m.promotionType = promo;
                                        game.makeMove(m);
                                        if (!game.isCheck(team)) moves.add(m);
                                        game.undoMove(m);
                                    }
                                } else {
                                    Move pseudoLegalMove = new Move(startRow, startCol, endRow, endCol, piece, capturedPiece);
                                    game.makeMove(pseudoLegalMove);
                                    if (!game.isCheck(team)) moves.add(pseudoLegalMove);
                                    game.undoMove(pseudoLegalMove);
                                }
                            }

                            if (piece.type == Type.king && piece.isTheFirstMove() && startRow == endRow && Math.abs(endCol - startCol) == 2) {
                                if (game.canCastle(piece, endRow, endCol)) {
                                    Move castlingMove = new Move(startRow, startCol, endRow, endCol, piece, null);
                                    castlingMove.isCastling = true;
                                    game.makeMove(castlingMove);
                                    if (!game.isCheck(team)) moves.add(castlingMove);
                                    game.undoMove(castlingMove);
                                }
                            }
                        }
                    }
                }
            }
        }

        // ORDINAMENTO MVV-LVA: Prova prima a mangiare i pezzi di valore alto (Regina) con pezzi di valore basso (Pedoni)
        moves.sort((m1, m2) -> {
            int score1 = 0;
            int score2 = 0;

            if (m1.capturedPiece != null) score1 = (10 * getPieceValue(m1.capturedPiece)) - getPieceValue(m1.movedPiece);
            if (m1.promotionType != null) score1 += 900;

            if (m2.capturedPiece != null) score2 = (10 * getPieceValue(m2.capturedPiece)) - getPieceValue(m2.movedPiece);
            if (m2.promotionType != null) score2 += 900;

            return Integer.compare(score2, score1); // Ordinamento decrescente
        });

        return moves;
    }

    // --- FUNZIONI DI VALUTAZIONE ---
    private int evaluateBoard(Game game) {
        int score = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece p = game.board[i][j];
                if(p == null) continue;

                int pieceValue = getPieceValue(p);
                int rowPos = p.team ? i : (7 - i);

                switch (p.type) {
                    case pawn -> pieceValue += pawnPST[rowPos][j];
                    case knight -> pieceValue += knightPST[rowPos][j];
                    case bishop -> pieceValue += bishopPST[rowPos][j];
                    case rook -> pieceValue += rookPST[rowPos][j];
                    case king -> pieceValue += kingPST[rowPos][j];
                }

                if (p.team) score += pieceValue; // Bianco = Positivo
                else score -= pieceValue; // Nero = Negativo
            }
        }
        return score;
    }

    private int getPieceValue(Piece p) {
        if (p == null) return 0;
        return switch (p.type) {
            case pawn -> 100;
            case knight -> 320;
            case bishop -> 330;
            case rook -> 500;
            case queen -> 900;
            case king -> 20000;
        };
    }

    private long computeHash(Game game, boolean isMaximizingPlayer) {
        long hash = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = game.board[r][c];
                if (p != null) {
                    int teamIdx = p.team ? 1 : 0;
                    hash ^= zobristTable[teamIdx][p.type.ordinal()][r * 8 + c];
                }
            }
        }
        if (isMaximizingPlayer) hash ^= zobristTurnMask;
        return hash;
    }
}