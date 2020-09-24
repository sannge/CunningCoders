package core.game;

import core.Color;

public class Board {

    protected Square[][] squaresOnBoard;

    public Board() {
        squaresOnBoard = new Square[19][19];
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                Square individualSquare = new Square(j, i);
                squaresOnBoard[j][i] = individualSquare;
            }
        }
    }

    public String displayBoard() {
        StringBuilder sb = new StringBuilder("  | ");
        for(int i = 0; i< squaresOnBoard.length; i++) {
            if(i == 0)
                sb.append(i );
            else if(i < 10)
                sb.append(" | " + i);
            else
                sb.append(" |" + i);
        }
        sb.append(" |\n ");
        printDashedRow(sb);
        for(int i = 0; i < squaresOnBoard.length; i++) {
            sb.append(i);
            if( i < 10)
                sb.append(" ");
            for(int j = 0; j < squaresOnBoard[0].length; j++) {
                sb.append(squaresOnBoard[j][i].toString());
            }
            sb.append("|\n ");
            printDashedRow(sb);
        }
        return sb.toString();
    }

    private void printDashedRow(StringBuilder b) {
        for (int k = 0; k < (squaresOnBoard.length*3)+1; k++) {
            if (k % 3 == 1) {
                b.append("|");
            }
            b.append("-");
        }
        b.append("|\n");
    }

    /**
     * Method that checks board for a winning state. If a color is found
     * (not the default board color) in a square, then the surrounding squares
     * are checked for six colors in a row.
     * @return True if there is a winning state, false if there is not a winning state.
     */
    public boolean isWinning() {
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                if (squaresOnBoard[i][j].getColor() != Color.Black)
                    if (subIsWinning(i, j, squaresOnBoard[i][j].getColor())) {
                        return true;
                    }
            }
        }
        return false;
    }

    /**
     * Submethod to isWinning. Calls a check to functions that check every
     * direction in which a win is possible.
     */
    public boolean subIsWinning(int x, int y, Color c) {
        return (checkHorizontal(x, y, c) || checkVertical(x, y, c) || checkDiagonalRight(x, y, c) ||
                checkDiagonalLeft(x, y, c));
    }

    public boolean checkHorizontal(int x, int y, Color c) {
        if (x > 13)
            return false;
        for (int i = 0; i < 6; i++) {
            if(squaresOnBoard[x + i][y].getColor() != c)
                return false;
        }
        return true;
    }

    public boolean checkVertical(int x, int y, Color c) {
        if (y > 13)
            return false;
        for (int i = 0; i < 6; i++) {
            if(squaresOnBoard[x][y + i].getColor() != c)
                return false;
        }
        return true;
    }

    public boolean checkDiagonalRight(int x, int y, Color c) {
        if (x > 13 || y > 13)
            return false;
        for (int i = 0; i < 6; i++) {
            if(squaresOnBoard[x + i][y + i].getColor() != c)
                return false;
        }
        return true;
    }

    public boolean checkDiagonalLeft(int x, int y, Color c) {
        if (x < 5 || y > 13)
            return false;
        for (int i = 0; i < 6; i++) {
            if(squaresOnBoard[x - i][y + i].getColor() != c)
                return false;
        }
        return true;
    }

    /**
     * Checks if a square is occupied already or not
     * @param x x coordinate on board
     * @param y y coordinate on board
     * @return true if the square isn't occupied, false if the square is already occupied
     */
    public boolean checkIfSquareIsOpen(int x, int y) {
        if (squaresOnBoard[x][y].getColor() != Color.Black) {
            return false;
        } else {
            return true;
        }
    }

}
