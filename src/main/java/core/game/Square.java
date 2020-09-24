package core.game;

import core.Color;

public class Square {
    private int xCoord;
    private int yCoord;
    private Color color;

    public Square(int x, int y) {
        xCoord = x;
        yCoord = y;
        //Default color is black before anyone clicks the square
        color = Color.Black;
    }

    public void changeColor(Color c) {
        this.color = c;
    }

    public String toString() {
        String color;
        if(this.color.equals(Color.Black))
            color = "|   ";
        else if(this.color.equals(Color.Blue))
            color = "| B ";
        else
            color = "| R ";
        return color;
    }

    public Color getColor() {
        return this.color;
    }

}
