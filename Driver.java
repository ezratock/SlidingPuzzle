import java.util.Collections;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;


public class Driver {
    static final boolean ENABLE_SMART_BOARD = true;
    static final int WORD_WRAP = 100;
    static final String WELCOME_MESSAGE = "Welcome to the Sliding Block Puzzle Simulation!\nGame instructions and descriptions of the different modes can be found in the README file.  The three modes are VALIDATION, INTERACTIVE and SOLVE mode.  Type \"help\" for help and \"quit\" to quit.\n";
    static final String VALIDATION_MESSAGE = "You chose VALIDATION mode.\nPlease enter board configurations and the set of moves in the same format as the examples provided in the P2Examples.zip file on blackboard.";
    static final String INVALID_BOARD_MESSAGE = "Invalid input.  Please enter data in the same format as the files in P2Examples.zip (check for typos)!";
    static final String HELP_MESSAGE = "In this puzzle, there are 15 numbered blocks arranged on the squares of a 4x4 grid——the one blank square is represented by a 0.  For each turn, you can \"slide\" any tile adjacent to the empty square into the empty square (swap the empty square with any adjacent tile).  This is denoted by the movement of the 0 tile: \"R\" means the 0 tiles moves right, \"L\" means the 0 tiles moves left, \\\"U\\\" means the 0 tiles moves up and \"D\" means the 0 tiles moves down.  The puzzle is solved once all non-zero tiles are in ascending order with the blank square either in the upper left or lower right corner.\n\nChoose which of the following three modes you want:\nVALIDATION - enter an initial puzzle configuration and a string of moves to see if that set of instructions solves the board.\nINTERACTIVE - Play the game yourself!  Start with an unknown puzzle configuration with ranging difficulty and then enter moves until the puzzle is solved or you give up.\nSOLVE - generate the solution to  either a randomly generated or a manually inputted puzzle configuration.\n";
    static final String EXIT_MESSAGE = "Goodbye!";
    static final ArrayList<String> YES_INPUTS = new ArrayList<>(Arrays.asList("yes", "y"));
    static final ArrayList<String> NO_INPUTS = new ArrayList<>(Arrays.asList("no", "n"));
    static final int EASYMOVES = (int) Math.round(7 + Math.random());
    static final int MEDIUMMOVES = 16;
    static final int HARDMOVES = 100;

