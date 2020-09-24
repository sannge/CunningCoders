package core.game;

import core.user.Move;
import core.user.User;
import java.util.*;

public class GameManager {

    //counter to give to games as their ID, incremented after each game is created & assigned the int
    private int gameIDCount = 0;
    //map to keep track of all the games, key is the unique ID, value is the game instance
    private Map<Integer, Game> allGameMap;
    //map tp keep track of all users created, key is the unique name, value is the user instance
    private Map<String, User> allUsers;

    public GameManager() {
        allGameMap = new HashMap<>();
        allUsers = new HashMap<>();
    }

    /**
     * Gets the instance of the game with the given ID
     * @param gameID ID of the game
     * @return the game instance
     */
    public Game getInstanceOfGame(int gameID) {
        return allGameMap.get(gameID);
    }

    /**
     *
     * @param username a string representation of a username
     * @return the User object with that username
     */
    public User getAUser(String username) { return allUsers.get(username); }

    /**
     * Creates a new game with the 2 given users as long as they are valid and adds the new game to the map
     * @param redName the first player in the game
     * @param blueName the second player in the game
     * @return the ID of the created game
     */
    public int createNewGame(String redName, String blueName, boolean isPublic) {
        gameIDCount++;
        if (!checkIfUsersExist(redName) || !checkIfUsersExist(blueName)) {
            System.err.println("This player doesn't exist! Create a new user first.");
            return -7;
        }
        Game g = new Game(gameIDCount, allUsers.get(redName), allUsers.get(blueName), isPublic);
        allGameMap.put(gameIDCount,g);
        return gameIDCount;
    }

    /**
     * Checks to see if the given user exists in the all users map
     * @param playerName the name of the user being created
     * @return true if the user exists, false if not
     */
    public boolean checkIfUsersExist(String playerName) { return (allUsers.containsKey(playerName)); }

    /**
     * Creates a new user that can be used to play in a game and keeo track of wins, losses, etc.
     * @param nameOfNewPlayer the unique name of the player being created
     * @return true if the player was successfully created, false if otherwise
     */
    public boolean createNewUser(String nameOfNewPlayer) {
        if (nameOfNewPlayer.length() > 12) {
            System.out.println("Error: Name too long!");
            return false;
        }
        User newPlayer = new User(nameOfNewPlayer);

        if (allUsers.putIfAbsent(nameOfNewPlayer, newPlayer) == null)
            return true;
        System.out.println("Error: Player already exists! Enter another name");
        return false;
    }

    /**
     * Returns a string representation of the leader board sorted by users with the highest score
     * @return a String representation of the leader board
     */
    public String leaderboardToString() {
        Map<String, User> sortedUserByScore = sortMap();
        return buildLeaderBoard(sortedUserByScore);
    }

