package chess;

import java.util.Arrays;

public class Move {
    
    private int[] fromCoordinates;
    private int[] toCoordinates;

    public Move(int[] fromCoordinates, int[] toCoordinates) {
        this.fromCoordinates = fromCoordinates;
        this.toCoordinates = toCoordinates;
    }

    public int[] getFromCoordinates() {
        return fromCoordinates;
    }

    public int[] getToCoordinates() {
        return toCoordinates;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Move)) {
            return false;
        }
        
        return Arrays.equals(this.getFromCoordinates(), ((Move) obj).getFromCoordinates())
                && Arrays.equals(this.getToCoordinates(), ((Move) obj).getToCoordinates());

    }

    @Override
    public String toString() {
        return "from row: " + fromCoordinates[0] + " column: " + fromCoordinates[1] + " | to row: " + toCoordinates[0]
                + " column: " + toCoordinates[1];
    }
}
