package chess.model;

import java.util.List;
import java.util.stream.Stream;

import chess.model.Board.Coordinate;

import java.util.ArrayList;

public class King extends Piece {

    public King(Colour colour, Board board) {
        super(colour, board);
        moveDirections = Direction.values();
    }

    @Override
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
                int horizontalDifference = ((board.getTurn() == Colour.WHITE) ? 2 : -2);

                // Hvor kongen skal rokkere er avhengig av rotasjonen til brettet
                if (!board.isBoardRotationEnabled()) {
                    horizontalDifference = (!longCastle ? 2 : -2);
                } else if (longCastle) {
                    horizontalDifference = -horizontalDifference;
                }
                Move castle = new Move(getCoordinates(),
                        getRelativeCoordinates(new int[] { 0, horizontalDifference }),
                        Move.MoveType.CASTLE);
                moveList.add(castle);
            }
        }

        return moveList;
    }

    /*
     * Da det er ganske krevende å gå gjennom alle fiendtlige brikker, og se om noen
     * av de truer kongen, sjekker jeg heller om kongen kan "ta" en fiendtlig brikke
     * tilsvarende til deres måte å ta. Altså for å sjekke om den er truet av en
     * løper, må den sjekke om det finnes noen løpere på diagonalene. Videre gjelder
     * for alle de andre brikkene
     */
    public boolean isThreatened() {
        // Trusler fra bønder
        Direction[] directions = { Direction.UP_LEFT, Direction.UP_RIGHT };
        Class<?>[] classArray = { Pawn.class };
        if (isThreatenedByPiece(directions, classArray, true))
            return true;

        // Trusler fra motstanders konge
        directions = Direction.values();
        classArray = new Class<?>[] { King.class };
        if (isThreatenedByPiece(directions, classArray, true))
            return true;

        // Trusler fra motstanders dronning og tårn
        directions = new Direction[] { Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT };
        classArray = new Class<?>[] { Rook.class, Queen.class };
        if (isThreatenedByPiece(directions, classArray, false))
            return true;

        // Trusler fra motstanders løper og dronning
        directions = new Direction[] { Direction.UP_LEFT, Direction.UP_RIGHT, Direction.DOWN_LEFT,
                Direction.DOWN_RIGHT };
        classArray = new Class<?>[] { Bishop.class, Queen.class };
        if (isThreatenedByPiece(directions, classArray, false))
            return true;

        // Trusler fra motstanders springere
        int[][] knightJumps = Knight.getJumpArray();
        for (int[] jump : knightJumps) {
            Piece piece = getPieceRelativeToPosition(jump);
            if (piece != null && !this.equalsColourOf(piece) && piece instanceof Knight)
                return true;
        }
        return false;
    }

    public boolean isMated(boolean stalemate) {
        if ((!isThreatened() && !stalemate) || getValidMoves().stream().anyMatch(move -> !move.leadsToCheck(board))) {
            return false;
        }

        return Stream.of(board.getGrid())
                .flatMap(row -> Stream.of(row))
                .filter(piece -> piece != null && this.equalsColourOf(piece))
                .map(piece -> piece.getValidMoves())
                .flatMap(list -> list.stream())
                .allMatch(move -> move.leadsToCheck(board));
    }

    public Rook getCastleRook(boolean longCastle) {
        if (hasMoved())
            return null;
        Colour turn = board.getTurn();
        int horizontalDifference;
        if (turn == Colour.WHITE) {
            horizontalDifference = (longCastle ? -4 : 3);
        } else {
            if (board.isBoardRotationEnabled()) {
                horizontalDifference = (longCastle ? 4 : -3);
            } else {
                horizontalDifference = (longCastle ? -4 : 3);
            }
        }
        Piece rook = getPieceRelativeToPosition(new int[] { 0, horizontalDifference });
        return ((rook instanceof Rook) ? (Rook) rook : null);
    }

    @Override
    public String toString() {
        return colour.toString() + "King";
    }

    private Piece getOpponentPiece(Direction direction, boolean singleMove) {
        List<Move> moveList = getUnidirectionalMoves(direction, getCoordinates(), true);

        // Hvis brikker kan bare flytte en rute, må første flyttet i flyttlisten være av
        // typen 'Take'. Dette fungerer da metoden for å finne mulige flytt arbeider seg
        // fra brikken og utover
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

    /*
     * Denne metoden sjekker hvorvidt kongen trues av en av de gitte typene i den
     * gitte retningen. Her kan man også oppgi om det fiendtlige brikken bare kan ta
     * en rute fra seg selv
     */
    private boolean isThreatenedByPiece(Direction[] directions, Class<?>[] classArray, boolean singleMove) {
        for (Direction direction : directions) {
            Piece piece = getOpponentPiece(direction, singleMove);
            for (Class<?> cls : classArray) {
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
            directionCondition = (!turnIsWhite && !longCastle) || (turnIsWhite && longCastle);
        }
        int rookDiff = (longCastle ? 4 : 3);
        Piece piece = getPieceRelativeToPosition(new int[] { 0, (directionCondition ? -rookDiff : rookDiff) });

        if (piece == null || !(piece instanceof Rook) || piece.hasMoved()) {
            return false;
        }
        for (int i = 1; i <= rookDiff - 1; i++) {
            Coordinate toCoordinates = getRelativeCoordinates(new int[] { 0, (directionCondition ? -i : i) });
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