    public static void main(String[] args) throws InterruptedException {
        String input;

        Scanner scnr = new Scanner(System.in);
        System.out.println(WordWrap.wrap(WELCOME_MESSAGE, WORD_WRAP, false));
        String mode = "";
        boolean promptMode = true;

        // Sets mode to one of the valid modes depending on enableSmartBoard
        ArrayList<String> I_MODE_INPUTS = new ArrayList<>(Arrays.asList("interactive", "i", "interactive mode"));
        ArrayList<String> V_MODE_INPUTS = new ArrayList<>(Arrays.asList("validation", "v", "validation mode"));
        ArrayList<String> S_MODE_INPUTS = new ArrayList<>(Arrays.asList("solve", "s", "solve mode"));
        ArrayList<String> Q_MODE_INPUTS = new ArrayList<>(Arrays.asList("quit", "q", "quit.","stop","stop."));
        ArrayList<String> H_MODE_INPUTS = new ArrayList<>(Arrays.asList("help", "h", "?"));
        ArrayList<ArrayList<String>> allModeInputs = ENABLE_SMART_BOARD ? join(I_MODE_INPUTS, V_MODE_INPUTS, S_MODE_INPUTS, Q_MODE_INPUTS, H_MODE_INPUTS) : join(I_MODE_INPUTS, V_MODE_INPUTS, Q_MODE_INPUTS, H_MODE_INPUTS);

        while (!mode.equals("quit")) {
            if (promptMode) {
                mode = getInput("Which mode would you like (\"quit\" to quit)\n>>> ", allModeInputs);
            }
            promptMode = true;

            // VALIDATION MODE
            if (mode.equals("validation")) {
                System.out.println(WordWrap.wrap(VALIDATION_MESSAGE, WORD_WRAP, false));

                Puzzle puzzle = new Puzzle();
                String moves = "";

                // gets user input until they enter a valid entry
                boolean validInput = false;
                while (!validInput) {
                    try {
                        puzzle = readBoardInput();
                        moves = scnr.nextLine();
                        if (moves.matches("#?(U|D|L|R|u|d|l|r)+")) {
                            validInput = true;
                        } else {
                            System.out.println(WordWrap.wrap(INVALID_BOARD_MESSAGE, WORD_WRAP, false));
                        }
                    } catch (IllegalArgumentException exception) {
                        scnr.nextLine();
                        System.out.println(WordWrap.wrap(exception.getMessage(), WORD_WRAP, false));
                    }
                }

                // performing all the moves
                Puzzle originalPuzzle = new Puzzle(puzzle.copyConfig(puzzle.board));
                puzzle.executeMoves(moves, false);

                // print the result
                System.out.println();
                if (puzzle.isSolved()) {
                    System.out.println(WordWrap.wrap("Yes! That set of moves solves the given board arrangement.", WORD_WRAP, false));
                } else {
                    System.out.println(WordWrap.wrap("No, that set of moves does not solve the given board arrangement. Try this instead:", WORD_WRAP, false));
                    Solver solver = new Solver();
                    System.out.println(WordWrap.wrap(solve(solver, originalPuzzle), WORD_WRAP, true));
                }

                promptMode = getInput("Would you like to stay in VALIDATION mode?\n>>>", join(YES_INPUTS, NO_INPUTS)).equals("yes") ? false : true;

            }

            // INTERACTIVE MODE
            if (mode.equals("interactive")) {
                ArrayList<String> easyInputs = new ArrayList<>(Arrays.asList("easy", "e"));
                ArrayList<String> mediumInputs = new ArrayList<>(Arrays.asList("medium", "m"));
                ArrayList<String> hardInputs = new ArrayList<>(Arrays.asList("hard", "h"));
                System.out.println("You chose INTERACTIVE mode.");

                // sets difficulty to easy medium or hard
                String difficulty = getInput("What difficulty level do you want?\n>>> ", join(easyInputs, mediumInputs, hardInputs));
                System.out.println("Difficulty set: " + difficulty.toUpperCase());

                // shuffles the board
                Puzzle puzzle = new Puzzle();
                int cantBe = -1;
                for (int i = 0; i < (difficulty == "easy" ? EASYMOVES : difficulty == "medium" ? MEDIUMMOVES : HARDMOVES); i++) {
                    cantBe = puzzle.randomMove(cantBe);
                }

                System.out.println(WordWrap.wrap("Enter the direction you want the zero tile to move (U = up, D = down, R = right, L = left).  You can also enter a number from 1-15 to swap that tile with the zero tile if they are adjacent.  Type 'stats' to view your current game statistics.  Type 'quit' to quit.", WORD_WRAP, false));
                puzzle.print();
                puzzle.togglePrint();

                // game loop
                int moves = 0;
                long startTime = System.currentTimeMillis();
                while (!puzzle.isSolved()) {
                    System.out.print("What's your next move? >>> ");
                    input = scnr.next();

                    ArrayList<String> upInputs = new ArrayList<>(Arrays.asList("up", "u"));
                    ArrayList<String> downInputs = new ArrayList<>(Arrays.asList("down", "d"));
                    ArrayList<String> rightInputs = new ArrayList<>(Arrays.asList("right", "r"));
                    ArrayList<String> leftInputs = new ArrayList<>(Arrays.asList("left", "l"));
                    ArrayList<String> statsInputs = new ArrayList<>(Arrays.asList("stats", "s", "statistics"));

                    if (upInputs.contains(input.toLowerCase())) {
                        if (!puzzle.up()) {
                            continue;
                        }
                    } else if (downInputs.contains(input.toLowerCase())) {
                        if (!puzzle.down()) {
                            continue;
                        }
                    } else if (rightInputs.contains(input.toLowerCase())) {
                        if (!puzzle.right()) {
                            continue;
                        }
                    } else if (leftInputs.contains(input.toLowerCase())) {
                        if (!puzzle.left()) {
                            continue;
                        }
                    } else if (input.matches("\\d{1,2}")) {
                        if (!puzzle.fromNumber(Integer.parseInt(input))) {
                            continue;
                        }
                    } else if (statsInputs.contains(input.toLowerCase())) {
                        System.out.println("Difficulty: " + difficulty.toUpperCase() + "\nMoves: " + moves + "\nTime: " + displayTime(System.currentTimeMillis() - startTime));
                        continue;
                    } else if (input.toLowerCase().equals("quit")) {
                        System.out.println("Game quit.");
                        break;
                    } else {
                        System.out.println("Unknown input...");
                        continue;
                    }

                    moves += 1;
                    puzzle.print();
                }

                if (puzzle.isSolved()) {
                    System.out.println(WordWrap.wrap("Congratulations!  You solved " + difficulty.toUpperCase() + " mode in " + displayTime(System.currentTimeMillis() - startTime) + " with " + moves + " moves.", WORD_WRAP, false));
                }

                promptMode = getInput("Would you like to play again?\n>>>", join(YES_INPUTS, NO_INPUTS)).equals("yes") ? false : true;
            }

            // SOLVE MODE
            if (mode.equals("solve")) {
                // Creating the desired board
                ArrayList<String> manualInputs = new ArrayList<>(Arrays.asList("manual", "m", "manually"));
                ArrayList<String> hardInputs = new ArrayList<>(Arrays.asList("random", "r", "randomly"));
                mode = getInput("Would you to create the board configuration manually or randomly?\n>>> ", join(manualInputs, hardInputs));

                // Creates the board
                Puzzle puzzle = new Puzzle();
                if (mode.equals("manual")) {
                    boolean validInput = false;
                    while (!validInput) {
                        try {
                            puzzle = readBoardInput();
                            validInput = true;
                        } catch (IllegalArgumentException exception) {
                            System.out.println(exception.getMessage());
                        }
                    }
                } else if (mode.equals("random")) {
                    ArrayList<Integer> tiles = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
                    for (int i = 0; i < 16; i++) {
                        Position pos = new Position(i % 4, i / 4);
                        int num = tiles.remove((int) Math.floor(Math.random() * tiles.size()));
                        puzzle.setPosition(pos, num);
                    }
                }

                System.out.println("Board created.  The initial board configuration is:");
                puzzle.print();
                System.out.println("Solving now...");

                // Solves the board
                Solver solver = new Solver();
                Puzzle originalPuzzle = new Puzzle(puzzle.copyConfig(puzzle.board));

                String solution = solve(solver, puzzle);

                // Prints results
                System.out.println("Solved! The solution is:\n" + WordWrap.wrap(solution, WORD_WRAP, true));

                input = getInput("Would you like to see this solution in action?\n>>> ", join(YES_INPUTS, NO_INPUTS));

                if (input == "yes") {
                    ArrayList<String> SLOW_INPUTS = new ArrayList<>(Arrays.asList("slow", "s"));
                    ArrayList<String> MEDIUM_INPUTS = new ArrayList<>(Arrays.asList("medium", "m"));
                    ArrayList<String> FAST_INPUTS = new ArrayList<>(Arrays.asList("fast", "f"));
                    String speed = getInput("How fast would you like to view the solution played out?\n>>> ", join(SLOW_INPUTS, MEDIUM_INPUTS, FAST_INPUTS));
                    originalPuzzle.setDelay(speed == "slow" ? 1000 : speed == "medium" ? 500 : 0);

                    System.out.println();
                    originalPuzzle.print();
                    originalPuzzle.executeMoves(solution, true);
                } else {
                    System.out.println("Ok.");
                }

                promptMode = getInput("Would you like to solve another board?\n>>>", join(YES_INPUTS, NO_INPUTS)).equals("yes") ? false : true;
            }

            if (mode.equals("help")) {
                System.out.println(WordWrap.wrap(HELP_MESSAGE, WORD_WRAP, false));
            }

            if (mode.equals("solve")) {
                System.out.println(WordWrap.wrap(EXIT_MESSAGE, WORD_WRAP, false));
            }
        }
    }

