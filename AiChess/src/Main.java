//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    Game game = new Game(true);
    printBoard(game);
    //System.out.println(game.board[6][2].charPiece());
    Piece prova = game.board[6][2];
//    rookTest(game);
    bishopTest(game);

}


void bishopTest(Game game) {
//    //Addosso agli alleati
//    System.out.println(game.move(game.board[7][2],6, 1));
//    printBoard(game);
//    System.out.println(game.move(game.board[7][2],6, 3));
//    printBoard(game);
    //Sblocco l'alfiere dal pedone
    System.out.println(game.move(game.board[6][1],4, 1));
    printBoard(game);

    //Muovo l'alfiere
    System.out.println(game.move(game.board[7][2],6, 1));
    printBoard(game);
    System.out.println(game.move(game.board[6][1],1, 6));
    printBoard(game);
    System.out.println(game.move(game.board[1][6],0, 5));
    printBoard(game);
    System.out.println(game.move(game.board[0][5],1, 6));
    printBoard(game);
    System.out.println(game.move(game.board[1][6],0, 7));
    printBoard(game);
    System.out.println(game.move(game.board[0][7],1, 6));
    printBoard(game);
}

void rookTest(Game game) {
    //testa di mangiare verso destra e verso giu e verso sinitra
    System.out.println(game.move(game.board[6][0],4,0));
    printBoard(game);
    System.out.println(game.move(game.board[4][0],3,0));
    printBoard(game);
    System.out.println(game.move(game.board[7][0],4,0));
    printBoard(game);
    System.out.println(game.move(game.board[4][0],4,3));
    printBoard(game);
    System.out.println(game.move(game.board[4][3],1,3));
    printBoard(game);
    System.out.println(game.move(game.board[1][3],1,4));
    printBoard(game);
//    System.out.println(game.move(game.board[3][0],3,0));
//    printBoard(game);
}

public void printBoard(Game game) {
    //System.out.println(" |A |B |C|D|E |F |G |H");
    String row = "";
    for (int i = 0; i < 8; i++) {
        row += ""+(8-i)+" ";
        for (int j = 0; j < 8; j++) {
            if (game.board[i][j] == null)
                row += '䷀';
            else
                row += game.board[i][j].charPiece()+"";
        }

        System.out.println(row);
        row = "";
    }
    //System.out.println(" |A |B |C|D|E |F |G |H");
    System.out.println();
}