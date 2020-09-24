package core.user;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class userPackageTest {

    User u1;
    User u2;

    @Before
    public void initUser() {
        u1 = new User("test");
        u2 = new User("test1");

        u1.addWin();
        u1.addLoss();
        u1.addTie();
    }
    @Test
    public void getWins() {
        assertEquals(0, u2.getWins());
        assertEquals(1, u1.getWins());
    }

    @Test
    public void getLosses() {
        assertEquals(0, u2.getLosses());
        assertEquals(1, u1.getLosses());
    }

    @Test
    public void getTies() {
        assertEquals(0, u2.getTies());
        assertEquals(1, u1.getTies());
    }

    @Test
    public void testGetName() {
        assertEquals("test", u1.getName());
        assertEquals("test1", u2.getName());
    }

    @Test
    public void testGetScore() {
        assertEquals(4, u1.getScore());
        assertEquals(0, u2.getScore());
    }

    @Test
    public void testSetGetUser() {
        Move move = new Move(0, 0, u2);
        assertEquals(0, move.getX());
        assertEquals(0, move.getY());
    }

    @Test
    public void testGetOwner() {
        Move move = new Move(0,0,u1);
        Move move2 = new Move(1,1,u2);
        assertEquals("test", move.getOwnerName());
        assertEquals("test1", move2.getOwnerName());
    }

    @Test
    public void moveToString() {
        // TODO will have to change this when we change how we keep track of users
        Move move = new Move(5, 8, u1);
        assertEquals("(5, 8)", move.toString());
    }
}