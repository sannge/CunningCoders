package core.game;

import core.Color;
import core.user.User;
import core.user.Move;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private int gameID;
    private boolean isPublic;
    private boolean isFinished;
    private Board board;
    User redPlayer, bluePlayer;
    private ArrayList<Move> movesInGame;

    public Game(int id, User redName, User blueName, boolean isPublic) {
        movesInGame = new ArrayList<>();
        this.gameID = id;
        board = new Board();
        redPlayer = redName;
        bluePlayer = blueName;
        isFinished = false;
        this.isPublic = isPublic;
    }

    public int getID() { return this.gameID; }

    public String getBoard() { return board.displayBoard(); }

    /**
     * Method that is called when a user attempts to make a move from the
     * UI. The method checks if the move is valid, then checks if the board
     * is in a winning state after the move is placed.
     * @param x x coordinate on the board
     * @param y y coordinate on the board
     * @param playerName name of the user that is making the move.
     * @return True if the move was made, false if not ( due to violations).
     */
    public boolean makeMove(int x, int y, String playerName) {
        Move currentMove;
        if (board.checkIfSquareIsOpen(x, y)) {
            if (playerName.equals(this.redPlayer.getName())) {
                board.squaresOnBoard[x][y].changeColor(Color.Red);
                currentMove = new Move(x,y,redPlayer);
            }else {
                board.squaresOnBoard[x][y].changeColor(Color.Blue);
                currentMove = new Move(x,y,bluePlayer);
            }
            movesInGame.add(currentMove);
        }
        else {
            System.out.println("Illegal move - square already taken");
            return false;
        }
        if (board.isWinning()) {
            if (redPlayer.getName().equals(playerName)) {
                System.out.println(playerName + " has won! Game over.");
                redPlayer.addWin();
                bluePlayer.addLoss();
                System.out.println(redPlayer.getName() + " now has " + redPlayer.getWins() + " wins!");
            }
            else if(bluePlayer.getName().equals(playerName)) {
                System.out.println(playerName + " has won! Game over.");
                bluePlayer.addWin();
                redPlayer.addLoss();
                System.out.println(bluePlayer.getName() + " now has " + bluePlayer.getWins() + " wins!");
            }
            isFinished = true;
        }
        if (movesInGame.size() == (19*19)) {
            System.out.println("Tie! Game over.");
            redPlayer.addTie();
            bluePlayer.addTie();
            System.out.println(redPlayer.getName() + " now has " + redPlayer.getTies() + " ties!");
            System.out.println(bluePlayer.getName() + " now has " + bluePlayer.getTies() + " ties!");
            isFinished = true;
        }
        return true;
    }

    public boolean gameIsFinished() { return isFinished; }

    /**
     * Method that checks the board for the last move and returns the
     * user's name.
     * @return Username of who's turn it is
     */
    public String currentMoveUser(){

        //If no moves have been made then it's red players turn.
        if (movesInGame.size() == 0) {
            return redPlayer.getName();
        }

        /*Red player goes first, so if red player has put down a piece,
        then it is blue players turn. */
        if (movesInGame.size() == 1 || movesInGame.size() == 2) {
            return bluePlayer.getName();
        }

        Move lastPiece = movesInGame.get(movesInGame.size() - 1);
        Move secondToLastPiece = movesInGame.get(movesInGame.size() - 2);
        Move thirdToLastPiece = movesInGame.get(movesInGame.size() - 3);


        //if last two pieces are same, then it is the other players turn
        if (lastPiece.getOwnerName().equalsIgnoreCase(secondToLastPiece.getOwnerName())) {
            return thirdToLastPiece.getOwnerName();
        }

        //if the last two pieces aren't the same that means that the first two pieces are the same
        return lastPiece.getOwnerName();
    }

    /**
     * If a user has put down a piece during their turn
     * @param userName the user to see if they have put down a piece
     * @return true if they put down a piece during their turn, false otherwise
     */
    public boolean hasPutDownPiece(String userName) {
        /*If the game was left before the first turn was made, then pretend
        that the red player already put down a piece, so later they only get to put
        down one. */
        if (movesInGame.size() == 0)
            return true;

        Move lastPiece = movesInGame.get(movesInGame.size() - 1);

        if (lastPiece.getOwnerName().equalsIgnoreCase(userName))
            return true;

        return false;
    }

    public String getRedPlayerName() { return redPlayer.getName(); }

    public String getBluePlayerName() { return bluePlayer.getName(); }

    public boolean isPublic() { return isPublic; }

    public List<Move> getMoves(){ return movesInGame; }

}
