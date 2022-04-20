package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.io.GameReaderWriter;
import chess.model.Board;
import chess.model.Colour;
import chess.model.Game;
import chess.model.King;
import chess.model.Move;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.Queen;
import chess.model.Board.Coordinate;
import chess.model.Game.GameData;
import chess.model.Square;

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

    private enum GameState {
        MATED, MATE_READY, STALEMATED, STALEMATE_READY, PAWN_PROMOTION_READY
    }

    private static Game getGame(GameState state) {
        GameReaderWriter grw = new GameReaderWriter();
        List<GameData> gameData;
        try {
            gameData = grw.getData(null);
        } catch (IOException e) {
            System.out.println("Unable to load game");
            return null;
        }

        System.out.println(gameData);
        return null;
        
    }

    public static void main(String[] args) {
        getGame(null);
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

    }
}
