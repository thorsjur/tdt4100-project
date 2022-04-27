package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Knight;
import chess.model.Move;
import chess.model.Board.Coordinate;
import chess.model.Move.MoveType;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class KnightTest {

    private Board knightBoard;

    @BeforeEach
    public void setup() {
        knightBoard = GameStateLoader.getGame(GameState.KNIGHT_TEST).getBoard();
    }

    @Test
    public void testGetValidMoves() {
        // Lader inn et nytt brett, hvor hvit springer er på (3, 6)
        // Her skal det være 4 ledige ruter den kan flytte, og 2 sorte bonder den kan
        // ta.
        // 2 mulige flytt faller bort da de er utenfor brettet
        // mulige flytt til ledig rute skal være til { (2, 4), (4, 4), (5, 5), (5, 7) }
        // og mulige flytt hvor den tar skal være til { (1, 5), (1, 7) }
        Coordinate whiteKnightCoord = new Coordinate(3, 6);
        Knight whiteKnight = (Knight) knightBoard.getPiece(whiteKnightCoord);
        Move[] expectedMoves = {
                new Move(whiteKnightCoord, new Coordinate(2, 4)),
                new Move(whiteKnightCoord, new Coordinate(4, 4)),
                new Move(whiteKnightCoord, new Coordinate(5, 5)),
                new Move(whiteKnightCoord, new Coordinate(5, 7)),
                new Move(whiteKnightCoord, new Coordinate(1, 5)),
                new Move(whiteKnightCoord, new Coordinate(1, 7)),
        };
        assertTrue(whiteKnight.getValidMoves().size() == 6);
        assertTrue(whiteKnight.getValidMoves().stream()
                .allMatch(move -> Arrays.asList(expectedMoves).contains(move)));
        assertTrue(whiteKnight.getValidMoves(MoveType.TAKE).size() == 2);
        knightBoard.movePiece(whiteKnight, expectedMoves[4].getToCoordinates());

        // Sort springer på (0, 1) skal bare ha ett mulig flytt, da (1, 0)-bonden
        // er har flyttet en rute. Mulige flyttet er forventet til å være til (2, 2)
        Coordinate blackKnightCoord = new Coordinate(0, 1);
        Knight blackKnight = (Knight) knightBoard.getPiece(blackKnightCoord);
        assertTrue(blackKnight.getValidMoves().size() == 1);
        assertTrue(blackKnight.getValidMoves().get(0).equals(new Move(blackKnightCoord, new Coordinate(2, 2))));

        // FLytter så den sorte springeren til (2, 2).
        // Nå skal den ha seks mulige flytt til { (0, 1), (1, 0), (3, 0), (4, 1), (4,
        // 3), (3, 4) }
        knightBoard.movePiece(blackKnight, new Coordinate(2, 2));
        blackKnightCoord = blackKnight.getCoordinates();

        Move[] expectedMoves2 = {
                new Move(blackKnightCoord, new Coordinate(0, 1)),
                new Move(blackKnightCoord, new Coordinate(1, 0)),
                new Move(blackKnightCoord, new Coordinate(3, 0)),
                new Move(blackKnightCoord, new Coordinate(4, 1)),
                new Move(blackKnightCoord, new Coordinate(4, 3)),
                new Move(blackKnightCoord, new Coordinate(3, 4))
        };
        assertTrue(blackKnight.getValidMoves().size() == 6);
        assertTrue(blackKnight.getValidMoves().stream()
                .allMatch(move -> Arrays.asList(expectedMoves2).contains(move)));
    }

}
