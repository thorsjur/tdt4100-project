package model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import chess.model.Board.Coordinate;
import chess.model.GameManager;
import chess.model.Pawn;
import chess.model.Square;

/*
 * Med unntak av metoden selectSquare er det veldig lite logikk
 * som bør testes i klassen ...
 */
@TestInstance(Lifecycle.PER_CLASS)
public class GameManagerTest {

    private GameManager gm;

    @BeforeAll
    public void setup() {
        gm = new GameManager();
        gm.startNewGame("", "", false);
    }


    @Test
    public void testSelectSquare() {
        assertTrue(getNumberOfSelectedSquares() == 0);

        // Det skal bare være mulig å velge egne brikker
        // Det er hvit tur, og (1, 3) er en sort bonde og kan derfor ikke velges
        gm.selectSquare(1, 3);
        assertTrue(getNumberOfSelectedSquares() == 0);

        // en hvit brikke er derimot et gyldig valg
        gm.selectSquare(6, 4);
        assertTrue(getNumberOfSelectedSquares() == 1);

        // hvis du så prøver å velge samme rute eller en ugyldig rute skal den bli
        // ikke valgt
        gm.selectSquare(6, 4);
        assertTrue(getNumberOfSelectedSquares() == 0);

        gm.selectSquare(6, 4);
        gm.selectSquare(1, 4);
        assertTrue(getNumberOfSelectedSquares() == 0);

        // Sjekker om den velger rett rute
        Coordinate c = new Coordinate(7, 3);
        gm.selectSquare(c.row(), c.column());
        assertTrue(c.equals(findSelectedSquare().getCoordinate()));

        // Hvis du velger et gyldig flytt (altså først brikken, og så 'målet') skal
        // den gjøre flyttet
        gm.selectSquare(6, 5);
        Coordinate arrivalCoord = new Coordinate(5, 5);
        assertNull(gm.getGame().getBoard().getPiece(arrivalCoord));
        gm.selectSquare(arrivalCoord.row(), arrivalCoord.column());
        assertTrue(gm.getGame().getBoard().getPiece(arrivalCoord) instanceof Pawn);
        
        // Metoden skal også returnere ruten som ble trykket på, uavhengig
        // om det er gyldig eller ikke
        assertTrue(gm.selectSquare(1, 3) == findSelectedSquare());
    }

    private int getNumberOfSelectedSquares() {
        return (int) Stream.of(gm.getGame().getBoard().getChessBoard())
            .flatMap(row -> Stream.of(row))
            .filter(square -> square.getState() == Square.State.SELECTED)
            .count();
    }

    private Square findSelectedSquare() {
        return Stream.of(gm.getGame().getBoard().getChessBoard())
            .flatMap(row -> Stream.of(row))
            .filter(square -> square.getState() == Square.State.SELECTED)
            .findFirst()
            .orElse(null);
    }
}
