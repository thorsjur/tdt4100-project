package chess;

public class Move {
    
    private int[] fromCoordinates;
    private int[] toCoordinates;

    @Override
    public String toString() {
        return "to row: " + fromCoordinates[0] + " column: " + fromCoordinates[1] + " | to row: " + toCoordinates[0]
                + " column: " + toCoordinates[1];
    }
}
