package chess.model;

public enum Colour {

    WHITE {
        @Override
        public String toString() {
            return "White";
        }
    },
    BLACK {
        @Override
        public String toString() {
            return "Black";
        }
    };

    public static Colour parseColour(String colour) {
        if (colour.equals("WHITE") || colour.equals("white") || colour.equals("White")) {
            return WHITE;
        } else if (colour.equals("BLACK") || colour.equals("black") || colour.equals("Black")) {
            return BLACK;
        } else {
            return null;
        }
    }

}
