public class Move {
    public int startRow, startCol, endRow, endCol;
    public Piece movedPiece;
    public Piece capturedPiece;
    public boolean wasFirstMove; // Salva lo stato della prima mossa

    public Move(int startRow, int startCol, int endRow, int endCol, Piece movedPiece, Piece capturedPiece) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.wasFirstMove = movedPiece.isTheFirstMove(); // Salva se era la sua prima mossa
    }
}