package core.user;

public class Move {

    private int x,y;
    private User owner;

    public Move(int x, int y, User player) {
        this.x = x;
        this.y = y;
        this.owner = player;
    }

    public String toString() {
        String str = String.format("(%d, %d)", this.x, this.y);
        return str;
    }

    public String getOwnerName() { return owner.getName(); }

    public User getOwner() { return owner; }

    public int getX() {
        return this.x;
    }

    public int getY() { return this.y; }

}
