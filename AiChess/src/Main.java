// Le tue classi Player, Game, Piece, ecc. andrebbero qui...

void main(String[] args) {
    Player player1 = new Player("test1");
    Player player2 = new Player("test2");
    Game game = new Game(true, player1, player2);
    printBoard(game);
    // System.out.println(game.board[6][2].charPiece());
    // Piece prova = game.board[6][2]; // Questo ora verrebbe gestito da "c2"
    rookTest(game, player1);
    // bishopTest(game, player1);
}

/**
 * Converte la notazione scacchistica (es. "a1") in coordinate
 * di un array 0-indicizzato [riga, colonna].
 * Questo metodo verrebbe ora usato DENTRO la tua classe Game,
 * nel nuovo metodo move(String fromPos, String toPos, Player player).
 */
private int[] convertToPosition(String pos) {
    // Corretto come da nostra discussione precedente
    int col = pos.charAt(0) - 'a';
    int row = 8 - Character.getNumericValue(pos.charAt(1));
    return new int[]{row, col};
}

void bishopTest(Game game, Player player) {
    // Sblocco l'alfiere dal pedone
    System.out.println(game.move("b2", "b4", player));
    printBoard(game);

    // Muovo l'alfiere
    System.out.println(game.move("c1", "b2", player));
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
    System.out.println(" |A|B|C|D|E|F|G|H");
    System.out.println();
}