    private String buildLeaderBoard(Map<String, User> boardStrings) {
        //Widths for the columns, easily change later if needed!
        int rowNameWidth = 12; //Make sure this >  than username min length!
        int rowScoreWidth = 11;
        int rowWinsWidth = 10;
        int rowLossesWidth = 10;
        int rowTiesWidth = 10;
        int total = rowNameWidth + rowScoreWidth + rowWinsWidth + rowLossesWidth + rowTiesWidth;
        StringBuilder s = new StringBuilder();
        for (int i = 0 ; i < total + 6; i++)
            s.append("-");
        s.append("\n|");
        leaderboardSpaceAppend("Name", rowNameWidth, s, false);
        s.append("Name");
        leaderboardSpaceAppend("Name", rowNameWidth, s, true);
        s.append("|");
        leaderboardSpaceAppend("Score", rowScoreWidth, s, false);
        s.append("Score");
        leaderboardSpaceAppend("Score", rowScoreWidth, s, true);
        s.append("|");
        leaderboardSpaceAppend("Wins", rowWinsWidth, s, false);
        s.append("Wins");
        leaderboardSpaceAppend("Wins", rowWinsWidth, s, true);
        s.append("|");
        leaderboardSpaceAppend("Losses", rowLossesWidth, s, false);
        s.append("Losses");
        leaderboardSpaceAppend("Losses", rowLossesWidth, s, true);
        s.append("|");
        leaderboardSpaceAppend("Ties", rowTiesWidth, s, false);
        s.append("Ties");
        leaderboardSpaceAppend("Ties", rowTiesWidth, s, true);
        s.append("|\n");
        //Add 6 for | in between
        for (int i = 0; i < total + 6; i++) {
            s.append("-");
        }
        s.append("\n");
        //Now build the leaderboard string
        for (String key: boardStrings.keySet()) {
            String rowName = boardStrings.get(key).getName();
            String rowScore = Integer.toString(boardStrings.get(key).getScore());
            String rowWins = Integer.toString(boardStrings.get(key).getWins());
            String rowLosses = Integer.toString(boardStrings.get(key).getLosses());
            String rowTies = Integer.toString(boardStrings.get(key).getTies());
            s.append("|");
            leaderboardSpaceAppend(rowName, rowNameWidth, s, false);
            s.append(rowName);
            leaderboardSpaceAppend(rowName, rowNameWidth, s, true);
            s.append("|");
            leaderboardSpaceAppend(rowScore, rowScoreWidth, s, false);
            s.append(rowScore);
            leaderboardSpaceAppend(rowScore, rowScoreWidth, s, true);
            s.append("|");
            leaderboardSpaceAppend(rowWins, rowWinsWidth, s, false);
            s.append(rowWins);
            leaderboardSpaceAppend(rowWins, rowWinsWidth, s, true);
            s.append("|");
            leaderboardSpaceAppend(rowLosses, rowLossesWidth, s, false);
            s.append(rowLosses);
            leaderboardSpaceAppend(rowLosses, rowLossesWidth, s, true);
            s.append("|");
            leaderboardSpaceAppend(rowTies, rowTiesWidth, s, false);
            s.append(rowTies);
            leaderboardSpaceAppend(rowTies, rowTiesWidth, s, true);
            s.append("|\n");
        }
        for (int i = 0 ; i < total + 6; i++)
            s.append("-");
        return s.toString();
    }

    /**
     * Helper method for leaderboardToString that appends the correct number of
     * spaces to a column depending on the length of the string and the width of
     * the column.
     * @param col the string to be in the center of the column
     * @param width the number of characters of the column
     * @param sb the StringBuilder to append to
     * @param secondHalf boolean to check if another space needs to be appended in special cases
     */
    private void leaderboardSpaceAppend(String col, int width, StringBuilder sb, boolean secondHalf) {
        for (int i = 0; i < (width - col.length()) / 2; i++)
            sb.append(" ");
        //Append another space on second half for special cases
        if (secondHalf && ((col.length() % 2 == 1 && width % 2 == 0) ||
                (col.length() % 2 == 0 && width % 2 == 1)))
            sb.append(" ");
    }

    private Map<String, User> sortMap() {
        //Convert userScores to list
        List<Map.Entry<String, User>> list =
                new LinkedList<>(allUsers.entrySet());
        //Sort the list by the user score
        Collections.sort(list, new Comparator<Map.Entry<String, User>>() {
            public int compare(Map.Entry<String, User> o1, Map.Entry<String, User> o2) {
                return ((Integer)o1.getValue().getScore()).compareTo(o2.getValue().getScore());
            }
        });
        Collections.reverse(list);
        //Convert back to map
        Map<String, User> sortedUserMap = new LinkedHashMap<>();
        for (Map.Entry<String, User> entry : list) {
            sortedUserMap.put(entry.getKey(), entry.getValue());
        }
        return sortedUserMap;
    }

    public Map<String, User> getSortedUsers(){
        return sortMap();
    }
    /**
     * Gets all the users the have been created
     * @return the map of all users
     */
    public Map<String, User> getAllUsers() { return allUsers; }

