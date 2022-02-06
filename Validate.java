import java.util.ArrayList;
import java.util.Scanner;

public class Validate {
    public static void main(String[] args) throws InterruptedException {

        String moves = "";

        String input = "";
        Scanner scnr = new Scanner(System.in);
        Puzzle puzzle;

        System.out.println("Enter the input below:");

        for (int i = 0; i < 4; i++) {
            input += scnr.nextLine() + "\n";
        }

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

        moves = scnr.nextLine();

        // performing all the moves

        puzzle.executeMoves(moves, false);
        // print the result
        System.out.println();
        if (puzzle.isSolved()) {
            System.out.println(WordWrap.wrap("success", Driver.WORD_WRAP, false));
        } else {
            System.out.println(WordWrap.wrap("failed", Driver.WORD_WRAP, false));
        }
    }
}
