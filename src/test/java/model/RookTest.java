package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Rook;
import chess.model.Board.Coordinate;
import chess.model.Move.MoveType;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class RookTest {

    private Board board;

    @BeforeEach
    public void setup() {
        board = GameStateLoader.getGame(GameState.ROOK_TEST).getBoard();
    }

    @Test
    public void testGetValidMoves() {
        // Tårn skal bare kunne bevege/ta på horisontalen.
        // På brettet som er lastet inn er det et hvitt tårn på (2, 7)
        // som forventes til å kunne ta to sorte brikker på { (0, 7), (2, 5) }
        // og kunne flytte til de ledige rutene på { (1, 7), (2, 6), (3, 7) (4, 7) }
        Coordinate whiteRookCoord = new Coordinate(2, 7);
        Rook whiteRook = (Rook) board.getPiece(whiteRookCoord);

        assertTrue(whiteRook.getValidMoves(MoveType.TAKE).size() == 2);
        assertTrue(whiteRook.getValidMoves(MoveType.MOVE).size() == 4);
        Coordinate[] expectedTakeToCoordinates = {
            new Coordinate(0, 7),
            new Coordinate(2, 5)
        };
        Coordinate[] expectedMoveToCoordinates = {
            new Coordinate(1, 7),
            new Coordinate(2, 6),
            new Coordinate(3, 7),
            new Coordinate(4, 7)
        };
        assertTrue(whiteRook.getValidMoves(MoveType.TAKE).stream()
                        .map(move -> move.getToCoordinates())
                        .allMatch(coord -> Arrays.asList(expectedTakeToCoordinates).contains(coord)));
        assertTrue(whiteRook.getValidMoves(MoveType.MOVE).stream()
                        .map(move -> move.getToCoordinates())
                        .allMatch(coord -> Arrays.asList(expectedMoveToCoordinates).contains(coord)));
    }

}
