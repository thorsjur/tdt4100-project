package chess.model;

import java.util.List;

import chess.model.Board.Coordinate;

import java.util.ArrayList;

public class Knight extends Piece {

    private static final int[][] jumps = {
            new int[] { 2, 1 },
            new int[] { 2, -1 },
            new int[] { -2, 1 },
            new int[] { -2, -1 },
            new int[] { 1, 2 },
            new int[] { -1, 2 },
            new int[] { 1, -2 },
            new int[] { -1, -2 }
    };

    public Knight(Colour colour, Board board) {
        super(colour, board);
    }

    @Override
    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();
        Coordinate fromCoordinates = getCoordinates();

        for (int[] jump : jumps) {
            Coordinate toCoordinates = fromCoordinates.addVector(jump);
            if (toCoordinates.row() > 7 || toCoordinates.row() < 0 || toCoordinates.column() > 7
                    || toCoordinates.column() < 0) {
                continue;
            }
            Piece atPiece = board.getPiece(toCoordinates);
            if (atPiece == null) {
                moveList.add(new Move(fromCoordinates, toCoordinates, Move.MoveType.MOVE));
                continue;
            }
            if (atPiece.getColour() != this.getColour()) {
                moveList.add(new Move(fromCoordinates, toCoordinates, Move.MoveType.TAKE));
            }
        }
        return moveList;
    }

    public static int[][] getJumpArray() {
        return jumps;
    }

    @Override
    public String toString() {
        return colour.toString() + "Knight";
    }
}
