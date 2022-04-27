package chess.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chess.model.Board.Coordinate;
import chess.model.Move.MoveType;

public class Pawn extends Piece {

    private boolean recentlyMovedTwoSquares;

    public Pawn(Colour colour, Board board) {
        super(colour, board);
        this.recentlyMovedTwoSquares = false;
    }

    @Override
    public List<Move> getValidMoves() {
        List<Move> moveList = new ArrayList<Move>();

        for (List<Move> moves : Arrays.asList(getPawnMoves(), getPawnTakes(), getEnPassantMoves())) {
            moveList.addAll(moves);
        }

        return moveList;
    }

    public boolean canPromote() {
        Coordinate coordinates = getCoordinates();
        return coordinates.row() == 0 || coordinates.row() == 7;
    }

    public void resetRecentlyMovedTwoSquares() {
        this.recentlyMovedTwoSquares = false;
    }

    private List<Move> getPawnMoves() {
        List<Move> moveList = getUnidirectionalMoves(Direction.UP, getCoordinates(), false);
        if (hasMoved == false && moveList.size() >= 2) {
            return moveList.subList(0, 2);
        } else if (moveList.size() >= 1) {
            if (!hasMoved) {
                return moveList.subList(0, 1);
            } else {
                return new ArrayList<Move>(Arrays.asList(moveList.get(0)));
            }
        }
        return new ArrayList<Move>();
    }

    private List<Move> getPawnTakes() {
        List<Move> takeList = new ArrayList<Move>();

        for (Direction direction : new Direction[] { Direction.UP_LEFT, Direction.UP_RIGHT }) {
            List<Move> tempTakeList = getUnidirectionalMoves(direction, getCoordinates(), true);
            if (tempTakeList.size() > 0 && tempTakeList.get(0).getType() == Move.MoveType.TAKE) {
                takeList.add(tempTakeList.get(0));
            }
        }
        return takeList;
    }

    private List<Move> getEnPassantMoves() {
        // Først finner ut om UP_LEFT eller UP_RIGHT, og 1x DOWN har nettopp flyttet 2
        // Hvis de har, gir det et lovlig flytt UP_LEFT/UP_RIGHT
        List<Move> enPassantList = new ArrayList<Move>();

        Direction[] directionArr = { Direction.UP_LEFT, Direction.UP_RIGHT };
        for (Direction direction : directionArr) {
            int[] directionVector = direction.getDirectionVector(board);
            Piece piece;
            try {
                Coordinate opponentPieceCoordinate = getCoordinates()
                    .addVector(directionVector)
                    .addVector(Direction.DOWN.getDirectionVector(board));
                piece = board.getPiece(opponentPieceCoordinate);
            } catch (IndexOutOfBoundsException e) {
                piece = null;
            }
            
            if (!(piece instanceof Pawn) || !((Pawn) piece).hasRecentlyMovedTwoSquares() || equalsColourOf(piece)) {
                continue;
            }
            enPassantList.add(new Move(getCoordinates(), getCoordinates().addVector(directionVector), MoveType.EN_PASSANT));
            
        }
        return enPassantList;
    }

    private boolean hasRecentlyMovedTwoSquares() {
        return recentlyMovedTwoSquares;
    }

    @Override
    public void registerMove() {
        // Denne sjekken er til kun for lesing av fil, hvor hver brikke skal registrerer hvorvidt de har bevegd seg
        // Da ønsker en ikke å finne ut hvorvidt den netopp har flyttet seg x2 ruter
        if (board != null) {

            // Brukes til En Passant, er veldig lite krevende å sjekke bare hvor den er på brettet,
            // og om dette er første flyttet for å finne ut om det er et dobbelflytt
            this.recentlyMovedTwoSquares = (hasMoved == false
                && (getCoordinates().row() == 3 || getCoordinates().row() == 4)
                );
        }
        super.registerMove();
    }

    @Override
    public String toString() {
        return colour.toString() + "Pawn";
    }
}