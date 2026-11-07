import java.util.ArrayList;
import java.util.List;

public class AI extends Player {

    public AI(String name, boolean starter) {
        super(name, starter);
    }

    // Il metodo che il Main chiamerà quando è il turno dell'AI
    public Move getBestMove(Game game, int depth) {
        List<Move> allMoves = generateLegalMoves(game, this.starter);

        Move bestMove = null;
        int bestScore = this.starter ? Integer.MIN_VALUE : Integer.MAX_VALUE; // Il Bianco massimizza, il Nero minimizza

        for (Move move : allMoves) {
            game.makeMove(move); // Simula la mossa

            // Calcola quanto è buona questa mossa guardando al futuro (ricorsione)
            // L'avversario minimizzerà, quindi gli passiamo false, e i limiti minimi e massimi per l'Alpha-Beta
            int score = minimax(game, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            game.undoMove(move); // Annulla la simulazione

            // Se sono il Bianco voglio il punteggio più alto, se sono il Nero il più basso
            if (this.starter) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    private int minimax(Game game, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        // Caso base: abbiamo raggiunto la profondità massima di calcolo
        if (depth == 0) {
            return evaluateBoard(game);
        }

        // Determina di chi è il turno in questo specifico nodo di simulazione
        boolean currentTeam = isMaximizingPlayer ? this.starter : !this.starter;
        List<Move> allMoves = generateLegalMoves(game, currentTeam);

        // Se non ci sono mosse, la partita è finita (Scacco Matto o Stallo)
        if (allMoves.isEmpty()) {
            if (game.isCheck(currentTeam)) {
                // Scacco Matto: chi subisce il matto perde.
                // Se tocca a noi (isMaximizingPlayer), è un disastro (-99999)
                // Se tocca all'avversario, è una vittoria schiacciante (+99999)
                return isMaximizingPlayer ? -99999 : 99999;
            } else {
                return 0; // Stallo: la partita finisce in patta
            }
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : allMoves) {
                game.makeMove(move); // Simula
                int eval = minimax(game, depth - 1, alpha, beta, false); // Ricorsione
                game.undoMove(move); // Annulla

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                // Alpha-Beta Pruning: l'avversario ha un'alternativa precedente migliore, inutile continuare a cercare qui
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : allMoves) {
                game.makeMove(move); // Simula
                int eval = minimax(game, depth - 1, alpha, beta, true); // Ricorsione
                game.undoMove(move); // Annulla

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                // Alpha-Beta Pruning
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    private int evaluateBoard(Game game) {
        //da implementare i bonus
        int pawnScore = 100;
        int knightScore = 320;
        int bishopScore = 330;
        int rookScore = 500;
        int queenScore = 900;
        int kingScore = 20000;
        int score = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece p = game.board[i][j];
                if(p == null) {continue;}
                if(p.team == starter) {
                    if(p.type == Type.pawn){
                        score+=pawnScore;
                    }else if(p.type == Type.knight){
                        score+=knightScore;
                    }else if(p.type == Type.bishop){
                        score+=bishopScore;
                    }else if(p.type == Type.rook){
                        score+=rookScore;
                    }else if(p.type == Type.queen){
                        score+=queenScore;
                    }else if(p.type == Type.king){
                        score+=kingScore;
                    }
                }else{
                    if(p.type == Type.pawn){
                        score-=pawnScore;
                    }else if(p.type == Type.knight){
                        score-=knightScore;
                    }else if(p.type == Type.bishop){
                        score-=bishopScore;
                    }else if(p.type == Type.rook){
                        score-=rookScore;
                    }else if(p.type == Type.queen){
                        score-=queenScore;
                    }else if(p.type == Type.king){
                        score-=kingScore;
                    }
                }
            }
        }
        return score;
    }

    private List<Move> generateLegalMoves(Game game, boolean team) {
        List<Move> moves = new ArrayList<>();

        // 1. Scorro la scacchiera per trovare i miei pezzi
        for (int startRow = 0; startRow < 8; startRow++) {
            for (int startCol = 0; startCol < 8; startCol++) {
                Piece piece = game.board[startRow][startCol];

                // Se c'è un pezzo ed è della squadra che sta cercando le mosse
                if (piece != null && piece.team == team) {

                    // 2. Controllo tutte le possibili caselle di destinazione
                    for (int endRow = 0; endRow < 8; endRow++) {
                        for (int endCol = 0; endCol < 8; endCol++) {

                            // Se il movimento geometrico è consentito per quel pezzo
                            if (game.isValidMove(piece, endRow, endCol)) {

                                Piece capturedPiece = game.board[endRow][endCol];
                                Move pseudoLegalMove = new Move(startRow, startCol, endRow, endCol, piece, capturedPiece);

                                // 3. Simulo la mossa per vedere se mi metto sotto scacco da solo
                                game.makeMove(pseudoLegalMove);

                                if (!game.isCheck(team)) {
                                    // Se il Re è al sicuro, la mossa è legale al 100%
                                    moves.add(pseudoLegalMove);
                                }

                                // 4. Annulla la simulazione per pulire la scacchiera per il prossimo ciclo
                                game.undoMove(pseudoLegalMove);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}