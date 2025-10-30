public class Game {
    public static Piece[][] board;
    public boolean st;
    public Player player1;
    public Player player2;
    public Game (boolean starter, Player player1, Player player2) {// true  => the AI starts first, false => the enemy starts first
        st = starter;
        this.player1 = player1;
        this.player2 = player2;
        initializeBoard(starter);
    }
    public boolean move(String piece_position, String pos,Player player) {

        int[] array = convertToPosition(piece_position);
        Piece piece =  board[array[0]][array[1]];
        array = convertToPosition(pos);
        int row = array[0], col = array[1];
        switch (piece.type) {
            case  pawn -> {
                if (piece.isTheFirstMove()) {
                    //Verso su
                    if (row == piece.row - 2) {
                        board[piece.row][piece.column] = null;
                        piece.row = row;
                        board[row][piece.column] = piece;
                        piece.doneFirstmove();
                        return true;
                    } else if (row == piece.row - 1) {
                        board[piece.row][piece.column] = null;
                        piece.row = row;
                        board[row][piece.column] = piece;
                        piece.doneFirstmove();
                        return true;
                    } else {
                        return false;
                    }
                } else if (row == piece.row - 1) {
                    board[piece.row][piece.column] = null;
                    piece.row = row;
                    board[row][piece.column] = piece;
                    return true;
                } else {
                    return false;
                }
            }
            case rook -> {
                if(row == piece.row || col == piece.column){
                    return rookMove(piece, row, col, player);
                }else return false;
            }
            case bishop -> {
                int col_control = piece.column;
                Piece target = board[row][col];

                //Controllo se la casella che voglio raggiungere e' vuota e se riesco a raggiungerla seguendo il mio percorso
                if(Math.abs(row - piece.row) == Math.abs(col - piece.column)){
                    return bishopMove(piece, row, col, col_control, target);
                }
                else return false;
            }
            case knight -> {}
            case queen -> {
                int col_control = piece.column;
                Piece target = board[row][col];
                if(row == piece.row || col == piece.column) return rookMove(piece, row, col, player);
                else return bishopMove(piece, row, col, col_control, target);
            }
            case king -> { // ARROCCO da mettere
                if(!isCheck(player)){

                }
            }
            }
            return false;
        }//fine move(...)

    //Il metodo ritorna un array che converte la posizione da stringa
    //Esempio
    // Input  : "a1"
    // Output : new int[]{7, 0} row, col
    private int[] convertToPosition(String pos){
        int col = pos.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(pos.charAt(1));
        return new int[]{row, col};
    }
    private void movePiece(Piece piece, int row, int col){
        board[piece.row][piece.column] = null;
        board[row][col] = piece;
        piece.row = row;
        piece.column = col;
    }
    private boolean isTileOccupied(int row, int col){
        return board[row][col] != null;
    }

    boolean isCheck(Player player){
        return false;
    }

    private boolean isTargetAvailable(Piece piece, Piece target, int row, int col){
        if(board[row][col]==target){
            //se non e' il mio alleato lo mangio
            if(target == null ||target.team != piece.team){
                return true;
            } else return false;
        } else return false;
    }

private boolean rookMove(Piece piece, int row, int col, Player player){
    if(row != piece.row){
        //edit row
        int firstRow = piece.row;
        if(row>piece.row){
            //edit vertically
            for (int i = piece.row+1; i <= row; i++) {
                piece.row = i;
                if(isTileOccupied(i,piece.column)) {
                    if(board[i][piece.column].team==piece.team)
                        return false;
                    break;
                }
            }
            if(piece.row != row) return false;
            board[firstRow][piece.column] = null;
            piece.row = row;
            board[row][piece.column] = piece;
            return true;
        }else{
            for (int i = piece.row-1; i >= row; i--) {
                piece.row = i;
                if(isTileOccupied(i,piece.column)) {
                    if(board[i][piece.column].team==piece.team)
                        return false;
                    break;
                }
            }
            if(piece.row != row) return false;
            board[firstRow][piece.column] = null;
            piece.row = row;
            board[row][piece.column] = piece;
            return true;
        }
    }else if(col != piece.column){
        //edit column
        int firstCol = piece.column;
        if(col>piece.column){
            //right rook
            for (int i = piece.column+1; i <= col; i++) {
                piece.column = i;
                if(isTileOccupied(piece.row, i)) {
                    if(board[piece.row][i].team==piece.team)
                        return false;
                    break;
                }
            }
            if(piece.column != col) return false;
            board[piece.row][firstCol] = null;
            piece.column = col;
            board[piece.row][piece.column] = piece;
            return true;
        }else{
            //left rook
            for (int i = piece.column-1; i >= col; i--) {
                piece.column = i;
                if(isTileOccupied(piece.row, i)) {
                    if(board[piece.row][i].team==piece.team)
                        return false;
                    break;
                }
            }
            if(piece.column != col) return false;
            board[piece.row][firstCol] = null;
            piece.column = col;
            board[piece.row][piece.column] = piece;
            return true;
        }
    }else return false;
}
private boolean bishopMove(Piece piece, int row, int col, int col_control, Piece target){
    //Vado verso l'alto
    if(row < piece.row){
        for (int i = piece.row - 1; i >= row; i--) {
            //vado a sinistra di colonna
            if(col < col_control){
                if(!isTileOccupied(i, --col_control) && isTargetAvailable(piece, target, row, col)){
                    movePiece(piece, row, col);
                    return true;
                }
                return false;
            }
            //vado a destra di colonna
            else if(col_control < col){
                System.out.printf("%d, %d\n",i, col_control+1);
                System.out.println("Va bene? : " + (!isTileOccupied(i, col_control + 1)));
                if(!isTileOccupied(i, ++col_control) && isTargetAvailable(piece, target, row, col)) {
                    movePiece(piece, row, col);
                    return true;
                }

                System.out.println("Non funziona");
                return false;
            }
        }
    }
    //Vado verso il basso
    else if(row > piece.row){
        for (int i = piece.row + 1; i <= row; i++) {
            //left column check
            if(col_control > col){
                if(!isTileOccupied(i, --col_control) && isTargetAvailable(piece, target, row, col)){
                    movePiece(piece, row, col);
                    return true;
                }
                return false;
            }
            else if(col_control < col){
                //right column check
                if(!isTileOccupied(i, ++col_control) && isTargetAvailable(piece, target, row, col)) {
                    movePiece(piece, row, col);
                    return true;
                }
                return false;
            }
        }
    }
    //Se non ha dato errori allora posso spostarlo
    movePiece(piece, row, col);
    return true;
}
    private void initializeBoard(boolean starter) {
        board = new Piece[8][8];
        if(starter) {
            // add pawns
            for (int i = 0; i < 8; i++) {
                board[1][i] = new Piece(i,1,Type.pawn,false);
                board[6][i] = new Piece(i,6,Type.pawn,true);
            }
            // add others black
            board[0][0] = new Piece(0,0,Type.rook,false);
            board[0][1] = new Piece(1,0,Type.knight,false);
            board[0][2] = new Piece(2,0,Type.bishop,false);
            board[0][3] = new Piece(3,0,Type.queen,false);
            board[0][4] = new Piece(4,0,Type.king,false);
            board[0][5] = new Piece(5,0,Type.bishop,false);
            board[0][6] = new Piece(6,0,Type.knight,false);
            board[0][7] = new Piece(7,0,Type.rook,false);
            // add others white
            board[7][0] = new Piece(0,7,Type.rook,true);
            board[7][1] = new Piece(1,7,Type.knight,true);
            board[7][2] = new Piece(2,7,Type.bishop,true);
            board[7][3] = new Piece(3,7,Type.queen,true);
            board[7][4] = new Piece(4,7,Type.king,true);
            board[7][5] = new Piece(5,7,Type.bishop,true);
            board[7][6] = new Piece(6,7,Type.knight,true);
            board[7][7] = new Piece(7,7,Type.rook,true);

        }else{
            // add pawns
            for (int i = 0; i < 8; i++) {
                board[1][i] = new Piece((char) (97 + i),1,Type.pawn,true);
                board[6][i] = new Piece((char) (97 + i),1,Type.pawn,false);
            }
            // add others white
            board[0][0] = new Piece(0,0,Type.rook,true);
            board[0][1] = new Piece(1,0,Type.knight,true);
            board[0][2] = new Piece(2,0,Type.bishop,true);
            board[0][3] = new Piece(3,0,Type.queen,true);
            board[0][4] = new Piece(4,0,Type.king,true);
            board[0][5] = new Piece(5,0,Type.bishop,true);
            board[0][6] = new Piece(6,0,Type.knight,true);
            board[0][7] = new Piece(7,0,Type.rook,true);
            // add others black
            board[7][0] = new Piece(0,7,Type.rook,false);
            board[7][1] = new Piece(1,7,Type.knight,false);
            board[7][2] = new Piece(2,7,Type.bishop,false);
            board[7][3] = new Piece(3,7,Type.queen,false);
            board[7][4] = new Piece(4,7,Type.king,false);
            board[7][5] = new Piece(5,7,Type.bishop,false);
            board[7][6] = new Piece(6,7,Type.knight,false);
            board[7][7] = new Piece(7,7,Type.rook,false);
        }
    }
}
