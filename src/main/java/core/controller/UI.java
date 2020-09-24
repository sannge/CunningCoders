package core.controller;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UI {
    static int xRed;
    static int yRed;
    static int xBlue;
    static int yBlue;
    static int menuChoice;
    static Scanner in;
    static String redPlayer, bluePlayer;
    static GameController controller;

    public static void main(String[] args) {
        in = new Scanner(System.in);
        controller = new GameController();
        menu();
    }

    /**
     * Gets the names of users that are entered
     */
    public static void getUsers() {
        System.out.print("Enter the first user to play: ");
        redPlayer = in.nextLine();
        if (redPlayer.equals("-1"))
            menu();
        if (!controller.hasPlayerRegistered(redPlayer)) {
            System.out.println("Error: This player doesn't exist! Create a new user first.\n");
            menu();
        }
        System.out.print("Enter the next user to play: ");
        bluePlayer = in.nextLine();
        if (bluePlayer.equals("-1"))
            menu();
        if (!controller.hasPlayerRegistered(bluePlayer)) {
            System.out.println("Error: This player doesn't exist! Create a new user first.\n");
            menu();
        }
    }

    /**
     * Gets Red Player's x and y entered coordinates
     */
    public static void getInputRedPlayer() {
        // gets x coordinate
        try {
            System.out.print(redPlayer + "'s (red) x move: ");
            xRed = in.nextInt();
        } catch (InputMismatchException e) {
            in.nextLine();
            System.out.println("Please enter a number!");
            System.out.print(redPlayer + "'s (red) x move: ");
            xRed = in.nextInt();
        }
        if (xRed == -1)
            menu();
        // get y coordinate
        try {
            System.out.print(redPlayer + "'s (red) y move: ");
            yRed = in.nextInt();
        } catch (InputMismatchException e) {
            in.nextLine();
            System.out.println("Please enter a number!");
            System.out.print(redPlayer + "'s (red) y move: ");
            yRed = in.nextInt();
        }
        if (yRed == -1)
            menu();
    }

    /**
     * Gets Blue Player's x and y entered coordinates
     */
    public static void getInputBluePlayer() {
        // gets x coordinate
        try {
            System.out.print(bluePlayer + "'s (blue) x move: ");
            xBlue = in.nextInt();
        } catch (InputMismatchException e) {
            in.nextLine();
            System.out.println("Please enter a number!");
            System.out.print(bluePlayer + "'s (blue) x move: ");
            xBlue = in.nextInt();
        }
        if (xBlue == -1)
            menu();
        // get y coordinate
        try {
            System.out.print(bluePlayer + "'s (blue) y move: ");
            yBlue = in.nextInt();
        } catch (InputMismatchException e) {
                in.nextLine();
                System.out.println("Please enter a number!");
                System.out.print(bluePlayer + "'s (blue) y move: ");
                yBlue = in.nextInt();
        }
        if (yBlue == -1)
            menu();
    }

    private static void createUser() {
        System.out.print("Enter your unique username (12 characters max, -1 to return to menu): ");
        String newPlayerName = in.nextLine();
        while(!controller.registerNewPlayer(newPlayerName)) {
            System.out.print("Enter your unique username: ");
            newPlayerName = in.nextLine();
        }
        System.out.println("User successfully created! Returning to menu...");
        menu();
    }

    private static void makeNewGame() {
        System.out.println("Enter -1 at any point to go back to the main menu");
        getUsers();
        int gameId;
        System.out.print("Enter a 0 to create a public game and a 1 for a private game: ");
        Integer input = in.nextInt();
        if (input == 0) {
            gameId = controller.newPublicGame(redPlayer, bluePlayer);
        } else if (input == 1) {
            gameId = controller.newPrivateGame(redPlayer, bluePlayer);
        } else {
            System.out.println("You didn't type it in correctly so you get a public game");
            gameId = controller.newPublicGame(redPlayer, bluePlayer);
        }


        if (gameId == -7)
            menu();

        System.out.println("Your gameId is " + gameId);
        playNewGame(gameId);
    }

    /**
     * Function called after a user creates a new game. This method is
     * different from playGameStartingWithBlue and playGameStartingWithRed
     * because it gives the first player one turn first, then each
     * player gets two turns.
     * @param gameId ID of the game that is being played.
     */
    private static void playNewGame(int gameId) {
        takeFirstTurn(gameId);
        playGameStartingWithBlue(gameId);
    }

    /**
     * Method called from playNewGame method that allows the first
     * player to only take one turn on their first play.
     * @param gameId
     */
    private static void takeFirstTurn(int gameId) {
        printBoard(gameId);
        // First player gets 1 turn.
        getInputRedPlayer();
        while(!controller.makeMove(gameId, xRed, yRed, redPlayer))
            getInputRedPlayer();
        printBoard(gameId);
    }

    private static void bluePlayerTakeTurn(int gameId) {
        for (int i = 0; i < 2; i++) {
            getInputBluePlayer();
            while(!controller.makeMove(gameId, xBlue, yBlue, bluePlayer))
                getInputBluePlayer();
            //Break to cut the game right after the winning move is made.
            if (controller.checkForFinishedGame(gameId)) {
                printBoard(gameId);
                return;
            }
            printBoard(gameId);
        }
    }

    private static void redPlayerTakeTurn(int gameId) {
        for (int i = 0; i < 2; i++) {
            getInputRedPlayer();
            while(!controller.makeMove(gameId, xRed, yRed, redPlayer))
                getInputRedPlayer();
            //Break to cut the game right after the winning move is made.
            if (controller.checkForFinishedGame(gameId)) {
                printBoard(gameId);
                return;
            }
            printBoard(gameId);
        }
    }

    private static void printBoard(int gameId) {
        System.out.println("\n" + controller.reportBoard(gameId));
    }

    /**
     * Method that allows a user to join a game, and to start playing the game
     * based on who took the last turn.
     */
    private static void joinGame() {
        System.out.println("Enter the ID of a game to join");
        int gameID = in.nextInt();
        redPlayer = controller.getUserNameRed(gameID);
        bluePlayer = controller.getUserNameBlue(gameID);
        if (controller.checkForFinishedGame(gameID)) {
            System.out.println("Game already finished. Returning to main menu.");
            menu();
        }
        printBoard(gameID);
        //check to see who made last move, then start game with other player
        String currentTurnUser = controller.userCurrentTurn(gameID);

        if(currentTurnUser.equals(redPlayer)){
            if (controller.playerHasPutDownPiece(gameID, redPlayer)) {
                putDownOnePiece(gameID, redPlayer);
                playGameStartingWithBlue(gameID);
            }else
                playGameStartingWithRed(gameID);
        }
        else{
            if (controller.playerHasPutDownPiece(gameID, bluePlayer)) {
                putDownOnePiece(gameID, bluePlayer);
                playGameStartingWithRed(gameID);
            }else
                playGameStartingWithBlue(gameID);
        }

        System.out.println("Thank you for playing!\n");
        menu();
    }

    private static void putDownOnePiece(int id, String name){
        printBoard(id);
        // First player gets 1 turn.
        if (name.equalsIgnoreCase(redPlayer)) {
            getInputRedPlayer();
            while (!controller.makeMove(id, xRed, yRed, redPlayer))
                getInputRedPlayer();
        }else if (name.equalsIgnoreCase(bluePlayer)){
            getInputBluePlayer();
            while (!controller.makeMove(id, xBlue, yBlue, bluePlayer))
                getInputBluePlayer();
        }
        printBoard(id);
    }

    private static void playGameStartingWithBlue(int id){
        while(!controller.checkForFinishedGame(id)) {
            bluePlayerTakeTurn(id);
            if (controller.checkForFinishedGame(id))
                break;
            redPlayerTakeTurn(id);
        }
        System.out.println("Thank you for playing!\n");
        menu();
    }

    private static void playGameStartingWithRed(int id) {
        while(!controller.checkForFinishedGame(id)) {
            redPlayerTakeTurn(id);
            if (controller.checkForFinishedGame(id))
                break;
            bluePlayerTakeTurn(id);
        }
        System.out.println("Thank you for playing!\n");
        menu();
    }

    private static void seeGamesInProgress() {
        System.out.println("Games in progress:\nid red player blue player");
        System.out.println(controller.seeInProgressGames());
        menu();
    }

    private static void seeCompletedGames() {
        System.out.println("Games that have been completed:\nid red player blue player");
        System.out.println(controller.seeFinishedGames());
        menu();
    }

    private static void seeLeaderboard() {
        System.out.println(controller.getLeaderBoard());
        menu();
    }

    private static void menu() {
        System.out.println("Enter a number to select an option:\n1. Create a user\n2. Create a new game\n3. See games" +
                " in progress\n4. Join a game\n5. See list of completed games\n6. See leaderboard\n");
        menuChoice = in.nextInt();
        System.out.println();
        in.nextLine();
        switch (menuChoice) {
            case 1:
                createUser();
                break;
            case 2:
                makeNewGame();
                break;
            case 3:
                seeGamesInProgress();
                break;
            case 4:
                joinGame();
                break;
            case 5:
                seeCompletedGames();
                break;
            case 6:
                seeLeaderboard();
                break;
            default:
                System.out.println("Exiting game...");
                System.exit(0);
        }
    }
}
