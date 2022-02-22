package test;

public class TestRun {

    public static void main(String[] args) {
        Piece rook = new Rook(Colour.WHITE);
        Piece pawn = new Pawn(Colour.WHITE);
        Piece rookTwo = new Rook(Colour.BLACK);

        Piece[] pieceArr= {rook, pawn, rookTwo};

        for (Piece piece : pieceArr) {
            System.out.println(piece.getType());
            System.out.println(piece.getColour());
        }

    }
    
}
