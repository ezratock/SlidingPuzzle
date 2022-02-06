#Markdown

There are two runnable classes: Validate.java (so that this project is compatible with the autograding script) and Driver.java (the normal main method).  In addition, there is a Puzzle class that encapsulates all the functionality of the puzzle's board, a Solver class that finds a solution to given Puzzle object, and a WordWrap class that helps format text.

Validate.java takes in the puzzle input configuration in the same format as the files on the P2Examples folder on blackboard.  Then it will print either success or failed depending of whether the set of instructions solves the given board.

The Driver.java class has a user-interaction loop that prompts the user for which mode they want until the user enters "quit".  The three modes are: interactive mode, validation mode and solve mode.

Puzzle.java stores the data of the board in a 2D array.  This class allows the main method to get and set different tiles, execute different moves in various ways, and print the board to the terminal via print().

##Interactive Mode
Choose the difficulty to be easy medium or hard.  This will scramble the board with either 7/8, 16, or 100 random moves respectively such that each scrambled move is not redundant (there will be no RL/LR or DU/UD substrings in the scrambling instructions).
 
 Afterwards, the program will enter another while loops that runs until either the board is solved or the user enters "quit".  They will be prompted to enter there next move which they can enter in one of the following methods: entering "up", "down", "left", "right" or the first letter of any of those directions to determine which way the *0 tile* should move.  Alternatively, they can type the number they would like to swap the zero tile with, but it will only properly execute if the entered tile is indeed adjacent to the 0 tile.
 
 At any point, the user can also type "stats" to view which difficulty they have selected, how many moves it has taken them so far and how much time it has taken.  Once the user solves the game, it will also print out these statistics.

##Validation Mode
Enter a board configuration and a set of instructions in the same format as the P2Examples folder on blackboard.  There is input validation, so it will not let you continue until the board is both in the valid format (it must be a 4 by 4 grid of numbers separated by spaces and then a string of moves) and a valid board (each tile must be from 0-15 and there can be no repeats). It will then tell the user if that set of instructions solves the puzzle, and if it doesn't, it will print a correct solution that does solve it.

##Solve Mode
I had some fun with this project, so I decided to add a solve mode. The program first prompts the user if they want to solve a randomly generated board or a user-inputted board.  If they choose randomly generated, it creates a random board by scrambling a sorted list of the numbers 0-15.  Forums assert that only half of the possible board configurations are solvable due to a parity argument, but because this assignment allows both of these two board configurations to be considered solved...

| 0 | 1 | 2 | 3 |  |  | 1 | 2 | 3 | 4 |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 4 | 5 | 6 | 7 |  |  | 5 | 6 | 7 | 8 |
| 8 | 9 | 10 | 11 |  |  | 9 | 10 | 11 | 12 |
| 12 | 13 | 14 | 15 |  |  | 13 | 14 | 15 | 0 |

... it turns out that **any** initial board setup can be solved to one of these states with only valid moves.  

If the user chooses to enter the board configuration manually, it reads the board input in the same fashion as it does in validation mode.

The program will then generate the sequence of moves that solves the board.  My algorithm works pretty methodically, solving the puzzle by row.  Some important helper methods are: findPath()—which uses the A* search algorithm to find the quickest path for the zero to make to a certain tile without interrupting any of the "locked" tiles—and solveTile()—which solves a certain tile to a certain position on the board by (pretty much) repeatedly using the findPath method back and forth from the tile and its desired position.

Once the solution has been generated, the user is asked if they want to see the solution in action.  If they enter yes, the program will continue to print the board, executing one move at a time, with a short delay in between each print depending on a speed the user inputs.