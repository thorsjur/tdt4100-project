package test;

public abstract class Piece {

    public abstract String getType();

    Colour colour;

    public Piece(Colour colour) {
        this.colour = colour;
    }

    public Colour getColour() {
        return colour;
    }
}
