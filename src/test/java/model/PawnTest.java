package model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Board.Coordinate;
import chess.model.Move;
import chess.model.Move.MoveType;
import chess.model.Pawn;
import chess.model.Piece;
import util.GameStateLoader;
import util.GameStateLoader.GameState;

public class PawnTest {

    private Board pawnBoard, enPassantBoard;

    @BeforeEach
    public void setup() {
        pawnBoard = GameStateLoader.getGame(GameState.PAWN_TEST).getBoard();
        enPassantBoard = GameStateLoader.getGame(GameState.EN_PASSANT).getBoard();
    }

    @Test
    public void testGetValidMoves() {
        // Bonde har i all hovedsak muligheten til å flytte en frem (men ikke ta),
        // med unntak at det er dens første flytt. Da skal den kunne flytte 1/2
        // Den kan ta diagonalt en rute oppover fra seg selv.
        // Den skal også finne ut som den kan gjennomføre En passant, altså at den kan
        // ta en bonde som har gjennomført dobbeltflytt, som on den bare hadde tatt et
        // enkeltflytt.

        // Bruker brettet pawnBoard hvor det er bl. a. en sort bonde på (2, 2)
        // Denne bonden har flyttet allerede, og kan derfor bare flytte 1 frem.
        // Det ligger en hvit bonde på (3, 1) som den skal kunne ta.
        // Altså er det bare to mulige flytt, ta bonde eller gå ett steg frem
        Coordinate blackPawnCoord = new Coordinate(2, 2);
        Pawn blackPawn = (Pawn) pawnBoard.getPiece(blackPawnCoord);
        assertTrue(blackPawn.getValidMoves().size() == 2);
        assertTrue(blackPawn.getValidMoves(MoveType.TAKE).size() == 1);
        assertTrue(blackPawn.getValidMoves(MoveType.TAKE).get(0).getToCoordinates().equals(new Coordinate(3, 1)));
        assertTrue(blackPawn.getValidMoves(MoveType.MOVE).get(0).getToCoordinates().equals(new Coordinate(3, 2)));

        pawnBoard.movePiece(blackPawn, new Coordinate(3, 1));

        // Bytter nå til hvit tur, hvor det er en hvit bonde på (6, 6)
        // som ikke har beveget seg. Den kan ikke ta noen sorte brikker,
        // men den har to mulige flytt til (5, 6) og (4, 6)
        Coordinate whitePawnCoord = new Coordinate(6, 6);
        Pawn whitePawn = (Pawn) pawnBoard.getPiece(whitePawnCoord);
        List<Move> moveList = whitePawn.getValidMoves();

        assertTrue(moveList.size() == 2);
        assertTrue(moveList.size() == whitePawn.getValidMoves(MoveType.MOVE).size());

        Coordinate[] expectedToCoordinates = {
                new Coordinate(5, 6),
                new Coordinate(4, 6)
        };
        assertTrue(moveList.stream()
                .map(move -> move.getToCoordinates())
                .allMatch(coord -> Arrays.asList(expectedToCoordinates).contains(coord)));

        // Videre skal evnen til å gjenkjenne gyldig en passant bli testet
        // Bruker brettet 'enPassantBoard', hvor det er en sort bonde på (4, 3)
        // og en hvit bonde på (4, 4) som har flyttet to hakk frem
        // Det er nå hvit turn, så muligheten for å bruke en passant for sort bonde er
        // over
        // Det er forventet ved å flytte hvit bonde på (6, 2) til (4, 2) at sort bonde
        // skal kunne bruke en passant, og bare på den.
        Pawn whiteEpPawn = (Pawn) enPassantBoard.getPiece(new Coordinate(6, 2));
        Pawn blackEpPawn = (Pawn) enPassantBoard.getPiece(new Coordinate(4, 3));
        enPassantBoard.movePiece(whiteEpPawn, new Coordinate(4, 2));

        assertTrue(blackEpPawn.getValidMoves(MoveType.EN_PASSANT).size() == 1);
        assertTrue(blackEpPawn.getValidMoves(MoveType.EN_PASSANT).get(0).getToCoordinates().equals(new Coordinate(5, 2)));

        // Gjennomfører to "dummyflytt" for å teste om bonden mister evnen til en passant
        Piece blackDummyPawn = enPassantBoard.getPiece(new Coordinate(1, 7));
        Piece whiteDummyPawn = enPassantBoard.getPiece(new Coordinate(6, 0));
        enPassantBoard.movePiece(blackDummyPawn, new Coordinate(2, 7));
        enPassantBoard.movePiece(whiteDummyPawn, new Coordinate(5, 0));

        // Nå skal den opprinnelige sorte bonden ikke ha mulighet til en passant lengre
        assertTrue(blackEpPawn.getValidMoves(MoveType.EN_PASSANT).isEmpty());
    }
}
