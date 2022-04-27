package model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Colour;
import chess.model.King;
import chess.model.Knight;
import chess.model.Move;
import chess.model.Piece;
import chess.model.Rook;
import chess.model.Board.Coordinate;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class KingTest {

    private Board board, boardOne, boardTwo, castleBoard;

    @BeforeEach
    public void setup() {
        this.board = new Board(Colour.WHITE);
        this.boardOne = GameStateLoader.getGame(GameState.KING_TEST_ONE).getBoard();
        this.boardTwo = GameStateLoader.getGame(GameState.KING_TEST_TWO).getBoard();
        this.castleBoard = GameStateLoader.getGame(GameState.CASTLE).getBoard();
    }

    private King getKing(Board board, Colour colour) {
        return (King) Stream.of(board.getGrid())
                .flatMap(row -> Stream.of(row))
                .filter(piece -> piece != null && piece instanceof King && piece.getColour() == colour)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Test
    public void testGetValidMoves() {
        King whiteKing = getKing(board, Colour.WHITE);
        Coordinate whiteKingCoord = whiteKing.getCoordinates();

        assertTrue(whiteKing.getValidMoves().isEmpty());
        Piece kingsPawn = board.getPiece(whiteKingCoord.addVector(new int[] { -1, 0 }));
        board.movePiece(kingsPawn, kingsPawn.getCoordinates().addVector(new int[] { -2, 0 }));
        assertTrue(whiteKing.getValidMoves().size() == 1);
        assertTrue(whiteKing.getValidMoves().get(0)
                .equals(new Move(whiteKingCoord, whiteKingCoord.addVector(new int[] { -1, 0 }))));

        // Lader inn et brett hvor hvit konge er omringet av 3 sorte bønder og
        // en hvit bonde, og ser om den har 3 flytt hvor den tar og 4 flytt som den kan
        // bevege seg
        // Merk denne metoden ikke tar hensyn til om kongen settes i sjakk av flyttet,
        // da det håndteres en annen sted i koden ...
        King whiteKing2 = getKing(boardOne, Colour.WHITE);
        Coordinate whiteKing2Coord = whiteKing2.getCoordinates();

        assertTrue(whiteKing2.getValidMoves().size() == 7);
        assertTrue(whiteKing2.getValidMoves().stream()
                .filter(move -> move.getType() == Move.MoveType.TAKE)
                .count() == 3);
        assertTrue(whiteKing2.getValidMoves().stream()
                .filter(move -> move.getType() == Move.MoveType.MOVE)
                .count() == 4);

        // Gjennomfører en stikkprøve
        assertTrue(whiteKing2.getValidMoves().stream()
                .anyMatch(move -> move.getToCoordinates().equals(new Coordinate(2, 4))));

        // Ønsker også teste om den den kan hente mulige valide rokkeringer
        // Lader først inn et brett hvor både kongen og begge tårn ikke har flyttet
        // siden start og alle ruter imellom er ledig og ikke truet.
        King whiteKingCastle = getKing(castleBoard, Colour.WHITE);
        King blackKingCastle = getKing(castleBoard, Colour.BLACK);

        // Nå skal begge kongene ha oppfylt kravene for både kort og lang rokkering
        assertTrue(whiteKingCastle.getValidMoves().stream()
                .filter(move -> move.getType() == Move.MoveType.CASTLE)
                .count() == 2);
        castleBoard.setTurn(Colour.BLACK);
        assertTrue(blackKingCastle.getValidMoves().stream()
                .filter(move -> move.getType() == Move.MoveType.CASTLE)
                .count() == 2);
        castleBoard.setTurn(Colour.WHITE);

        // Flytter en bonde, slik at ruten til venstre for hvit dronning nå
        // er truet, og dermed skal bare kort rokkering være lov
        // dette på grunn av en av rutene kongen skal over er truet
        castleBoard.movePiece(castleBoard.getPiece(new Coordinate(6, 2)), new Coordinate(5, 2));
        castleBoard.setTurn(Colour.WHITE);
        assertTrue(whiteKingCastle.getValidMoves().stream()
                .filter(move -> move.getType() == Move.MoveType.CASTLE)
                .count() == 1);
        assertTrue(whiteKingCastle.getValidMoves().stream()
                .filter(move -> move.getType() == Move.MoveType.CASTLE)
                .findFirst()
                .orElseThrow(IllegalStateException::new).getToCoordinates().column() == 6);
    }

    @Test
    public void testIsThreatened() {
        // 'boardOne' brettet starter med at kongen ikke er truet, men ved å flytte den
        // hvite kongen og en sort bonde skal den være truet av bonden.
        King whiteKing = getKing(boardOne, Colour.WHITE);
        Coordinate whiteKingCoord = whiteKing.getCoordinates();

        assertFalse(whiteKing.isThreatened());
        boardOne.movePiece(whiteKing, whiteKingCoord.addVector(new int[] { 1, 1 }));
        boardOne.movePiece(boardOne.getPiece(whiteKingCoord.addVector(new int[] { -1, 0 })), whiteKingCoord);

        // Nå skal kongen være truet av en sort bonde
        assertTrue(whiteKing.isThreatened());

        // Lader inn et nytt brett, hvor hvit konge er ikke truet, men ved å flytte
        // sort springer (2, 2) til (4, 3) skal bli truet.
        King whiteKing2 = getKing(boardTwo, Colour.WHITE);
        assertFalse(whiteKing2.isThreatened());
        Knight knight = (Knight) boardTwo.getPiece(new Coordinate(2, 2));
        boardTwo.movePiece(knight, new Coordinate(4, 3));
        assertTrue(whiteKing2.isThreatened());
    }

    @Test
    public void testGetCastleRook() {
        // Denne metoden har bare å finne relevant tårn, og sjekker ikke
        // hvorvidt det er mulig å rokkere, og krever derfor lite testing
        King whiteKing = getKing(boardTwo, Colour.WHITE);
        King blackKing = getKing(boardOne, Colour.BLACK);
        
        // Hvit konge har bevegd seg, og som en konsekvens vil ikke ha
        // tårn til å kunne rokkere, mens kongen har ikke bevegd seg
        assertNull(whiteKing.getCastleRook(false));
        assertNull(whiteKing.getCastleRook(true));

        assertTrue(blackKing.getCastleRook(true) instanceof Rook);
        assertTrue(blackKing.getCastleRook(false) instanceof Rook);
    }

}
