package chess.model;

import java.util.Arrays;

public class Move {

    enum MoveType {
        TAKE {
            public String getHexColour() {
                return "#8e0000";
            }
        },
        MOVE {
            public String getHexColour() {
                return "#05752a";
            }
        },
        EN_PASSANT {
            public String getHexColour() {
                return "#750550";
            }
        },
        CASTLE {
            public String getHexColour() {
                return "#683ad0";
            }
        },
        PROMOTION {
            public String getHexColour() {
                return "#ffd700";
            }
        };

        public String getHexColour() {
            return this.getHexColour();
        }
    }

    private MoveType type;
    private int[] fromCoordinates;
    private int[] toCoordinates;

    public Move(int[] fromCoordinates, int[] toCoordinates) {
        this.fromCoordinates = fromCoordinates;
        this.toCoordinates = toCoordinates;
    }

    public Move(int[] fromCoordinates, int[] toCoordinates, MoveType type) {
        this(fromCoordinates, toCoordinates);
        this.type = type;
    }

    public int[] getFromCoordinates() {
        return fromCoordinates;
    }

    public int[] getToCoordinates() {
        return toCoordinates;
    }

    public MoveType getType() {
        return type;
    }

    public void highlightMove(Board board) {
        String hexColour = "#000000";
        if (type != null) {
            hexColour = type.getHexColour();
        }
        Square square = board.getSquare(toCoordinates);
        square.setHighlight(hexColour);
    }

    public boolean leadsToCheck(Board board) {
        Piece piece = board.getPiece(fromCoordinates);
        return board.checkNextBoardForCheck(piece, toCoordinates);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
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
