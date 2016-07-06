import java.util.*;

public class Maze {
    public static final int XSIZE = 25, YSIZE = 25;

    int startX, startY, endX, endY;
    final int xSize, ySize;
    // false represents wall, true represents path
    // (0, 0) is bottom left, (24, 24) is top right
    final boolean grid[][];

    public static void main(String[] arg) {
        new Maze();
    }

    private Maze(int xSize, int ySize) {
        grid = new boolean[xSize][ySize];
        this.xSize = xSize;
        this.ySize = ySize;
    }

    private Maze() {
        this(XSIZE, YSIZE);
    }

    public static Maze generate() {
        Maze maze = new Maze();
        maze.generatePath(0, 0, XSIZE - 1, YSIZE - 1);
        maze.obfuscate(1.4);
        return maze;
    }

    private void generatePath(int startX, int startY, int endX, int endY) {
        Random rand = new Random();
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        grid[startX][startY] = true;
        grid[endX][endY] = true;

        int pathX = startX, pathY = startY;
        while(pathX != endX || pathY != endY) {
            int direction = rand.nextInt(4);
            switch(direction) {
                case 0:
                    if(pathX + 2 < xSize) {
                        grid[pathX + 1][pathY] = true;
                        grid[pathX + 2][pathY] = true;
                        pathX += 2;
                    }
                    break;
                case 1:
                    if(pathX - 2 >= 0) {
                        grid[pathX - 1][pathY] = true;
                        grid[pathX - 2][pathY] = true;
                        pathX -= 2;
                    }
                    break;
                case 2:
                    if(pathY + 2 < xSize) {
                        grid[pathX][pathY + 1] = true;
                        grid[pathX][pathY + 2] = true;
                        pathY += 2;
                    }
                    break;
                case 3:
                    if(pathY - 2 >= 0) {
                        grid[pathX][pathY - 1] = true;
                        grid[pathX][pathY - 2] = true;
                        pathY -= 2;
                    }
                    break;

            }
            int converge = 1;
            if(Math.abs(pathX - endX) <= 2 && Math.abs(pathY - endY) <= 2) {
                converge = 4;
            }
            for(int i = 0; i < converge && pathX != endX; i++) {
                pathX -= Math.abs(pathX - endX)/(pathX - endX);
                grid[pathX][pathY] = true;
            }
            for(int i = 0; i < converge && pathY != endY; i++) {
                pathY -= Math.abs(pathY - endY)/(pathY - endY);
                grid[pathX][pathY] = true;
            }
        }
    }

    private void obfuscate(double factor) {
        int iterations = (int)(factor * xSize * ySize);
        Random rand = new Random();
        for(int i = 0; i < iterations; i++){
            int x = rand.nextInt(xSize), y = rand.nextInt(ySize);
            if(!grid[x][y]) {
                //it is only valid if one and only one adjacent block is open
                int openSiblings = 0;
                if(x + 1 < xSize && grid[x + 1][y]) openSiblings++;
                if(x - 1 > 0 && grid[x - 1][y]) openSiblings++;
                if(y + 1 < ySize && grid[x][y + 1]) openSiblings++;
                if(y - 1 > 0 && grid[x][y - 1]) openSiblings++;
                if(openSiblings == 1) {
                    grid[x][y] = true;
                }
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(xSize * ySize + ySize);
        for(int y = ySize - 1; y >= 0; y--) {
            for(int x = 0; x < xSize; x++) {
                if(x == startX && y == startY) {
                    sb.append('S');
                } else if(x == endX && y == endY) {
                    sb.append('E');
                } else if(grid[x][y]) {
                    sb.append(' ');
                } else {
                    sb.append('#');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
