import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Puzzle {
    protected int[][] board;
    private int delay = 1000;
    protected int[][] solution = new int[][]{{0,1,2,3},{4,5,6,7},{8,9,10,11},{12,13,14,15}};
    protected int[][] alternateSolution = new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
    public int[][] errorConfig = new int[][]{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
    private boolean isPrint = false;

    public Puzzle(){
        this.board = copyConfig(alternateSolution);
    }

    public Puzzle(int[][] config) {
        this.board = copyConfig(config);
    }

    public static int[][] copyConfig(int[][] config){
        int[][] returnVar = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                returnVar[i][j] = config[i][j];
            }
        }
        return returnVar;
    }

    public void setDelay(int delay) { this.delay = delay; }

    public void setPosition(Position position, int value){
        board[position.y][position.x] = value;
    }

    public void setPosition(int position, int value){
        board[position / 4][position % 4] = value;
    }

    public int getTile(int index){
        return board[index / 4][index % 4];
    }

    public int getTile(Position pos) { return board[pos.y][pos.x]; }

    public void print(){
        String horizontal = "+---+---+---+---+";
        for (int[] row : board){
            System.out.print(horizontal + "\n|");
            for (int i : row){
                System.out.print(i < 10 ? " " + i + " |" : i + " |");
            }
            System.out.println();
        }
        System.out.println(horizontal);
    }

    public Position findTilePos(int tile){
        for (int y = 0; y < 4; y++){
            for (int x = 0; x < 4; x++){
                if (board[y][x] == tile) {
                    return new Position(x, y);
                }
            }
        }
        return new Position(-1, -1);
    }

    protected void invalidMove(){
        if (isPrint) { System.out.println("Invalid move!"); }
    }

    public void swap(Position a, Position b){
        int tmp = board[a.y][a.x];
        board[a.y][a.x] = board[b.y][b.x];
        board[b.y][b.x] = tmp;
    }

    public boolean up(){
        boolean validMove = true;
        Position blankPos = this.findTilePos(0);
        if (blankPos.y == 0) { invalidMove(); validMove = false;}
        else {
            Position adjacentPos = new Position(blankPos.x, blankPos.y - 1);
            this.swap(blankPos, adjacentPos);
        }
        return validMove;
    }

    public boolean down(){
        boolean validMove = true;
        Position blankPos = this.findTilePos(0);
        if (blankPos.y == 3) { invalidMove(); validMove = false;}
        else {
            Position adjacentPos = new Position(blankPos.x, blankPos.y + 1);
            this.swap(blankPos, adjacentPos);
        }
        return validMove;
    }

    public boolean right(){
        boolean validMove = true;
        Position blankPos = this.findTilePos(0);
        if (blankPos.x == 3) { invalidMove(); validMove = false;}
        else {
            Position adjacentPos = new Position(blankPos.x + 1, blankPos.y);
            this.swap(blankPos, adjacentPos);
        }
        return validMove;
    }

    public boolean left(){
        boolean validMove = true;
        Position blankPos = this.findTilePos(0);
        if (blankPos.x == 0) { invalidMove(); validMove = false;}
        else {
            Position adjacentPos = new Position(blankPos.x - 1, blankPos.y);
            this.swap(blankPos, adjacentPos);
        }
        return validMove;
    }

    public boolean fromNumber(int num){
        Position blankPos = this.findTilePos(0);
        if (board[blankPos.y][Math.max(0, blankPos.x - 1)] == num) {
            return left();
        } else if (board[blankPos.y][Math.min(3, blankPos.x + 1)] == num) {
            return right();
        } else if (board[Math.max(0, blankPos.y - 1)][blankPos.x] == num) {
            return up();
        } else if (board[Math.min(3, blankPos.y + 1)][blankPos.x] == num) {
            return down();
        }
        invalidMove();
        return false;
    }

    public boolean isEqual(int[][] checkBoard){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ( board[i][j] != checkBoard[i][j] ) { return false; }
            }
        }
        return true;
    }

    public boolean isSolved(){
        return isEqual(solution) || isEqual(alternateSolution);
    }

    public int randomMove(int cantBe){
        boolean madeMove = false;
        int returnVar = -1;
        // randomly chooses a move and sets cantBe to the opposite move (to avoid moving back and forth)
        while (! madeMove) {
            int rand = (int) Math.floor(Math.random() * 4);
            if (rand != cantBe) {
                if (rand == 0) {
                    madeMove = this.up();
                    returnVar = 1;
                } else if (rand == 1) {
                    madeMove = this.down();
                    returnVar = 0;
                } else if (rand == 2) {
                    madeMove = this.left();
                    returnVar = 3;
                } else if (rand == 3) {
                    madeMove = this.right();
                    returnVar = 2;
                } else {
                    System.out.println("ERROR: Random number out of range");
                }
            }
        }
        return returnVar;
    }

    public void togglePrint() { isPrint = !isPrint; }

    public String executeMoves(String moves, boolean print) throws InterruptedException {
        for (char i : moves.toCharArray()){
            if (print) {
                System.out.println(i);
            }
            if ( i == 'U'){
                up();
                if (print) {
                    print();
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
            } else if (i == 'D') {
                down();
                if (print) {
                    print();
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
            } else if (i == 'R') {
                right();
                if (print) {
                    print();
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
            } else if (i == 'L') {
                left();
                if (print) {
                    print();
                    TimeUnit.MILLISECONDS.sleep(delay);
                }
            } else if (i == '#') {
            } else { System.out.println("ERROR: Invalid move..."); System.exit(0); }
        }
        return moves;
    }
}
