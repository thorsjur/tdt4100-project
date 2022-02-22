package chess;

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
    },
}
