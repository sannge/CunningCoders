package core.game;
import core.Color;
import core.controller.GameController;
import core.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class gamePackageTest {

    Board b1;
    Square s1, s2;
    GameManager gm;

    @Before
    public void init() {
        b1 = new Board();
        s1 = new Square(5,10);
        s2 = new Square(2,4);
        gm = new GameManager();
    }

    @Test
    public void checkHorizontalTest() {
        //horizontal success
        b1.squaresOnBoard[2][2].changeColor(Color.Red);
        b1.squaresOnBoard[3][2].changeColor(Color.Red);
        b1.squaresOnBoard[4][2].changeColor(Color.Red);
        b1.squaresOnBoard[5][2].changeColor(Color.Red);
        b1.squaresOnBoard[6][2].changeColor(Color.Red);
        b1.squaresOnBoard[7][2].changeColor(Color.Red);
        assertFalse(b1.checkHorizontal(4,1, b1.squaresOnBoard[2][2].getColor()));
        assertTrue(b1.checkHorizontal(2,2, b1.squaresOnBoard[2][2].getColor()));
        //only 5 in a row, should fail
        assertFalse(b1.checkHorizontal(3,2, b1.squaresOnBoard[3][2].getColor()));
        //would go off board, should fail
        b1.squaresOnBoard[16][2].changeColor(Color.Red);
        b1.squaresOnBoard[17][2].changeColor(Color.Red);
        b1.squaresOnBoard[18][2].changeColor(Color.Red);
        assertFalse(b1.checkHorizontal(16,2,b1.squaresOnBoard[16][2].getColor()));
    }

    @Test
    public void checkVerticalTest() {
        //vertical success
        b1.squaresOnBoard[1][1].changeColor(Color.Red);
        b1.squaresOnBoard[1][2].changeColor(Color.Red);
        b1.squaresOnBoard[1][3].changeColor(Color.Red);
        b1.squaresOnBoard[1][4].changeColor(Color.Red);
        b1.squaresOnBoard[1][5].changeColor(Color.Red);
        b1.squaresOnBoard[1][6].changeColor(Color.Red);
        assertFalse(b1.checkVertical(6,4,b1.squaresOnBoard[1][1].getColor()));
        assertTrue(b1.checkVertical(1,1,b1.squaresOnBoard[1][1].getColor()));
        //only 4 in a row, should fail
        assertFalse(b1.checkVertical(1,3, b1.squaresOnBoard[1][3].getColor()));
        //would go off board, should fail
        b1.squaresOnBoard[1][15].changeColor(Color.Red);
        b1.squaresOnBoard[1][16].changeColor(Color.Red);
        b1.squaresOnBoard[1][17].changeColor(Color.Red);
        b1.squaresOnBoard[1][18].changeColor(Color.Red);
        assertFalse(b1.checkVertical(1,15, b1.squaresOnBoard[1][15].getColor()));
    }

    @Test
    public void checkDiagonalLeftTest() {
        //diagonal left success
        b1.squaresOnBoard[8][7].changeColor(Color.Red);
        b1.squaresOnBoard[7][8].changeColor(Color.Red);
        b1.squaresOnBoard[6][9].changeColor(Color.Red);
        b1.squaresOnBoard[5][10].changeColor(Color.Red);
        b1.squaresOnBoard[4][11].changeColor(Color.Red);
        b1.squaresOnBoard[3][12].changeColor(Color.Red);
        assertFalse(b1.checkDiagonalLeft(3,8, b1.squaresOnBoard[8][7].getColor()));
        assertTrue(b1.checkDiagonalLeft(8,7, b1.squaresOnBoard[8][7].getColor()));
    }

    @Test
    public void checkDiagonalRightTest() {
        //diagonal right success
        b1.squaresOnBoard[2][3].changeColor(Color.Red);
        b1.squaresOnBoard[3][4].changeColor(Color.Red);
        b1.squaresOnBoard[4][5].changeColor(Color.Red);
        b1.squaresOnBoard[5][6].changeColor(Color.Red);
        b1.squaresOnBoard[6][7].changeColor(Color.Red);
        b1.squaresOnBoard[7][8].changeColor(Color.Red);
        assertFalse(b1.checkDiagonalRight(2,10, b1.squaresOnBoard[2][3].getColor()));
        assertTrue(b1.checkDiagonalRight(2,3, b1.squaresOnBoard[2][3].getColor()));
    }

    @Test
    public void testIsWinning() {
        b1.squaresOnBoard[13][13].changeColor(Color.Red);
        //assertFalse(b1.isWinning(13,13,b1.squaresOnBoard[13][13].getColor()));
        //assertTrue(b1.isWinning(2,3,b1.squaresOnBoard[2][3].getColor()));
        assertFalse(b1.isWinning());
        //assertTrue(b1.isWinning());
    }

    @Test
    public void testAWinUsingGameManager1() {
        gm.createNewUser("nick");
        gm.createNewUser("sam");
        gm.createNewGame("nick", "sam", true);

        gm.moveInGame(1, 0, 0, "nick");
        gm.moveInGame(1, 10, 0, "sam");
        gm.moveInGame(1, 10, 1, "sam");
        gm.moveInGame(1, 0, 1, "nick");
        gm.moveInGame(1, 0, 2, "nick");
        gm.moveInGame(1, 10, 2, "sam");
        gm.moveInGame(1, 10, 3, "sam");
        gm.moveInGame(1, 0, 3, "nick");
        gm.moveInGame(1, 0, 4, "nick");
        gm.moveInGame(1, 10, 4, "sam");
        gm.moveInGame(1, 10, 5, "sam");

        assertEquals(0, gm.getAUser("nick").getWins());
        assertEquals(1, gm.getAUser("nick").getLosses());
        assertEquals(1, gm.getAUser("sam").getWins());
        assertEquals(0, gm.getAUser("sam").getLosses());
    }

    @Test
    public void testAWinUsingGameManager2() {
        gm.createNewUser("nick");
        gm.createNewUser("sam");
        gm.createNewGame("nick", "sam", true);

        gm.moveInGame(1, 0, 0, "nick");
        gm.moveInGame(1, 10, 0, "sam");
        gm.moveInGame(1, 10, 1, "sam");
        gm.moveInGame(1, 0, 1, "nick");
        gm.moveInGame(1, 0, 2, "nick");
        gm.moveInGame(1, 10, 2, "sam");
        gm.moveInGame(1, 10, 3, "sam");
        gm.moveInGame(1, 0, 3, "nick");
        gm.moveInGame(1, 0, 4, "nick");
        gm.moveInGame(1, 10, 4, "sam");
        gm.moveInGame(1, 10, 7, "sam");
        gm.moveInGame(1, 0, 5, "nick");

        assertEquals(1, gm.getAUser("nick").getWins());
        assertEquals(0, gm.getAUser("nick").getLosses());
        assertEquals(0, gm.getAUser("sam").getWins());
        assertEquals(1, gm.getAUser("sam").getLosses());
    }

    @Test
    public void changeColorTestSquare() {
        s1.changeColor(Color.Red);
        assertEquals(Color.Red, s1.getColor());
    }

    @Test
    public void toStringTestSquare() {
        assertEquals("|   ", s1.toString());
        assertEquals("|   ", s2.toString());
    }

    @Test
    public void getColorTestSquare() {
        assertEquals(Color.Black, s2.getColor());
    }

    @Test
    public void testCheckIfSquareIsOpen() {
        assertEquals(true, b1.checkIfSquareIsOpen(0, 0));
        b1.squaresOnBoard[0][0].changeColor(Color.Red);
        assertEquals(false, b1.checkIfSquareIsOpen(0, 0));
    }


    //TODO: figure out how to test a tie.

    @Test
    public void testTie() {
        gm.createNewUser("Sam");
        gm.createNewUser("Nick");
        String redPlayer = "Sam", bluePlayer = "Nick";
        int id = gm.createNewGame(redPlayer, bluePlayer, true);

        for (int row = 0; row < 19; row++) {

            if (row%2 == 0) {
                for (int col = 0; col < 18; col += 4) {
                    gm.moveInGame(id, col, row, redPlayer);
                    gm.moveInGame(id, col + 1, row, redPlayer);
                }
                for (int col = 2; col < 18; col += 4) {
                    gm.moveInGame(id, col, row, bluePlayer);
                    gm.moveInGame(id, col + 1, row, bluePlayer);
                }
            }else{
                for (int col = 0; col < 18; col += 4) {
                    gm.moveInGame(id, col, row, bluePlayer);
                    gm.moveInGame(id, col + 1, row, bluePlayer);
                }

                for (int col = 2; col < 18; col += 4) {
                    gm.moveInGame(id, col, row, redPlayer);
                    gm.moveInGame(id, col + 1, row, redPlayer);
                }
            }
        }

        for (int row = 0; row < 19; row++) {
            if (row%2 == 0)
                gm.moveInGame(id, 18, row, redPlayer);
            else
                gm.moveInGame(id, 18, row, bluePlayer);


        }

        /*We can see the board is full and nobody has won,
        so the game gives each player a tie and closes the game. */
        System.out.println(gm.getBoard(id));

    }

    @Test
    public void gameManagerGameCreationAndProgressReport() {
        // make a game and make sure its in progress and not finished
        gm.createNewUser("red");
        gm.createNewUser("blue");

        gm.createNewGame("red", "blue", true);
        assertEquals("1 red blue\n", gm.getAllGamesInProgress());
        assertEquals("", gm.getAllFinishedGames());


        gm.createNewUser("red2");
        gm.createNewUser("blue2");
        // make a second game and make it be finished
        gm.createNewGame("red2", "blue2", true);
        for (int i = 0; i < 6; i++)
            gm.moveInGame(2, 0, i, "red2");

        // check that the second game has finished and that the game in progress is still there and that there is a
        // new finished game
        assertEquals(true, gm.getInstanceOfGame(2).gameIsFinished());
        assertEquals("1 red blue\n", gm.getAllGamesInProgress());
        assertEquals("2 red2 blue2\n", gm.getAllFinishedGames());

    }

    @Test
    public void gameManagerUserCreation() {
        assertEquals(false, gm.checkIfUsersExist("nick"));
        gm.createNewUser("nick");
        assertEquals(true, gm.checkIfUsersExist("nick"));

    }

    @Test
    public void LeaderboardTest() {
        gm.createNewUser("Sam");
        gm.createNewUser("Michael");
        gm.createNewUser("Walker");
        gm.createNewUser("San");
        gm.createNewUser("Nick");
        Map<String, User> users = gm.getAllUsers();
        users.get("Sam").addWin();
        users.get("Sam").addWin();
        users.get("Sam").addWin();

        users.get("Michael").addLoss();
        users.get("Walker").addLoss();
        users.get("San").addLoss();
        users.get("Nick").addWin();
        users.get("Walker").addLoss();
        users.get("Sam").addTie();
        users.get("Nick").addTie();

        for (int i = 0; i < 125; i++)
            users.get("Sam").addWin();

        for (int i = 0; i < 78; i++)
            users.get("Michael").addLoss();

        System.out.println(gm.leaderboardToString());
    }

    @Test
    public void currentMoveUserTest() {
        gm.createNewUser("red");
        gm.createNewUser("blue");

        gm.createNewGame("red", "blue", true);
        gm.moveInGame(1, 1, 1, "red");

        Assert.assertEquals("blue", gm.getInstanceOfGame(1).currentMoveUser());
        gm.moveInGame(1,2,1,"blue");
        Assert.assertEquals("blue", gm.getInstanceOfGame(1).currentMoveUser());
        gm.moveInGame(1,3,1,"blue");
        Assert.assertEquals("red", gm.getInstanceOfGame(1).currentMoveUser());

    }

    @Test
    public void hasPutDownPieceTest() {
        gm.createNewUser("red");
        gm.createNewUser("blue");

        gm.createNewGame("red", "blue", true);
        // for reason its always true before anyone makes a move
        Assert.assertEquals(true, gm.getInstanceOfGame(1).hasPutDownPiece("red"));
        Assert.assertEquals(true, gm.getInstanceOfGame(1).hasPutDownPiece("blue"));

        gm.moveInGame(1, 1, 1, "red");
        Assert.assertEquals(true, gm.getInstanceOfGame(1).hasPutDownPiece("red"));
        Assert.assertEquals(false, gm.getInstanceOfGame(1).hasPutDownPiece("blue"));

        gm.moveInGame(1,2,1,"blue");
        Assert.assertEquals(false, gm.getInstanceOfGame(1).hasPutDownPiece("red"));
        Assert.assertEquals(true, gm.getInstanceOfGame(1).hasPutDownPiece("blue"));

    }

}