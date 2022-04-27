package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Queen;
import chess.model.Board.Coordinate;
import chess.model.Move.MoveType;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class QueenTest {

    private Board board;

    @BeforeEach
    public void setup() {
        board = GameStateLoader.getGame(GameState.QUEEN_TEST).getBoard();
    }

    @Test
    public void testGetValidMoves() {
        // Dronning kan gå så mange ruter mulig diagonalt, vertikalt og horisontalt
        // så lenge den ikke blokkeres av egne eller motstanders brikker.
        // Den kan også ta så lenge en motstanders brikke ligger i bevegelsesretningen

        // Bruker et brett hvor hvit dronning er på (2, 5).
        // Det er forventet at den kan ta sorte brikker på { (0, 5), (0, 7), (1, 4), (2,
        // 3), (2, 6), (3, 4) }
        // Den har også ledige flytt til { (1, 5), (1, 6), (2, 4), (3, 5), (3, 6), (4,
        // 5), (4, 7), 5, 5 }
        Coordinate whiteQueenCoord = new Coordinate(2, 5);
        Queen whiteQueen = (Queen) board.getPiece(whiteQueenCoord);

        assertTrue(whiteQueen.getValidMoves(MoveType.TAKE).size() == 6);
        assertTrue(whiteQueen.getValidMoves(MoveType.MOVE).size() == 8);
        Coordinate[] expectedTakeToCoordinates = {
                new Coordinate(0, 5),
                new Coordinate(0, 7),
                new Coordinate(1, 4),
                new Coordinate(2, 3),
                new Coordinate(2, 6),
                new Coordinate(3, 4)
        };
        Coordinate[] expectedMoveToCoordinates = {
                new Coordinate(1, 5),
                new Coordinate(1, 6),
                new Coordinate(2, 4),
                new Coordinate(3, 5),
                new Coordinate(3, 6),
                new Coordinate(4, 5),
                new Coordinate(4, 7),
                new Coordinate(5, 5)
        };

        assertTrue(whiteQueen.getValidMoves(MoveType.TAKE).stream()
                .map(move -> move.getToCoordinates())
                .allMatch(coord -> Arrays.asList(expectedTakeToCoordinates).contains(coord)));
        assertTrue(whiteQueen.getValidMoves(MoveType.MOVE).stream()
                .map(move -> move.getToCoordinates())
                .allMatch(coord -> Arrays.asList(expectedMoveToCoordinates).contains(coord)));
        board.movePiece(whiteQueen, expectedTakeToCoordinates[2]);

        // Videre testes den sorte dronningen på (3, 2)
        Coordinate blackQueenCoord = new Coordinate(3, 2);
        Queen blackQueen = (Queen) board.getPiece(blackQueenCoord);

        // Dronningen forventes å kunne ta hvite brikker på { (4, 2), (5, 4) }
        // Den skal flytte til ledige ruter på { (2, 1), (2, 2), (3, 0), (3, 1), (3, 3),
        // (4, 1), (4, 3), (5, 0) }

        assertTrue(blackQueen.getValidMoves(MoveType.TAKE).size() == 2);
        assertTrue(blackQueen.getValidMoves(MoveType.MOVE).size() == 8);
        Coordinate[] expectedTakeToCoordinatesBQ = {
            new Coordinate(4, 2),
            new Coordinate(5, 4)
        };
        Coordinate[] expectedMoveToCoordinatesBQ = {
                new Coordinate(2, 1),
                new Coordinate(2, 2),
                new Coordinate(3, 0),
                new Coordinate(3, 1),
                new Coordinate(3, 3),
                new Coordinate(4, 1),
                new Coordinate(4, 3),
                new Coordinate(5, 0)
        };
        assertTrue(blackQueen.getValidMoves(MoveType.TAKE).stream()
                .map(move -> move.getToCoordinates())
                .allMatch(coord -> Arrays.asList(expectedTakeToCoordinatesBQ).contains(coord)));
        assertTrue(blackQueen.getValidMoves(MoveType.MOVE).stream()
                .map(move -> move.getToCoordinates())
                .allMatch(coord -> Arrays.asList(expectedMoveToCoordinatesBQ).contains(coord)));

    }
}
