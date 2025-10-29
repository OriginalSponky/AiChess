void main(String[] args) {
    Player player1 = new Player("test1");
    Player player2 = new Player("test2");
    Game game = new Game(true, player1, player2);
    printBoard(game);
    //System.out.println(game.board[6][2].charPiece());
    Piece prova = game.board[6][2];
    rookTest(game,player1);
    //bishopTest(game);

}


void bishopTest(Game game, Player player) {
//    //Addosso agli alleati
//    System.out.println(game.move(game.board[7][2],6, 1));
//    printBoard(game);
//    System.out.println(game.move(game.board[7][2],6, 3));
//    printBoard(game);
    //Sblocco l'alfiere dal pedone
    System.out.println(game.move(game.board[6][1],4, 1, player));
    printBoard(game);

    //Muovo l'alfiere
    System.out.println(game.move(game.board[7][2],6, 1, player));
    printBoard(game);
    System.out.println(game.move(game.board[6][1],1, 6, player));
    printBoard(game);
    System.out.println(game.move(game.board[1][6],0, 5, player));
    printBoard(game);
    System.out.println(game.move(game.board[0][5],1, 6, player));
    printBoard(game);
    System.out.println(game.move(game.board[1][6],0, 7, player));
    printBoard(game);
    System.out.println(game.move(game.board[0][7],1, 6, player));
    printBoard(game);
}

void rookTest(Game game,Player player) {
    //testa di mangiare verso destra e verso giu e verso sinitra
    System.out.println(game.move(game.board[6][0],4,0,player));
    printBoard(game);
    System.out.println(game.move(game.board[4][0],3,0,player));
    printBoard(game);
    System.out.println(game.move(game.board[7][0],4,0,player));
    printBoard(game);
    System.out.println(game.move(game.board[4][0],4,3,player));
    printBoard(game);
    System.out.println(game.move(game.board[4][3],1,3,player));
    printBoard(game);
    System.out.println(game.move(game.board[1][3],0,3,player));
    printBoard(game);
    System.out.println(game.move(game.board[0][3],0,2,player));
    printBoard(game);
    System.out.println(game.move(game.board[0][2],1,2,player));
    printBoard(game);
    System.out.println(game.move(game.board[1][2],1,3,player));
    printBoard(game);
    System.out.println(game.move(game.board[1][3],1,2,player));
    printBoard(game);
    System.out.println(game.move(game.board[1][2],1,1,player));
    printBoard(game);
    System.out.println(game.move(game.board[1][1],1,0,player));
    printBoard(game);
    System.out.println(game.move(game.board[1][0],3,0,player));
    printBoard(game);
    System.out.println(game.move(game.board[3][0],3,0,player));
    printBoard(game);
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

