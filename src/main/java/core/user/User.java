package core.user;

public class User {

    private int wins;
    private int losses;
    private int ties;
    private String name;

    public User(String name) {
        this.name = name;
        wins = 0;
        losses = 0;
        ties = 0;
    }

    public int getWins() {
        return this.wins;
    }
    public int getLosses() {
        return this.losses;
    }
    public int getTies() {
        return this.ties;
    }
    public String getName() {
        return this.name;
    }
    public void addWin() {
        wins++;
    }
    public void addLoss() {
        losses++;
    }
    public void addTie() {
        ties++;
    }
    public int getScore() {
        return (3 * wins) + ties;
    }

}
