public class Piece {
    int column;
    int row;
    Type type;
    boolean team; // true -> white , false -> black
    //boolean alive;
    private boolean isTheFirstMove = false;

    Piece() {
    }

    Piece(int column, int row, Type type, boolean team) {
        this.column = column;
        this.row = row;
        this.type = type;
        this.team = team;
        this.isTheFirstMove = true; // Utile per Pedoni, Re e Torri (arrocco)
    }

    public boolean isTheFirstMove() {
        return isTheFirstMove;
    }

    public void setFirstMove(boolean state) {
        this.isTheFirstMove = state;
    }

    public void doneFirstmove(){
        isTheFirstMove = false;
    }
    public String charPiece() {
        if (team) {
            switch (type) {
                case king -> {
                    return "♚";
                }
                case queen -> {
                    return "♛";
                }
                case rook -> {
                    return "♜";
                }
                case knight -> {
                    return "♞";
                }
                case bishop -> {
                    return "♝";
                }
                case pawn -> {
                    return "♟";
                }
            }
        } else {
            switch (type) {
                case king -> {
                    return "♔";
                }
                case queen -> {
                    return "♕";
                }
                case rook -> {
                    return "♖";
                }
                case knight -> {
                    return "♗";
                }
                case bishop -> {
                    return "♘";
                }
                case pawn -> {
                    return "♙";
                }
            }
        }
        return " ";
    }
}
