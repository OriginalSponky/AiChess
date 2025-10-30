// Le tue classi Player, Game, Piece, ecc. andrebbero qui...

void main() {
    Player player1 = new RealPlayer("test1",true);
    Player player2 = new RealPlayer("test2",false);
    Game game = new Game(true, player1, player2);
    printBoard(game);
    // System.out.println(game.board[6][2].charPiece());
    // Piece prova = game.board[6][2]; // Questo ora verrebbe gestito da "c2"
    //rookTest(game, player1);
//    bishopTest(game, player1);
    queenTest(game, player1);
}
void bishopTest(Game game, Player player) {
    // Sblocco l'alfiere dal pedone
    System.out.println(game.move("b2", "b4", player)); //Muove il pedone di 2 caselle
    printBoard(game);

    // Muovo l'alfiere
    System.out.println(game.move("c1", "b2", player)); //Alfiere va in alto a sinistra
    printBoard(game);
    System.out.println(game.move("b2", "g7", player));
    printBoard(game);
    System.out.println(game.move("g7", "f8", player));
    printBoard(game);
    System.out.println(game.move("f8", "g7", player));
    printBoard(game);
    System.out.println(game.move("g7", "h8", player));
    printBoard(game);
    System.out.println(game.move("h8", "g7", player));
    printBoard(game);
}

void rookTest(Game game, Player player) {
    // testa di mangiare verso destra e verso giu e verso sinitra
    System.out.println(game.move("a2", "a4", player));
    printBoard(game);
    System.out.println(game.move("a4", "a5", player));
    printBoard(game);
    System.out.println(game.move("a1", "a4", player));
    printBoard(game);
    System.out.println(game.move("a4", "d4", player));
    printBoard(game);
    System.out.println(game.move("d4", "d7", player));
    printBoard(game);
    System.out.println(game.move("d7", "d8", player));
    printBoard(game);
    System.out.println(game.move("d8", "c8", player));
    printBoard(game);
    System.out.println(game.move("c8", "c7", player));
    printBoard(game);
    System.out.println(game.move("c7", "d7", player));
    printBoard(game);
    System.out.println(game.move("d7", "c7", player));
    printBoard(game);
    System.out.println(game.move("c7", "b7", player));
    printBoard(game);
    System.out.println(game.move("b7", "a7", player));
    printBoard(game);
    System.out.println(game.move("a7", "a5", player));
    printBoard(game);
    System.out.println(game.move("a5", "a5", player)); // Mossa nulla
    printBoard(game);
}

void queenTest(Game game, Player player) {
        // not work good nega in diagonals
    System.out.println(game.move("d2", "d4", player)); // Muove il pedone di 2 avanti
    printBoard(game);
    System.out.println(game.move("d1", "d3", player)); // Muove la regina e lo posiziona dietro il pedone
    printBoard(game);
//    System.out.println(game.move("d3", "d1", player)); // Riposiziona la regina alla sua posizione iniziale
    printBoard(game);
    System.out.println(game.move("d3", "f3", player)); // Sposta la regina a destra
    printBoard(game);
    System.out.println(game.move("f3", "f5", player)); // Mossa nulla
    printBoard(game);
    System.out.println(game.move("f5", "g6", player)); // Mossa nulla
    printBoard(game);
}

public void printBoard(Game game) {
    String row = "";
    for (int i = 0; i < 8; i++) {
        row += "" + (8 - i) + " ";
        for (int j = 0; j < 8; j++) {
            if (game.board[i][j] == null)
                row += '䷀'; // Carattere spazio vuoto
            else
                row += game.board[i][j].charPiece() + "";
        }

        System.out.println(row);
        row = "";
    }
    System.out.println(" |A|B|CD|E|FG|H");
    System.out.println();
}