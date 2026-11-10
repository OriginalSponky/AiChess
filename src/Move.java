public class Move {
    public int startRow, startCol, endRow, endCol;
    public Piece movedPiece;
    public Piece capturedPiece;
    public boolean wasFirstMove; // Salva lo stato della prima mossa
    public boolean wasPromoted = false;
    public Type promotionType = null; // Se è != null, l'AI ha scelto in cosa promuovere
    public boolean isCastling = false; // Se è true, sappiamo che va mossa anche la Torre

    public Move(int startRow, int startCol, int endRow, int endCol, Piece movedPiece, Piece capturedPiece) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.wasFirstMove = movedPiece.isTheFirstMove();
    }
}