import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {

//        Board board = new Board(new int[][]{{4,5,0,6},{14,7,1,2},{12,8,15,10},{3,13,9,11}});
//
//        board = new Board(new int[][]{{4,7,15,0},{10,12,9,11},{3,2,6,13},{14,1,8,5}});
////        board = new Board(new int[][]{{5,1,15,14},{8,4,7,12},{11,9,2,13},{6,3,0,10}});
////        board = new Board(new int[][]{{7,10,9,2},{3,0,4,12},{1,5,8,11},{15,6,13,14}});
////        board = new Board(new int[][]{{8,0,11,6},{9,5,10,15},{2,4,1,13},{7,14,3,12}});
////        board = new Board(new int[][]{{8,14,3,6},{11,13,12,0},{5,1,2,4},{15,10,9,7}});
////        board = new Board(new int[][]{{4,13,0,8},{15,9,12,14},{1,7,11,5},{10,2,3,6}});
//        board = new Board(new int[][]{{13,14,7,10},{5,12,6,9},{4,1,2,3},{11,0,15,8}});
//        board = new Board(new int[][]{{8,12,0,15},{2,3,10,13},{5,14,4,7},{6,1,11,9}});
////        board = new Board(board.copyConfig(board.alternateSolution));
//
//        System.out.println(solver.isAlternate(board));
//        Scanner scnr = new Scanner(System.in);
//        scnr.nextLine();
//
//        Board board_pre = new Board(board.copyConfig(board.board));
//        board.print();
//        board.setDelay(0);
//
//        String solution = solver.solve(board);
//        System.out.println(solution);
//
//        TimeUnit.SECONDS.sleep(1);
//
//        board_pre.print();
//        board_pre.setDelay(0);
//        board_pre.executeMoves(solution, true);
//        System.out.println(solver.isAlternate(board_pre));

        // board.executeMoves(solution, true);




        Scanner scnr = new Scanner(System.in);
        System.out.println("Single board? or # of iterations:");
        String input = scnr.nextLine();

        if (input.equals("single")) {
            Puzzle puzzle = new Puzzle();
            boolean validInput = false;
            while (!validInput) {
                try {
                    puzzle = Driver.readBoardInput();
                    validInput = true;
                }
                catch( IllegalArgumentException exception){
                    System.out.println(exception.getMessage());
                }
            }

            Solver solver = new Solver(true);
            puzzle.setDelay(0);

            String solution;

            if (solver.isAlternate(puzzle)) {
                solution = solver.solve(puzzle);
            } else {
                Puzzle transposedPuzzle = solver.transpose(puzzle);
                transposedPuzzle.setDelay(0);
                String transposedSolution = solver.solve(transposedPuzzle);
                solution = solver.transpose(transposedSolution);
                puzzle = transposedPuzzle;
            }

            System.out.println(solution);
            puzzle.print();

        } else {
            for (int c = 0; c < Integer.parseInt(input); c++) {
                // Creates the board
                Puzzle puzzle = new Puzzle();
                Solver solver = new Solver();
                ArrayList<Integer> tiles = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
                for (int i = 0; i < 16; i++) {
                    Position pos = new Position(i % 4, i / 4);
                    int num = tiles.remove((int) Math.floor(Math.random() * tiles.size()));
                    puzzle.setPosition(pos, num);
                }

                // solves board
//                puzzle.print();
                Puzzle originalPuzzle = new Puzzle(puzzle.copyConfig(puzzle.board));

                String solution;
                if (solver.isAlternate(puzzle)) {
                    solution = solver.solve(puzzle);
                } else {
                    Puzzle transposedPuzzle = solver.transpose(puzzle);
                    String transposedSolution = solver.solve(transposedPuzzle);
                    solution = solver.transpose(transposedSolution);
                    puzzle = transposedPuzzle;
                }

                if (puzzle.isSolved()) {
                    if (c % 10000 == 0) {
                        System.out.println("Solved random board #" + c);
                    }
                } else {
                    originalPuzzle.print();
                    System.out.println("DID NOT SOLVE!\nSolution generated: " + solution);
                    TimeUnit.MILLISECONDS.sleep(500);
                    puzzle.print();
                    System.exit(0);
                }

//                TimeUnit.MILLISECONDS.sleep(100);

            }
        }


        System.out.println(WordWrap.wrap("This is test to see\nif the old code \nis still in functional.  I really hope that it is because it will make all future welcome messages much easier.", 100, false));
        System.out.println(WordWrap.wrap("DRULLDURDLUDRUDLRULDRUUDULDRLUDLRLRLDURLUDLRUDLURLDURLDURLDURLUDLRUUDLRDULURLDURLDURLDURLUDRURULDLDURLDURLUDRUURLDLURLDURLDUDLDLDUDDULDRLUDR", 100, true));
        System.out.println(WordWrap.wrap("does it also work for lines shorter than 100?", 100, false));

    }
}
