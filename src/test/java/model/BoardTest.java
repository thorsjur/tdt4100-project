package model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Board.Coordinate;
import chess.model.Colour;
import chess.model.King;
import chess.model.Move;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Queen;
import chess.model.Square;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class BoardTest {

    private Board board;
    private Coordinate whitePawnCoord, nullCoord, blackKingCoord, blackQueenCoord;
    private Piece whitePawn, blackKing, blackQueen, nullPiece;

    @BeforeEach
    public void setup() {
        board = new Board(Colour.WHITE);
        whitePawnCoord = new Coordinate(6, 4);
        nullCoord = new Coordinate(5, 4);
        blackKingCoord = new Coordinate(0, 4);
        blackQueenCoord = new Coordinate(0, 3);

        nullPiece = board.getPiece(nullCoord);
        whitePawn = board.getPiece(whitePawnCoord);
        blackKing = board.getPiece(blackKingCoord);
        blackQueen = board.getPiece(blackQueenCoord);
    }

    private Board getBoard(GameState state) {
        return GameStateLoader.getGame(state).getBoard();
    }

    @Test
    public void testGetPiece() {
        assertTrue(nullPiece == null
                && whitePawn instanceof Pawn
                && blackKing instanceof King
                && blackQueen instanceof Queen);
    }

    @Test
    public void testGetPieceCoordinate() {
        assertEquals(blackQueenCoord, board.getPieceCoordinate(blackQueen));
        assertNull(board.getPieceCoordinate(null));
    }

    @Test
    public void testMovePiece() {
        Coordinate expectedCoordinate = new Coordinate(4, 4);
        board.movePiece(whitePawn, expectedCoordinate);
        assertEquals(expectedCoordinate, board.getPieceCoordinate(whitePawn));

        assertThrows(IllegalArgumentException.class, () -> board.movePiece(whitePawn, expectedCoordinate));
        assertThrows(IllegalArgumentException.class, () -> board.movePiece(whitePawn,
                expectedCoordinate.addVector(new int[] { -1, 0 })));
        assertThrows(IllegalArgumentException.class, () -> board.movePiece(blackQueen, expectedCoordinate));
    }

    @Test
    public void testIsValidMove() {
        assertThrows(IllegalArgumentException.class, () -> board.isValidMove(null));
        assertFalse(board.isValidMove(new Move(
                blackKingCoord,
                new Coordinate(1, 4))));

        assertTrue(board.isValidMove(new Move(
                whitePawnCoord,
                new Coordinate(5, 4))));
    }

    @Test
    public void testResetSquareStates() {
        board.getSquare(whitePawnCoord).setState(Square.State.SELECTED);
        assertTrue(Stream.of(board.getChessBoard())
                .flatMap(row -> Stream.of(row))
                .anyMatch(square -> !(square.getState() == Square.State.DEFAULT)));
        board.resetSquareStates();
        assertFalse(Stream.of(board.getChessBoard())
                .flatMap(row -> Stream.of(row))
                .anyMatch(square -> !(square.getState() == Square.State.DEFAULT)));
    }

    @Test
    public void testIsKingMated() {

        // Sjekker om patt fungerer
        Board stalematedBoard = getBoard(GameState.STALEMATED);
        assertTrue(stalematedBoard.isKingMated(Colour.BLACK, true));
        assertFalse(stalematedBoard.isKingMated(Colour.WHITE, true));

        Board stalemateReadyBoard = getBoard(GameState.STALEMATE_READY);
        assertFalse(stalemateReadyBoard.isKingMated(Colour.WHITE, true));
        stalemateReadyBoard.movePiece(
            stalemateReadyBoard.getPiece(new Coordinate(6, 3)),
            new Coordinate(5, 4));
        assertTrue(stalemateReadyBoard.isKingMated(Colour.BLACK, true));

        // Sjekker om sjakkmatt fungerer
        Board matedBoard = getBoard(GameState.MATED);
        assertTrue(matedBoard.isKingMated(Colour.WHITE, false));
        assertFalse(matedBoard.isKingMated(Colour.BLACK, false));

        Board mateReadyBoard = getBoard(GameState.MATE_READY);
        assertFalse(mateReadyBoard.isKingMated(Colour.WHITE, false));
        mateReadyBoard.movePiece(
            mateReadyBoard.getPiece(new Coordinate(0, 3)),
            new Coordinate(4, 7));
        assertTrue(mateReadyBoard.isKingMated(Colour.WHITE, false));
    }

    @Test
    public void testIsSquareThreatened() {
        Board testBoard = getBoard(GameState.STALEMATE_READY);
        
        // Her er sort konge omringet av 2 hvite dronninger, og har som en konsekvens
        // ingen mulige flytt (er ikke patt enda da det er en sort bonde i spill)
        // For spilltilstanden 'STALEMATE_READY' er kongen på (1, 5)
        King blackKing = (King) testBoard.getPiece(new Coordinate(1, 5));
        assertTrue(blackKing.getValidMoves().stream()
                .map(e -> e.getToCoordinates())
                .allMatch(coord -> testBoard.isSquareThreatened(
                        testBoard.getSquare(coord)
                )));
        
        // Ene dronningen står på (2, 3) og (3, 3) skal ikke være truet for hvit
        assertFalse(testBoard.isSquareThreatened(testBoard.getSquare(new Coordinate(3, 3))));

        // Bytter tur til sort, så nå skal samme ruten bli truet
        testBoard.setTurn(Colour.BLACK);
        assertTrue(testBoard.isSquareThreatened(testBoard.getSquare(new Coordinate(3, 3))));
    }

    @Test
    public void testPromotePawn() {
        Board testBoard = getBoard(GameState.ELIGIBLE_FOR_PROMOTION);

        // Hvorvidt bonden kan promoteres testen i bondens klasse, her skal bare evnen
        // til å promotere testes. Bonden er på (1, 7)
        Pawn pawn = (Pawn) testBoard.getPiece(new Coordinate(1, 7));
        assertThrows(IllegalStateException.class, () -> testBoard.promotePawn(pawn, 'Q'));
        Coordinate arrivalCoordinate = new Coordinate(0, 7);
        testBoard.movePiece((Piece) pawn, arrivalCoordinate);
        assertDoesNotThrow(() -> testBoard.promotePawn(pawn, 'Q'));
        assertTrue(testBoard.getPiece(arrivalCoordinate) instanceof Queen);
        
        Board testBoard2 = getBoard(GameState.ELIGIBLE_FOR_PROMOTION);
        Pawn pawn2 = (Pawn) testBoard2.getPiece(new Coordinate(1, 7));
        testBoard2.movePiece((Piece) pawn2, arrivalCoordinate);
        assertDoesNotThrow(() -> testBoard2.promotePawn(pawn2, 'Æ'));
        assertTrue(testBoard2.getPiece(arrivalCoordinate) instanceof Pawn);

    }
}
