package chess;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class King extends Piece {

    public King(Colour colour, Board board) {
        super(colour, board);
        moveDirections = Direction.values();
    }

    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();
        for (Direction direction : moveDirections) {
            List<Move> moves = getUnidirectionalMoves(direction, getCoordinates(), true);
            if (moves.size() > 0) {
                moveList.add(moves.get(0));
            }
        }
        return moveList;
    }

    public boolean isThreatened() {
        // Threats from pawns
        Direction[] directions = {Direction.UP_LEFT, Direction.UP_RIGHT};
        Class<?>[] classList = {Pawn.class};
        if (isThreatenedByPiece(directions, classList, true)) return true;

        // Threats from king
        directions = Direction.values();
        classList = new Class<?>[]{King.class};
        if (isThreatenedByPiece(directions, classList, true)) return true;

        // Threats from rooks and queen
        directions = new Direction[]{Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT};
        classList = new Class<?>[]{Rook.class, Queen.class};
        if (isThreatenedByPiece(directions, classList, false)) return true;
        
        // Threats from bishops and queen
        directions = new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT, Direction.DOWN_RIGHT};
        classList = new Class<?>[]{Bishop.class, Queen.class};
        if (isThreatenedByPiece(directions, classList, false)) return true;

        // Threats from knights
        int[][] knightJumps = Knight.getJumpArray();
        int[] fromCoordinates = getCoordinates();
        for (int[] jump : knightJumps) {
            int[] toCoordinates = {fromCoordinates[0] + jump[0], fromCoordinates[1] + jump[1]};
            Piece piece = getPieceRelativeToPosition(toCoordinates);
            if (piece != null && piece instanceof Knight) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return colour.toString() + "King";
    }

    private Piece getOpponentPiece(Direction direction, boolean singleMove) {
        List<Move> moveList = getUnidirectionalMoves(direction, getCoordinates(), true);
        for (Move move : moveList) {
            if (move.getType() == Move.MoveType.TAKE) return board.getPiece(move.getToCoordinates());
        }
        return null;
    }

    private boolean isThreatenedByPiece(Direction[] directions, Class<?>[] classList, boolean singleMove) {
        for (Direction direction : directions) {
            Piece piece = getOpponentPiece(direction, singleMove);
            for (Class<?> cls : classList) {
                if (piece != null && piece.getClass() == cls) return true;
            }
        }
        return false;
    }
}
