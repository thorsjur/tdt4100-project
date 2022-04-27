package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Bishop;
import chess.model.Board;
import chess.model.Board.Coordinate;
import chess.model.Move;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class BishopTest {

    private Board board;

    @BeforeEach
    public void setup() {
        board = GameStateLoader.getGame(GameState.BISHOP_TEST).getBoard();
    }

    @Test
    public void testGetValidMoves() {
        // Sort løper står på (3, 2) og kan bare flytte diagonalt
        // den har ett flytt opp til venstre, før den skal stoppes av sort bonde,
        // tre flytt opp til høyre, to flytt ned til høyre og en hvor den tar
        // og to flytt ned til venstre før den når kanten.
        // Bonden den kan skal være på (6, 5)
        Bishop bishop = (Bishop) board.getPiece(new Coordinate(3, 2));
        assertTrue(bishop.getValidMoves().size() == 9);
        assertTrue(bishop.getValidMoves(Move.MoveType.TAKE).size() == 1);
        assertTrue(bishop.getValidMoves(Move.MoveType.TAKE).get(0).getToCoordinates().equals(new Coordinate(6, 5)));
    }

}
