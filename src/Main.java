import java.util.Scanner;

// Le tue classi Player, Game, Piece, ecc. andrebbero qui...
public class Main {
    public void main(String[] str) {
        AI AI = new AI("test1", true);
        RealPlayer player = new RealPlayer("test2", false);
        Game game = new Game(true, AI, player);
        //printBoard(game);
        // System.out.println(game.board[6][2].charPiece());
        // Piece prova = game.board[6][2]; // Questo ora verrebbe gestito da "c2"
        //rookTest(game, player1);
        //bishopTest(game, player1);
        //queenTest(game, player1);
        //playVsAI();
        playAIVsAI();
    }

    public void playVsAI() {
        Scanner scanner = new Scanner(System.in);

        // L'AI è il Bianco (true), tu sei il Nero (false)
        AI ai = new AI("Computer", true);
        RealPlayer human = new RealPlayer("Umano", false);

        // Passiamo l'AI (Bianco) e l'Umano (Nero) alla classe Game
        Game game = new Game(true, ai, human);

        // Inizia il Bianco (cioè l'AI)
        boolean isHumanTurn = false;

        System.out.println("=== PARTITA INIZIATA ===");
        System.out.println("L'AI è il Bianco (in basso) e inizia per prima.");
        System.out.println("Tu sei il Nero (in alto).");
        System.out.println("Inserisci le mosse nel formato: partenza destinazione (es. e7 e5)");

        while (true) {
            printBoard(game);

            if (isHumanTurn) {
                System.out.println("Tuo turno (Nero). Inserisci la mossa: ");
                String input = scanner.nextLine().trim().toLowerCase();

                String[] parts = input.split(" ");
                if (parts.length != 2) {
                    System.out.println("Formato non valido! Riprova con: e7 e5");
                    continue;
                }

                boolean success = game.move(parts[0], parts[1], human);

                if (!success) {
                    System.out.println("Mossa illegale o Re sotto scacco! Riprova.");
                    continue;
                }
            } else {
                System.out.println("L'AI sta pensando...");

                Move aiMove = ai.getBestMove(game, 3); // Cerca 3 mosse nel futuro

                if (aiMove == null) {
                    if (game.isCheck(true)) {
                        System.out.println("SCACCO MATTO! Hai vinto!");
                    } else {
                        System.out.println("STALLO! La partita finisce in parità.");
                    }
                    break;
                }

                game.makeMove(aiMove);

                String startStr = "" + (char) ('a' + aiMove.startCol) + (8 - aiMove.startRow);
                String endStr = "" + (char) ('a' + aiMove.endCol) + (8 - aiMove.endRow);
                System.out.println("L'AI ha giocato: " + startStr + " " + endStr);
            }

            // Cambia turno
            isHumanTurn = !isHumanTurn;
        }
        scanner.close();
    }
    public void playAIVsAI() {
        AI aiWhite = new AI("AI Bianca", true);
        AI aiBlack = new AI("AI Nera", false);

        // Il RealPlayer non serve in questa modalità, possiamo passare null
        Game game = new Game(true, aiWhite, null);

        boolean isWhiteTurn = true;

        System.out.println("=== BATTAGLIA AI vs AI INIZIATA ===");

        while (true) {
            printBoard(game);

            AI currentAI = isWhiteTurn ? aiWhite : aiBlack;
            System.out.println("Sta pensando " + currentAI.name + "...");

            // Profondità 3 è istantanea, puoi provare 4 per vederle ragionare meglio
            Move aiMove = currentAI.getBestMove(game, 4);

            if (aiMove == null) {
                if (game.isCheck(currentAI.starter)) {
                    System.out.println("SCACCO MATTO! Vince: " + (isWhiteTurn ? "AI Nera" : "AI Bianca"));
                } else {
                    System.out.println("STALLO! La partita finisce in parità.");
                }
                break;
            }

            // Esegue la mossa vera e propria (non una simulazione) usando il metodo testuale per avere tutti i controlli finali
            String startPos = "" + (char)('a' + aiMove.startCol) + (8 - aiMove.startRow);
            String endPos = "" + (char)('a' + aiMove.endCol) + (8 - aiMove.endRow);

            // Usiamo il move testuale del Game per eseguire la mossa finale confermata
            game.move(startPos, endPos, currentAI);

            System.out.println(currentAI.name + " ha giocato: " + startPos + " " + endPos);

            // Pausa di 1 secondo (1000 millisecondi) per farti leggere la mossa
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isWhiteTurn = !isWhiteTurn;
        }
    }

    public void printBoard(Game game) {
        String row = "";
        for (int i = 0; i < 8; i++) {
            row += "" + (8 - i) + " ";
            for (int j = 0; j < 8; j++) {
                if (game.board[i][j] == null)
                    row += " ䷀"; // Carattere spazio vuoto
                else
                    row += "|"+ game.board[i][j].charPiece();
            }

            System.out.println(row);
            row = "";
        }
        System.out.println("   A  B C  D  E F  G  H");
        System.out.println();
    }

    public void createGame() {
        System.out.println("Creating a new game...");
        System.out.println("select the mode with numbers");
        System.out.println("1. one vs one");
        System.out.println("2. you vs AI");
        System.out.println("3. AI vs AI");
        System.out.println("4. AI advice");
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        String choose = scanner.nextLine();
        System.out.println("enter your choice");
        while (choose.equals("1") || choose.equals("2") || choose.equals("3") || choose.equals("4")) {
            choose = scanner.nextLine();
            System.out.println("enter your correct choice pls (1,2,3,4)");
        }
        switch (choose) {
            case "1" -> {
            }
            case "2" -> {
            }
            case "3" -> {
            }
            case "4" -> {
            }
            default -> System.out.println("Invalid choice");
        }
    }
}