    public static String getInput(String prompt, ArrayList<ArrayList<String>> validInputs) {
        // Creates an array of all possible valid inputs
        ArrayList<String> whileCondition = new ArrayList<>();
        for (ArrayList<String> i : validInputs) {
            whileCondition.addAll(i);
        }

        Scanner scnr = new Scanner(System.in);
        String input = "";

        while (!whileCondition.contains(input)) {
            System.out.print(WordWrap.wrap(prompt, WORD_WRAP, false));
            input = scnr.nextLine();

            // If the input is valid, return what they selected
            for (ArrayList<String> i : validInputs) {
                if ( i.contains(input) ) {
                    return i.get(0);
                }
            }

            // If the input is invalid, print their options and try again
            String toPrint = "Invalid input. Type ";
            for (int i = 0; i < validInputs.size(); i++) {
                toPrint += "'" + validInputs.get(i).get(0) + "'";
                if (i == validInputs.size() - 1) {
                    toPrint += "...";
                } else if (i == validInputs.size() - 2) {
                    toPrint += " or ";
                } else {
                    toPrint += ", ";
                }
            }
            System.out.println(WordWrap.wrap(toPrint, WORD_WRAP, false));
        }
        return "ERROR: Didn't get valid input";
    }

    public static ArrayList<ArrayList<String>> join(ArrayList<String>... args) {
        ArrayList<ArrayList<String>> returnVar = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> arg : args) {
            returnVar.add(arg);
        }
        return returnVar;
    }

    public static String displayTime(long milliseconds) {
        int seconds = (int) milliseconds / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds - minutes * 60;
        minutes = minutes - hours * 60;
        String returnString = "";

        if (hours > 0) {
            returnString += hours + (hours == 1 ? " hour, " : " hours, ");
        }
        if (minutes > 0) {
            returnString += minutes + (minutes == 1 ? " minute" + (hours > 0 ? "," : "") +" and " : " minutes, and ");
        }
        returnString += seconds + (seconds == 1 ? " second" : " seconds");

        return returnString;
    }

    public static Puzzle readBoardInput() throws IllegalArgumentException {
        String input = "";
        Scanner scnr = new Scanner(System.in);
        Puzzle puzzle;

        // Gets input and throws an exception if it isn't in the right format
        System.out.println("Enter a board configuration:");
        for (int i = 0; i < 4; i++) {
            input += scnr.nextLine() + "\n";
        }

        if (!input.matches("((\\d{1,2}\\s?){4}\n){4}")) {
            throw new IllegalArgumentException(INVALID_BOARD_MESSAGE);
        }

        // Creates a new Board from the input
        Scanner iterator = new Scanner(input);
        puzzle = new Puzzle();
        ArrayList<Integer> boardContains = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++){
                int nextInt = iterator.nextInt();
                puzzle.setPosition(new Position(j, i), nextInt);
                boardContains.add(nextInt);
            }
        }

        // Throws an exception if there isn't exactly one of each tile 0-15
        ArrayList<Integer> correctBoard = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        Collections.sort(boardContains);
        if (!boardContains.equals(correctBoard)){
            throw new IllegalArgumentException("Invalid entry.  That board is impossible——check that all tiles are from 0-15 and that there are no repeats.");
        }
        return puzzle;
    }

    private static String solve(Solver solver, Puzzle puzzle) throws InterruptedException {
        if (solver.isAlternate(puzzle)) {
            return solver.solve(puzzle);
        } else {
            Puzzle transposedPuzzle = solver.transpose(puzzle);
            String transposedSolution = solver.solve(transposedPuzzle);
            return solver.transpose(transposedSolution);
        }
    }

}
