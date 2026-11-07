public class Game {
    // Rimosso "static" così l'AI potrà creare copie della partita per simulare le mosse
    public Piece[][] board;
    public boolean st;
    public AI AI;
    public RealPlayer player;

    public Game(boolean starter, AI AI, RealPlayer player) { //true -> AI white | false -> AI black
        this.st = starter;
        this.AI = AI;
        this.player = player;
        initializeBoard(starter);
    }

    public boolean move(String piece_position, String pos, Player player) {
        int[] start = convertToPosition(piece_position);
        int[] end = convertToPosition(pos);

        if (!isInsideBoard(start[0], start[1]) || !isInsideBoard(end[0], end[1])) {
            return false;
        }

        Piece piece = board[start[0]][start[1]];

        // Controllo che la casella di partenza non sia vuota e appartenga al giocatore
        if (piece == null) {
            return false;
        }
        if (player != null && piece.team != player.starter) {
            return false;
        }

        int targetRow = end[0];
        int targetCol = end[1];

        // 1. Gestione speciale: Arrocco (il Re si muove di 2 colonne)
        if (piece.type == Type.king && piece.row == targetRow && Math.abs(targetCol - piece.column) == 2) {
            return tryCastling(piece, targetRow, targetCol);
        }

        // 2. Controllo se il movimento geometrico del pezzo è valido
        if (!isValidMove(piece, targetRow, targetCol)) {
            return false;
        }

        // 3. Simulo la mossa per verificare che non lasci il proprio Re sotto scacco
        int oldRow = piece.row;
        int oldCol = piece.column;
        Piece capturedPiece = board[targetRow][targetCol];

        // Eseguo lo spostamento temporaneo
        board[oldRow][oldCol] = null;
        board[targetRow][targetCol] = piece;
        piece.row = targetRow;
        piece.column = targetCol;

        // Se dopo la mossa il mio Re è sotto scacco, annullo tutto (mossa illegale!)
        if (isCheck(piece.team)) {
            board[oldRow][oldCol] = piece;
            board[targetRow][targetCol] = capturedPiece;
            piece.row = oldRow;
            piece.column = oldCol;
            return false;
        }

        // 4. La mossa è legale: confermo stato e controllo promozione del pedone
        piece.doneFirstmove();
        if (piece.type == Type.pawn && (targetRow == 0 || targetRow == 7)) {
            piece.type = Type.queen; // Promozione automatica a Regina
        }

        return true;
    }

    // Verifica se un pezzo può andare in (targetRow, targetCol) senza spostarlo (RICORDATI DI MODIFICARLO IN BASE ALLA CLASSE MOVE)
    public boolean isValidMove(Piece piece, int targetRow, int targetCol) {
        // Non posso muovermi sulla stessa casella in cui mi trovo
        if (piece.row == targetRow && piece.column == targetCol) {
            return false;
        }

        // Non posso mangiare un pezzo della mia stessa squadra
        if (isTileOccupied(targetRow, targetCol) && board[targetRow][targetCol].team == piece.team) {
            return false;
        }

        int rowDiff = targetRow - piece.row;
        int colDiff = targetCol - piece.column;
        int absRow = Math.abs(rowDiff);
        int absCol = Math.abs(colDiff);

        return switch (piece.type) {
            case pawn -> isValidPawnMove(piece, targetRow, targetCol, rowDiff, absCol);
            case rook -> (rowDiff == 0 || colDiff == 0) && isPathClear(piece.row, piece.column, targetRow, targetCol);
            case bishop -> (absRow == absCol) && isPathClear(piece.row, piece.column, targetRow, targetCol);
            case queen -> (rowDiff == 0 || colDiff == 0 || absRow == absCol) && isPathClear(piece.row, piece.column, targetRow, targetCol);
            case knight -> (absRow == 2 && absCol == 1) || (absRow == 1 && absCol == 2);
            case king -> absRow <= 1 && absCol <= 1;
        };
    }

    private boolean isValidPawnMove(Piece piece, int targetRow, int targetCol, int rowDiff, int absCol) {
        // Determina la direzione in base a chi è stato posizionato in basso (riga 6) o in alto (riga 1)
        // Se st == true, il team true parte dal basso (sale: -1), altrimenti scende (+1)
        int dir = (piece.team == st) ? -1 : 1;

        // Movimento in avanti (stessa colonna, NON può catturare)
        if (absCol == 0) {
            if (rowDiff == dir && !isTileOccupied(targetRow, targetCol)) {
                return true;
            }
            if (piece.isTheFirstMove() && rowDiff == 2 * dir
                    && !isTileOccupied(piece.row + dir, targetCol)
                    && !isTileOccupied(targetRow, targetCol)) {
                return true;
            }
            return false;
        }

        // Cattura in diagonale (1 colonna di lato e 1 riga in avanti)
        if (absCol == 1 && rowDiff == dir) {
            return isTileOccupied(targetRow, targetCol) && board[targetRow][targetCol].team != piece.team;
        }

        return false;
    }

    // Controlla che tutte le caselle intermedie (esclusa la destinazione) siano vuote
    private boolean isPathClear(int startRow, int startCol, int targetRow, int targetCol) {
        int stepRow = Integer.compare(targetRow, startRow);
        int stepCol = Integer.compare(targetCol, startCol);

        int currRow = startRow + stepRow;
        int currCol = startCol + stepCol;

        while (currRow != targetRow || currCol != targetCol) {
            if (isTileOccupied(currRow, currCol)) {
                return false; // C'è un pezzo in mezzo!
            }
            currRow += stepRow;
            currCol += stepCol;
        }
        return true;
    }

    // Gestione dell'Arrocco (corto e lungo)
    private boolean tryCastling(Piece king, int targetRow, int targetCol) {
        if (!king.isTheFirstMove() || isCheck(king.team)) {
            return false;
        }

        int rookCol = (targetCol > king.column) ? 7 : 0; // 7 = arrocco corto, 0 = arrocco lungo
        int rookTargetCol = (targetCol > king.column) ? targetCol - 1 : targetCol + 1;
        Piece rook = board[king.row][rookCol];

        if (rook == null || rook.type != Type.rook || rook.team != king.team || !rook.isTheFirstMove()) {
            return false;
        }

        // Il percorso tra Re e Torre deve essere completamente libero
        if (!isPathClear(king.row, king.column, king.row, rookCol)) {
            return false;
        }

        // Il Re non può attraversare una casella controllata dal nemico né finire sotto scacco
        int step = (targetCol > king.column) ? 1 : -1;
        int startCol = king.column;

        for (int col = startCol + step; ; col += step) {
            board[king.row][king.column] = null;
            board[king.row][col] = king;
            king.column = col;

            if (isCheck(king.team)) {
                // Ripristina il Re se passa o finisce sotto scacco
                board[king.row][col] = null;
                board[king.row][startCol] = king;
                king.column = startCol;
                return false;
            }
            if (col == targetCol) break;
        }

        // Sposto anche la Torre e segno che si sono mossi
        board[rook.row][rookCol] = null;
        board[rook.row][rookTargetCol] = rook;
        rook.column = rookTargetCol;

        king.doneFirstmove();
        rook.doneFirstmove();
        return true;
    }

    // Controlla se il Re della squadra "team" è attualmente sotto scacco
    public boolean isCheck(boolean team) {
        int kingRow = -1, kingCol = -1;

        // 1. Trova le coordinate del Re
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.team == team && p.type == Type.king) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }

        // Se per qualche motivo nei test non c'è il Re, non è sotto scacco
        if (kingRow == -1) return false;

        // 2. Controlla se almeno un pezzo avversario può catturare la casella del Re
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.team != team) {
                    if (isValidMove(p, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Esegue la mossa per l'AI
    public void makeMove(Move move) {
        board[move.startRow][move.startCol] = null;
        board[move.endRow][move.endCol] = move.movedPiece;
        move.movedPiece.row = move.endRow;
        move.movedPiece.column = move.endCol;
        move.movedPiece.doneFirstmove();

        // (In futuro qui gestiremo anche la promozione del pedone per l'AI)
    }

    // Annulla la mossa per l'AI (FONDAMENTALE)
    public void undoMove(Move move) {
        board[move.startRow][move.startCol] = move.movedPiece;
        board[move.endRow][move.endCol] = move.capturedPiece; // Ripristina il pezzo mangiato (o null)
        move.movedPiece.row = move.startRow;
        move.movedPiece.column = move.startCol;

        // Ripristina lo stato della prima mossa
        move.movedPiece.setFirstMove(move.wasFirstMove);
    }

    // Mantenuto per retrocompatibilità col tuo codice
    public boolean isCheck(Player player) {
        return isCheck(player.starter);
    }

    private int[] convertToPosition(String pos) {
        int col = Character.toLowerCase(pos.charAt(0)) - 'a';
        int row = 8 - Character.getNumericValue(pos.charAt(1));
        return new int[]{row, col};
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private boolean isTileOccupied(int row, int col) {
        return board[row][col] != null;
    }

    private void initializeBoard(boolean starter) {
        board = new Piece[8][8];
        // Se starter è true: i pezzi con team=true sono in basso (righe 6-7) e team=false in alto (righe 0-1)
        boolean topTeam = !starter;
        boolean bottomTeam = starter;

        // Pedoni (corretto il bug di char e righe)
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Piece(i, 1, Type.pawn, topTeam);
            board[6][i] = new Piece(i, 6, Type.pawn, bottomTeam);
        }

        // Pezzi superiori (riga 0)
        board[0][0] = new Piece(0, 0, Type.rook, topTeam);
        board[0][1] = new Piece(1, 0, Type.knight, topTeam);
        board[0][2] = new Piece(2, 0, Type.bishop, topTeam);
        board[0][3] = new Piece(3, 0, Type.queen, topTeam);
        board[0][4] = new Piece(4, 0, Type.king, topTeam);
        board[0][5] = new Piece(5, 0, Type.bishop, topTeam);
        board[0][6] = new Piece(6, 0, Type.knight, topTeam);
        board[0][7] = new Piece(7, 0, Type.rook, topTeam);

        // Pezzi inferiori (riga 7)
        board[7][0] = new Piece(0, 7, Type.rook, bottomTeam);
        board[7][1] = new Piece(1, 7, Type.knight, bottomTeam);
        board[7][2] = new Piece(2, 7, Type.bishop, bottomTeam);
        board[7][3] = new Piece(3, 7, Type.queen, bottomTeam);
        board[7][4] = new Piece(4, 7, Type.king, bottomTeam);
        board[7][5] = new Piece(5, 7, Type.bishop, bottomTeam);
        board[7][6] = new Piece(6, 7, Type.knight, bottomTeam);
        board[7][7] = new Piece(7, 7, Type.rook, bottomTeam);
    }
}