    /**
     * Returns the board as a 2-D array with its current state
     * @param id ID of the game from which the board will be retreived
     * @return the baord state as a string and is easy to read
     */
    public String getBoard(int id) {
        if(!allGameMap.containsKey(id))
            return "no games found";
        else
            return (allGameMap.get(id).getBoard());
    }

    /**
     * Checks to see if game with given ID is is progress or finished
     * @param gameID ID of the game status being checked
     * @return true if game if finished, false if still in progress
     */
    public boolean checkForGameOver(int gameID) { return allGameMap.get(gameID).gameIsFinished(); }

    /**
     * Loops through all of the games in allGameMap and if the game has not finished then that games information,
     * the ID and each player's name, is added to a string to be returned
     * @return string representation of every in progress game, including the gameID and each players name
     */
    public String getAllGamesInProgress() {
        StringBuilder str = new StringBuilder();
        for (Game g : allGameMap.values()) {
            if (!g.gameIsFinished() && g.isPublic()) {
                str.append(getGameInfo(g));
            }
        }
        return str.toString();
    }

    /**
     * Loops through all of the games in allGameMap and if the game has been completed then that games information,
     * the ID and each player's name, is added to a string to be returned
     * @return string representation of every finished game, including the gameID and each players name
     */
    public String getAllFinishedGames() {
        StringBuilder str = new StringBuilder();
        for (Game g : allGameMap.values()) {
            if (g.gameIsFinished() && g.isPublic()) {
                str.append(getGameInfo(g));
            }
        }
        return str.toString();
    }

    /**
     * Helper method for getAllGamesInProgress and getAllFinishedGames that returns a string with a game's information
     * with the ID and player names
     * @param g the game to get the info from
     * @return string representation of the passed in game, including the gameID and each players name
     */
    private String getGameInfo(Game g) {
        int id = g.getID();
        String redPlayer = g.redPlayer.getName();
        String bluePlayer = g.bluePlayer.getName();
        return String.format("%s %s %s\n", Integer.toString(id), redPlayer, bluePlayer);
    }

    public String getMyGameInfo(String playerName){
        StringBuilder str = new StringBuilder();
        for (Game g: allGameMap.values()){
            if ( (playerName.equalsIgnoreCase(g.getRedPlayerName())
                    || playerName.equalsIgnoreCase(g.getBluePlayerName()) ) && !(g.gameIsFinished()) ){
                str.append(getGameInfo(g));
            }
        }
        return str.toString();
    }

    /**
     * Makes a move in a game with the given ID
     * @param ID ID of game to make move in
     * @param x x coordinate of move
     * @param y y coordinate of move
     * @param playerName name of the User making the move
     * @return true if move successfully made, false otherwise
     */
    public boolean moveInGame(int ID, int x, int y, String playerName) {
        return allGameMap.get(ID).makeMove(x, y, playerName);
    }

    /**
     * Gets the last user who made a move in the game with given ID
     * @param id ID of game
     * @return name of User who made the last move in that game
     */
    public String  getUserCurrentTurn(int id){
        Game g = allGameMap.get(id);
        String lastUserToMakeMove = g.currentMoveUser();
        return lastUserToMakeMove;
    }


    public boolean hasPutDownPiece(int id, String name){
        return allGameMap.get(id).hasPutDownPiece(name);
    }

    /**
     * Returns the username of the red player in the game
     * @param id ID of the game
     * @return the name of the Red Player in the game
     */
    public String playerNameInGameRed(int id){ return allGameMap.get(id).getRedPlayerName(); }

    /**
     * Returns the username of the blue player in the game
     * @param id ID of the game
     * @return the name of the Blue Player in the game
     */
    public String playerNameInGameBlue(int id) { return allGameMap.get(id).getBluePlayerName(); }

    public boolean checkGameExists(int id){ return (allGameMap.containsKey(id)); }

    public List<Move> getMovesInGame(int id) { return allGameMap.get(id).getMoves(); }

    public boolean checkFinishedGame(int id) { return allGameMap.get(id).gameIsFinished(); }

}
