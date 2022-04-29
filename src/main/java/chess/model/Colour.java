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
        if (colour.toLowerCase().equals("white")) {
            return WHITE;
        } else if (colour.toLowerCase().equals("black")) {
            return BLACK;
        } else {
            return null;
        }
    }

}
