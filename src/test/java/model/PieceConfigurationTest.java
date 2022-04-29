package model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board;
import chess.model.Colour;
import chess.model.King;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.PieceConfiguration;
import chess.model.Rook;

public class PieceConfigurationTest {

    private PieceConfiguration pc;

    @BeforeEach
    public void setup() {
        Board board = new Board(Colour.WHITE);
        pc = board.getPieceConfiguration();
    }

    // Tester at iterator traverserer brettet i riktig rekkefølge
    @Test
    public void testIterator() {
        Iterator<Piece> iterator = pc.iterator();
        Piece expectedBlackRook = iterator.next();
        assertTrue(expectedBlackRook instanceof Rook && expectedBlackRook.getColour() == Colour.BLACK);
        for (int i = 0; i < 23; i++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }
        Piece nullPiece = iterator.next();
        assertNull(nullPiece);

        for (int i = 0; i < 24; i++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }

        Piece expectedWhitePawn = iterator.next();
        assertTrue(expectedWhitePawn instanceof Pawn && expectedWhitePawn.getColour() == Colour.WHITE);

        for (int i = 0; i < 6; i++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }
        Piece expectedWhiteRook = iterator.next();
        assertTrue(expectedWhiteRook instanceof Rook && expectedWhiteRook.getColour() == Colour.WHITE);

        for (int i = 0; i < 3; i++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }

        Piece expectedWhiteKing = iterator.next();
        assertTrue(expectedWhiteKing instanceof King && expectedWhiteKing.getColour() == Colour.WHITE);

        for (int i = 0; i < 3; i++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

}
