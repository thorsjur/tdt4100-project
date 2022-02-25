package chess;

public enum Colour {
    
    WHITE {
        @Override
        public String toString() {
            return "White";
        }

        public String getHexColour() {
            return "#d3d3d3";
        }
    },
    BLACK {
        @Override
        public String toString() {
            return "Black";
        }

        public String getHexColour() {
            return "#3c3d3b";
        }
    };

    public String getHexColour() {
        return this.getHexColour();
    }
}
