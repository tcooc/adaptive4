import java.util.*;
import java.util.List;

public class MinMax {

    public interface Agent {
        int[] getMove(Conga game, int[][] moveset, int color);
    }

    public static class FirstMoveAgent implements Agent {
        @Override
        public int[] getMove(Conga game, int[][] moveset, int color) {
            return moveset[0];
        }
    }

    public static class RandomAgent implements Agent {
        private final Random rand = new Random();
        @Override
        public int[] getMove(Conga game, int[][] moveset, int color) {
            return moveset[rand.nextInt(moveset.length)];
        }
    }

    // the min-max agent stores internal state based on the current game being played
    // if a new game is started, or if the board state is changed in a way that doesn't follow game rules, create a new one
    public static class MinMaxAgent implements Agent {
        private static class Node {
            int score;
            int[] move;
            public Node(int score, int[] move) {
                this.score = score;
                this.move = move;
            }
        }
        private final int maxDepth;

        public MinMaxAgent(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        int color;
        int nodesExplored;
        int abPrunes;

        int nullAlpha = Integer.MIN_VALUE;
        int nullBeta = Integer.MAX_VALUE;

        @Override
        public int[] getMove(Conga game, int[][] moveset, int color) {
            // create min-max tree
            // root node is MAX. game states that favor `color have higher rating`, and game states that favor the opponent have lower rating
            this.color = color;
            nodesExplored = 0;
            abPrunes = 0;

            Node rootNode = expandNode(game, null, 0, color, true, nullAlpha, nullBeta);

            System.out.println("Nodes explored: " + nodesExplored);
            System.out.println("a-b prunes: " + abPrunes);
            return rootNode.move;
        }

        // alpha = highest value of MAX so far
        // beta = lowest value of MIN so far
        public Node expandNode(Conga state, int[] lastMove, int depth, int color, boolean max, int aAlpha, int aBeta) {
            int[][] moveset = state.getMoveSet(color);
            Node maxima = new Node(max ? Integer.MIN_VALUE : Integer.MAX_VALUE, lastMove);
            int alpha = aAlpha;
            int beta = aBeta;
            if(depth == maxDepth) {
                return new Node(calculateScore(state, this.color), lastMove);
            }
            // limit this Agents moves to only "good" moves, based shouldMakeMove
            List<int[]> limitedMoveset = new ArrayList<>();
            if(max) {
                for (int[] move : moveset) {
                    if (shouldMakeMove(state, move, color)) {
                        limitedMoveset.add(move);
                    }
                }
            }
            if(limitedMoveset.size() > 0) {
                moveset = new int[limitedMoveset.size()][];
                limitedMoveset.toArray(moveset);
            }
            for(int[] move : moveset) {
                maxima = calculateMaxima(max, maxima, expandNode(state.move(move), move, depth + 1, -color, !max, alpha, beta));
                nodesExplored++;
                if(max) {
                    alpha = Math.max(alpha, maxima.score);
                } else {
                    beta = Math.min(beta, maxima.score);
                }
                // a-b pruning
                if(max) {
                    // this is a MAX node, if alpha >= beta of ancestors, skip
                    if(alpha >= aBeta) {
                        abPrunes++;
                        break;
                    }
                } else {
                    // this is a MIN node, if beta <= alpha of ancestors, skip
                    if(beta <= aAlpha) {
                        abPrunes++;
                        break;
                    }
                }
            }
            if(lastMove != null) {
                maxima.move = lastMove;
            }
            if(depth == 0) {
                nullAlpha = alpha / 2 + nullAlpha / 2;
                nullBeta = beta / 2 + nullBeta / 2;
            }
            return maxima;
        }

        private static Node calculateMaxima(boolean max, Node n1, Node n2) {
            if(max) {
                return n1.score > n2.score ? n1 : n2;
            } else {
                return n1.score > n2.score ? n2 : n1;
            }
        }

        // higher score = better state for `color`
        public int calculateScore(Conga state, int color) {
            int myMovesetSize = state.getMoveSet(color).length;
            int opponentMovesetSize = state.getMoveSet(-color).length;
            if(myMovesetSize == 0) {
                return Integer.MIN_VALUE;
            }
            return -Math.round(100.0f * opponentMovesetSize / myMovesetSize);
        }

        private boolean shouldMakeMove(Conga state, int[] move, int color) {
            return state.move(move).getMoveSet(-color).length < state.getMoveSet(-color).length;
        }
    }

