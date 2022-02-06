import java.util.ArrayList;
import java.util.Arrays;

public class Position {
    public int x;
    public int y;
    private ArrayList<Integer> validPositions = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));

    public Position(int x, int y){
        this.x = x;
        this.y = y;

        if ( !validPositions.contains(x) || !validPositions.contains(y) ){
            System.out.println("WARNING: created invalid Position object");
        }
    }
}
