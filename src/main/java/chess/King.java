package chess;

import java.util.List;
import java.util.ArrayList;

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

        for (boolean longCastle : new boolean[] { true, false }) {
            if (canCastle(longCastle)) {
                int horizontalDifference = board.getTurn() == Colour.WHITE ? 2 : -2;
                if (! board.isBoardRotationEnabled()) {
                    horizontalDifference = ! longCastle ? 2 : -2;
                } else if (longCastle) {
                    horizontalDifference = -horizontalDifference;
                }
                Move castle = new Move(getCoordinates(), getRelativeCoordinates(new int[] { 0, horizontalDifference }),
                        Move.MoveType.CASTLE);
                moveList.add(castle);
            } else {
                System.out.println("Can't castle + " + (longCastle ? "long" : "short"));
            }
        }

        return moveList;
    }

    public boolean isThreatened() {
        // Threats from pawns
        Direction[] directions = { Direction.UP_LEFT, Direction.UP_RIGHT };
        Class<?>[] classList = { Pawn.class };
        if (isThreatenedByPiece(directions, classList, true))
            return true;

        // Threats from king
        directions = Direction.values();
        classList = new Class<?>[] { King.class };
        if (isThreatenedByPiece(directions, classList, true))
            return true;

        // Threats from rooks and queen
        directions = new Direction[] { Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT };
        classList = new Class<?>[] { Rook.class, Queen.class };
        if (isThreatenedByPiece(directions, classList, false))
            return true;

        // Threats from bishops and queen
        directions = new Direction[] { Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT,
                Direction.DOWN_RIGHT };
        classList = new Class<?>[] { Bishop.class, Queen.class };
        if (isThreatenedByPiece(directions, classList, false))
            return true;

        // Threats from knights
        int[][] knightJumps = Knight.getJumpArray();
        for (int[] jump : knightJumps) {
            Piece piece = getPieceRelativeToPosition(jump);
            if (piece != null && piece.getColour() != this.getColour() && piece instanceof Knight)
                return true;
        }
        return false;
    }

    public boolean isMated() {
        if (!isThreatened()) {
            return false;
        }
        List<Move> moveList = getValidMoves();
        for (Move move : moveList) {
            if (!move.leadsToCheck(board)) {
                return false;
            }
        }
        return true;
    }

    public Rook getCastleRook(boolean longCastle) {
        Colour turn = board.getTurn();
        int horizontalDifference;
        if (turn == Colour.WHITE) {
            horizontalDifference = longCastle ? -4 : 3;
        } else {
            if (board.isBoardRotationEnabled()) {
                horizontalDifference = longCastle ? 4 : -3;
            } else {
                horizontalDifference = longCastle ? -4 : 3;
            }
        }
        Piece rook = getPieceRelativeToPosition(new int[]{ 0, horizontalDifference });
        return (rook instanceof Rook) ? (Rook) rook : null;
    }

    @Override
    public String toString() {
        return colour.toString() + "King";
    }

    private Piece getOpponentPiece(Direction direction, boolean singleMove) {
        List<Move> moveList = getUnidirectionalMoves(direction, getCoordinates(), true);
        if (singleMove && moveList.size() > 0) {
            Move move = moveList.get(0);
            if (move.getType() == Move.MoveType.TAKE) {
                return board.getPiece(move.getToCoordinates());
            }
            return null;
        }
        for (Move move : moveList) {
            if (move.getType() == Move.MoveType.TAKE) {
                return board.getPiece(move.getToCoordinates());
            }
        }
        return null;
    }

    private boolean isThreatenedByPiece(Direction[] directions, Class<?>[] classList, boolean singleMove) {
        for (Direction direction : directions) {
            Piece piece = getOpponentPiece(direction, singleMove);
            for (Class<?> cls : classList) {
                if (piece != null && piece.getClass() == cls)
                    return true;
            }
        }
        return false;
    }

    private boolean canCastle(boolean longCastle) {
        if (hasMoved) {
            return false;
        }
        boolean turnIsWhite = board.getTurn() == Colour.WHITE;
        boolean directionCondition = longCastle;
        if (board.isBoardRotationEnabled()) {
            directionCondition = (! turnIsWhite && ! longCastle) || (turnIsWhite && longCastle);
        }
        int rookDiff = longCastle ? 4 : 3;
        Piece piece = getPieceRelativeToPosition(new int[] { 0, directionCondition ? -rookDiff : rookDiff });

        if (piece == null || !(piece instanceof Rook) || piece.hasMoved()) {
            return false;
        }
        for (int i = 1; i <= rookDiff - 1; i++) {
            int[] toCoordinates = getRelativeCoordinates(new int[]{0, directionCondition ? -i : i});
            Square passingSquare = board.getSquare(toCoordinates);
            Piece pieceAtSquare = passingSquare.getPiece();
            if (longCastle && pieceAtSquare == null && i == rookDiff - 1) {
                break;
            }
            if (pieceAtSquare != null || board.isSquareThreatened(passingSquare)) {
                return false;
            }
        }
        return true;
    }
}