    public static void main(String[] arg) {
        try {
            CongaTest.test();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Conga game = new Conga();
        Agent player1 = new MinMaxAgent(8); // black
        Agent player2 = new RandomAgent(); // white
        int turn = Conga.BLACK;
        int[][] moveset;
        int moves = 0;
        while((moveset = game.getMoveSet(turn)).length > 0) {
            System.out.println(game.toString());
            Agent player = turn == Conga.BLACK ? player1 : player2;
            int[] move = player.getMove(game, moveset, turn);
            game = game.move(move);
            turn = -turn;
            moves++;
        }
        System.out.println("Winner=" + (turn == Conga.BLACK ? "WHITE" : "BLACK"));
        System.out.println("Moves=" + moves);
        System.out.println(game.toString());
//        System.out.println(Arrays.deepToString(game.getMoveSet(Conga.BLACK)));
//        System.out.println(Arrays.deepToString(game.getMoveSet(Conga.WHITE)));
    }

    public static class Conga {
        // black is negative numbers, white is positive
        public static final int BLACK = -1, WHITE = 1;

        private static final int[][] DIRECTIONS = new int[][]{
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        private static final int SIZE = 4;

        private final int board[][] = new int[SIZE][SIZE];

        public Conga() {
            setPieces(1 - 1, 4 - 1, 10 * BLACK);
            setPieces(4 - 1, 1 - 1, 10 * WHITE);
        }

        public Conga(Conga c) {
            for(int i = 0; i < board.length; i++) {
                board[i] = Arrays.copyOf(c.board[i], SIZE);
            }
        }

        private int setPieces(int x, int y, int value) {
            return board[x][y] = value;
        }

        public Conga move(int[] move) {
            return move(move[0], move[1], move[2], move[3]);
        }

        public Conga move(int startX, int startY, int nextX, int nextY) {
            Conga c = new Conga(this);
            c.moveVolatile(startX, startY, nextX, nextY);
            return c;
        }

        private void moveVolatile(int startX, int startY, int nextX, int nextY) {
            int dX = nextX - startX;
            int dY = nextY - startY;
            if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
                throw new IllegalArgumentException("nextX/nextY isn't an adjacent tile");
            }
            int color = getColor(board[startX][startY]);
            if (color == 0) {
                throw new IllegalArgumentException("No pieces to move");
            }

            if (areEnemies(color, board[nextX][nextY])) {
                throw new IllegalArgumentException("Illegal move onto enemy piece");
            }

            int count = Math.abs(board[startX][startY]);
            int stack = 1;
            while (nextX < SIZE && nextX >= 0 && nextY < SIZE && nextY >= 0
                    && !areEnemies(color, board[nextX][nextY])) {
                if (stack >= count) {
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

        public int[][] getMoveSet(int color) {
            List<int[]> moveset = new ArrayList<>();
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];
                    if (value * color > 0) { // same color
                        for (int k = 0; k < DIRECTIONS.length; k++) {
                            int i1 = i + DIRECTIONS[k][0];
                            int j1 = j + DIRECTIONS[k][1];
                            if (i1 >= 0 && i1 < SIZE && j1 >= 0 && j1 < SIZE) {
                                int value1 = board[i1][j1];
                                if (value1 == 0 || value1 * color > 0) {
                                    moveset.add(new int[]{i, j, i1, j1});
                                }
                            }
                        }
                    }
                }
            }
            int[][] movesetArr = new int[moveset.size()][];
            moveset.toArray(movesetArr);
            return movesetArr;
        }

        private static final boolean areEnemies(int value1, int value2) {
            return getColor(value1) * getColor(value2) < 0;
        }

        private static final int getColor(int value) {
            if (value < 0) {
                return BLACK;
            }
            if (value > 0) {
                return WHITE;
            }
            return 0;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (int y = SIZE - 1; y >= 0; y--) {
                for (int x = 0; x < SIZE; x++) {
                    sb.append(String.format("%3d", board[x][y]));
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    private static class CongaTest {
        private static void test() throws Exception {
            Conga c;
            // given test case 1
            c = clearConga();
            c.setPieces(1 - 1, 4 - 1, 10 * Conga.WHITE);
            c.setPieces(1 - 1, 3 - 1, 5 * Conga.BLACK);
            c.setPieces(2 - 1, 3 - 1, 5 * Conga.BLACK);
            c = c.move(1 - 1, 4 - 1, 2 - 1, 4 - 1);
            checkBoardState(c, new int[][]{
                    {0, 1, 2, 7},
                    {-5, -5, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
            });
            // given test case 2
            c = clearConga();
            c.setPieces(1 - 1, 4 - 1, 6 * Conga.WHITE);
            c.setPieces(3 - 1, 4 - 1, 4 * Conga.WHITE);
            c.setPieces(4 - 1, 4 - 1, 5 * Conga.BLACK);
            c.setPieces(1 - 1, 3 - 1, 5 * Conga.BLACK);
            c = c.move(1 - 1, 4 - 1, 2 - 1, 4 - 1);
            checkBoardState(c, new int[][]{
                    {0, 1, 9, -5},
                    {-5, 0, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
            });
            // base case
            c = new Conga();
            c = c.move(1 - 1, 4 - 1, 2 - 1, 4 - 1);
            checkBoardState(c, new int[][]{
                    {0, -1, -2, -7},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
                    {0, 0, 0, 10},
            });
        }

        private static Conga clearConga() {
            Conga c = new Conga();
            c.setPieces(1 - 1, 4 - 1, 0);
            c.setPieces(4 - 1, 1 - 1, 0);
            return c;
        }

        private static void checkBoardState(Conga board, int[][] state) throws Exception {
            int[][] transposed = new int[state.length][state[0].length];
            for (int i = 0; i < state.length; i++) {
                for (int j = 0; j < state[0].length; j++) {
                    transposed[j][state[0].length - 1 - i] = state[i][j];
                }
            }
            if (!Arrays.deepEquals(board.board, transposed)) {
                System.err.println(board);
                throw new Exception("Invalid board state");
            }
        }
    }
}
