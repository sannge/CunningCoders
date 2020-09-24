package core.controller;

import core.game.GameManager;
import core.user.Move;
import core.user.User;
import java.util.List;
import java.util.Map;

public class GameController {

    private GameManager gameManager = new GameManager();

    public GameController() { }

    public String reportBoard(int gameID) {
        return gameManager.getBoard(gameID);
    }

    public boolean makeMove(int ID, int x, int y, String playerName) {
        return gameManager.moveInGame(ID, x, y, playerName);
    }

    public boolean checkForFinishedGame(int gameID) {
        return gameManager.checkForGameOver(gameID);
    }

    public String seeInProgressGames() {
        return gameManager.getAllGamesInProgress();
    }

    public String seeFinishedGames() {
        return gameManager.getAllFinishedGames();
    }

    public int newPublicGame(String redPlayer, String bluePlayer) {
        return gameManager.createNewGame(redPlayer, bluePlayer, true);
    }

    public int newPrivateGame(String redPlayer, String bluePlayer) {
        return gameManager.createNewGame(redPlayer, bluePlayer, false);
    }

    public boolean hasPlayerRegistered(String playerName) {
        return gameManager.checkIfUsersExist(playerName);
    }

    public boolean registerNewPlayer(String nameOfNewUser) {
        return gameManager.createNewUser(nameOfNewUser);
    }

    public String getLeaderBoard() {
        return gameManager.leaderboardToString();
    }

    public boolean playerHasPutDownPiece(int id, String name){
        return gameManager.hasPutDownPiece(id, name);
    }

    public String getUserNameRed(int id){ return gameManager.playerNameInGameRed(id); }

    public String getUserNameBlue(int id) { return gameManager.playerNameInGameBlue(id); }

    public String userCurrentTurn( int id){ return gameManager.getUserCurrentTurn(id); }

    public boolean checkIfGameExists(int id){ return gameManager.checkGameExists(id); }

    public List<Move> getMovesInGame(int id) { return gameManager.getMovesInGame(id); }

    public Map<String, User> getUsers() { return gameManager.getAllUsers(); }

    public Map<String, User> getSortedByScore(){ return  gameManager.getSortedUsers(); }

    public String seeMyGames(String playerName){ return gameManager.getMyGameInfo(playerName); }

    public boolean isFinished(int id){ return gameManager.checkFinishedGame(id); }

}
