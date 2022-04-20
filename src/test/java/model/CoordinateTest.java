package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chess.model.Board.Coordinate;

public class CoordinateTest {

    private Coordinate validCoordinate;

    @BeforeEach
    public void setup() {
        validCoordinate = new Coordinate(1, 3);
    }

    @Test
    public void testConstructor() {
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(2, -3));
        assertThrows(IndexOutOfBoundsException.class, () -> new Coordinate(5, 8));
    }

    @Test
    public void testAdd() {
        Coordinate expectedResult = new Coordinate(3, 6);
        Coordinate additionCoord = new Coordinate(2, 3);
        assertEquals(expectedResult, validCoordinate.add(additionCoord));
    }
    
    @Test
    public void testSubtract() {
        Coordinate expectedResult = new Coordinate(0, 1);
        Coordinate additionCoord = new Coordinate(1, 2);
        assertEquals(expectedResult, validCoordinate.subtract(additionCoord));
    }
    
}
