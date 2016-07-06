import java.awt.*;

public class Conga {
    public static final int BLACK = -1, WHITE = 1;

    private final int board[][] = new int[4][4];

    public Conga() {
        setPieces(1, 4, 10 * BLACK);
        setPieces(4, 1, 10 * WHITE);

    }

    public int getPieces(int x, int y) {
        return board[x - 1][y - 1];
    }

    private int setPieces(int x, int y, int value) {
        return board[x - 1][y - 1] = value;
    }

    public void move(int startX, int startY, int nextX, int nextY) {
        moveInternal(startX - 1, startY - 1, nextX - 1, nextY - 1);
    }

    private void moveInternal(int startX, int startY, int nextX, int nextY) {
        int dX = nextX - startX;
        int dY = nextY - startY;
        if(Math.abs(dX) > 1 || Math.abs(dY) > 1) {
            throw new IllegalArgumentException("nextX/nextY isn't an adjacent tile");
        }
        int color = getColor(board[startX][startY]);
        if(color == 0) {
            throw new IllegalArgumentException("No pieces to move");
        }

        if (areEnemies(color, board[nextX][nextY])) {
            throw new IllegalArgumentException("Illegal move onto enemy piece");
        }

        int count = Math.abs(board[startX][startY]);
        int stack = 1;
        while(nextX < board.length && nextX >= 0 && nextY < board[0].length && nextY >= 0
                && !areEnemies(color, board[nextX][nextY])) {
            if(stack >= count) {
                stack = count;
            }
            board[nextX][nextY] += color * stack;
            count -= stack;
            stack++;
            nextX += dX;
            nextY += dY;
        }
        board[startX][startY] = 0;
        board[nextX - dX][nextY - dY] += color * count;
    }

    private static final boolean areEnemies(int value1, int value2) {
        return getColor(value1) * getColor(value2) < 0;
    }

    private static final int getColor(int value) {
        if(value < 0) {
            return BLACK;
        }
        if(value > 0) {
            return WHITE;
        }
        return 0;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(int y = board[0].length - 1; y >= 0; y--) {
            for(int x = 0; x < board.length; x++) {
                sb.append(String.format("%3d", board[x][y]));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
