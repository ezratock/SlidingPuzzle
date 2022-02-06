import java.util.ArrayList;

public class Solver {

    private Puzzle pathFinder;
    private String solution;
    private boolean PRINT_ON = false;

    public Solver() {
        pathFinder = new Puzzle(new int[][]{{16, 16, 16, 16}, {16, 16, 16, 16}, {16, 16, 16, 16}, {16, 16, 16, 16}});
        solution = "";
    }

    public Solver(boolean printOn) {
        pathFinder = new Puzzle(new int[][]{{16, 16, 16, 16}, {16, 16, 16, 16}, {16, 16, 16, 16}, {16, 16, 16, 16}});
        solution = "";
        this.PRINT_ON = printOn;
    }

    public String solve(Puzzle puzzle) throws InterruptedException {
        if (puzzle.isSolved()) {
            return "";
        }

        String tmp;
        for (int j = 0; j < 2; j++) { // for the first two rows
            for (int i = 0; i < 3; i++) { // solve the first three tiles of the row
                Position currentPosition = new Position(i, j);
                solveTile(puzzle, currentPosition, (i + 1) + (4 * j));
            }

            // to solve 4th tile of row: bring zero tile to below the first tile in the row...
            if (puzzle.getTile(new Position(3, j)) != 4 + 4 * j) {
                if (j == 1 && puzzle.getTile(new Position(0, 3)) == 8) { // very methodical algorithm, but fails for one corner case when the 8 tile is in the lower left corner
                    pathFinder.setPosition(new Position(0, 3), -1);
                    solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(1,3)) + "LU", PRINT_ON);
                    pathFinder.setPosition(new Position(0, 3), 16);
                } else {
                    solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(0, j + 1)), PRINT_ON);
                }

                if (puzzle.getTile(new Position(3, j)) == 4 + 4 * j) { continue; } // if the 8 or 4 tile is already solved...

                // ... do URR...
                solution += puzzle.executeMoves("URR", PRINT_ON);
                pathFinder.setPosition(new Position(0, j + 1), -1);
                pathFinder.setPosition(new Position(2, j), 16);

                // ... put the fourth tile of the row in the third tile's location...
                solveTile(puzzle, new Position(2, j), 4 * j + 4);

                // ... then cycle the row back into place with RULLLD
                solution += puzzle.executeMoves("RULLLD", PRINT_ON);
                pathFinder.setPosition(new Position(0, j + 1), 16);
                pathFinder.setPosition(new Position(2, j), -1);
                pathFinder.setPosition(new Position(3, j), -1);
            }
        }

        // last two rows
        solveTile(puzzle, new Position(0, 2), 9);

        solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(3, 3)), PRINT_ON);
        unTrap(puzzle, 1, 10);
        solveTile(puzzle, new Position(1, 2), 10);

        solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(3, 3)), PRINT_ON);
        unTrap(puzzle, 2, 11);
        solveTile(puzzle, new Position(2, 2), 11);

        solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(3, 3)), PRINT_ON);
        Position twelvePos = puzzle.findTilePos(12);
        if (twelvePos.x == 0) {
            solution += puzzle.executeMoves("DLLLURDRURDLULDRURDLURDLLULD", PRINT_ON);
        } else if (twelvePos.x == 1) {
            solution += puzzle.executeMoves("DLLLURRDRULLLD", PRINT_ON);
        } else if (twelvePos.x == 2) {
            solution += puzzle.executeMoves("DLLLURRRDLULLD", PRINT_ON);
        } else {
            solution += puzzle.executeMoves("D", PRINT_ON);
        }

        solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(3, 3)), PRINT_ON);

        if (puzzle.isSolved()) {
            return solution;
        }


        int endVariation = puzzle.getTile(new Position(0, 3));
        solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(3, 3)), PRINT_ON);
        solution += puzzle.executeMoves("LLLURRRDLLLURR", PRINT_ON);
        if (endVariation == 14) {
            solution += puzzle.executeMoves("RDLU", PRINT_ON);
        } else if (endVariation == 15) {
            solution += puzzle.executeMoves("RDLURDLU", PRINT_ON);
        }
        solution += puzzle.executeMoves("LLDRRRULLLDRRR", PRINT_ON);

        return solution;
    }

    private void unTrap(Puzzle puzzle, int degree, int tile) throws InterruptedException {
        // Creating an ArrayList of positions considered "trapped"
        ArrayList<Integer> trappedTiles = getTrappedTiles(puzzle, degree);

        // Does LURRDLULD until the tile is untrapped
        while (trappedTiles.contains(tile)) {
            Position tilePos = puzzle.findTilePos(tile);
            solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), new Position(tilePos.x + 1, tilePos.y)), PRINT_ON);

            solution += puzzle.executeMoves("LURRDLULDRR", PRINT_ON);
            trappedTiles = getTrappedTiles(puzzle, degree);
        }
    }

    private ArrayList<Integer> getTrappedTiles(Puzzle puzzle, int degree) {
        ArrayList<Integer> trappedTiles = new ArrayList<>();
        trappedTiles.add(puzzle.getTile(new Position(0, 3)));
        if (degree == 2) {
            trappedTiles.add(puzzle.getTile(new Position(1, 3)));
        }
        return trappedTiles;
    }

    private void solveTile(Puzzle puzzle, Position targetPosition, int tile) throws InterruptedException {
        while (puzzle.getTile(targetPosition) != tile) {
            pathFinder.setPosition(puzzle.findTilePos(tile), -1);
            solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), targetPosition), PRINT_ON);
            pathFinder.setPosition(puzzle.findTilePos(tile), 16);
            solution += puzzle.executeMoves(findPath(puzzle.findTilePos(0), puzzle.findTilePos(tile)), PRINT_ON);
        }
        pathFinder.setPosition(targetPosition, -1);
    }

    public String findPath(Position start, Position stop) {
        if (pathFinder.getTile(stop) == -1 || pathFinder.getTile(start) == -1) {
            throw new IllegalArgumentException("TRYING TO FIND PATH TO/FROM A LOCKED SQUARE");
        }
        if (start.x == stop.x && start.y == stop.y) {
            return "";
        }

        // Sets all unlocked tiles to 16
        for (int i = 0; i < 16; i++) {
            if (pathFinder.getTile(i) != -1) {
                pathFinder.setPosition(i, 16);
            }
        }
        pathFinder.setPosition(start, 0);

        // Creates A* weight diagram and stores it in pathFinder
        int breakAt = 0;
        for (int num = 0; num < 16; num++) {
            for (int i = 0; i < 16; i++) {
                if (pathFinder.getTile(i) == num) {
                    // Iterates over all adjacent tiles, updating their A* search score if untouched
                    ArrayList<Position> adjTiles = getAdjTiles(i);
                    for (Position pos : adjTiles) {
                        if (pos.x == stop.x && pos.y == stop.y) {
                            breakAt = num + 1;
                            break;
                        }
                        if (pathFinder.getTile(pos) == 16) {
                            pathFinder.setPosition(pos, num + 1);
                        }
                    }
                    if (breakAt > 0) {
                        break;
                    }
                }
            }
            if (breakAt > 0) {
                break;
            }
        }

        // Finds optimal path from A* weight diagram
        String returnVar = "";
        if (breakAt == 0) {
            return "ERROR: Didn't find path";
        }
        Position currentPos = stop;
        Position newPos;
        for (int i = breakAt; i > 0; i--) {
            if (currentPos.x != 0) {
                newPos = new Position(currentPos.x - 1, currentPos.y);
                if (pathFinder.getTile(newPos) == i - 1) {
                    returnVar = "R" + returnVar;
                    currentPos = newPos;
                    continue;
                }
            }
            if (currentPos.x != 3) {
                newPos = new Position(currentPos.x + 1, currentPos.y);
                if (pathFinder.getTile(newPos) == i - 1) {
                    returnVar = "L" + returnVar;
                    currentPos = newPos;
                    continue;
                }
            }
            if (currentPos.y != 0) {
                newPos = new Position(currentPos.x, currentPos.y - 1);
                if (pathFinder.getTile(newPos) == i - 1) {
                    returnVar = "D" + returnVar;
                    currentPos = newPos;
                    continue;
                }
            }
            if (currentPos.y != 3) {
                newPos = new Position(currentPos.x, currentPos.y + 1);
                if (pathFinder.getTile(newPos) == i - 1) {
                    returnVar = "U" + returnVar;
                    currentPos = newPos;
                    continue;
                }
            }
        }
        return returnVar;
    }

    public boolean isAlternate(Puzzle puzzle) {
        Position zeroPos = puzzle.findTilePos(0);
        int inversions = 0;
        for (int i = 0; i < 16; i++) {
            int currentTile = puzzle.getTile(i);
            for (int j = i + 1; j < 16; j++) {
                if (currentTile > puzzle.getTile(j) && puzzle.getTile(j) != 0) {
                    inversions++;
                }
            }
        }
        return (inversions + zeroPos.y) % 2 == 1;
    }

    public Puzzle transpose(Puzzle puzzle) {
        Puzzle returnVar = new Puzzle();
        for (int i = 0; i < 16; i++) {
            returnVar.setPosition(i, (16 - puzzle.getTile(15 - i)) % 16);
        }
        return returnVar;
    }

    public String transpose(String transposedSolution) {
        transposedSolution = transposedSolution.replace("R", "l");
        transposedSolution = transposedSolution.replace("L", "r");
        transposedSolution = transposedSolution.replace("D", "u");
        transposedSolution = transposedSolution.replace("U", "d");
        return transposedSolution.toUpperCase();
    }

    private ArrayList<Position> getAdjTiles(int pos) {
        ArrayList<Position> adjTiles = new ArrayList<>();
        if (pos > 3) {
            adjTiles.add(new Position(pos % 4, (pos / 4) - 1));
        }
        if (pos < 12) {
            adjTiles.add(new Position(pos % 4, (pos / 4) + 1));
        }
        if (pos % 4 != 0) {
            adjTiles.add(new Position((pos % 4) - 1, pos / 4));
        }
        if (pos % 4 != 3) {
            adjTiles.add(new Position((pos % 4) + 1, pos / 4));
        }

        return adjTiles;
    }

}
