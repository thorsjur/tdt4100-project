package io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import chess.io.GameReaderWriter;
import chess.model.Board;
import chess.model.Board.Coordinate;
import chess.model.Game;
import chess.model.Piece;
import chess.model.PieceConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
public class GameReaderWriterTest {

    private final static String DIRECTORY_PATH = "src/test/resources/temp/";
    private final static String FILE_EXTENSION = ".txt";
    private final File tempFile = new File(DIRECTORY_PATH + "temp_game" + FILE_EXTENSION);
    private GameReaderWriter grw;
    private Game expectedGame1, expectedGame2;

    @BeforeAll
    public void setup() throws IOException {
        grw = new GameReaderWriter(tempFile);
        tempFile.createNewFile();

        setupGame1();
        setupGame2();
    }

    @AfterAll
    public void tearDown() throws IOException {
        tempFile.delete();
    }

    @Test
    public void testConstructor() {
        GameReaderWriter grw1 = new GameReaderWriter();
        final String FILE_PATH = "src\\main\\resources\\games\\games.txt";
        assertTrue(grw1.getFilePath().equals(FILE_PATH));

        assertThrows(IllegalArgumentException.class, () -> new GameReaderWriter("test.png"));
        GameReaderWriter grw2 = new GameReaderWriter("test");
        assertTrue(grw2.getFilePath().endsWith(".txt"));

        assertThrows(IllegalArgumentException.class, () -> new GameReaderWriter(new File("")));
        assertThrows(IllegalArgumentException.class, () -> new GameReaderWriter(new File(DIRECTORY_PATH)));
        assertThrows(IllegalArgumentException.class, () -> new GameReaderWriter(new File(DIRECTORY_PATH + "NAME")));
        assertDoesNotThrow(() -> new GameReaderWriter(new File(DIRECTORY_PATH + "NAME" + FILE_EXTENSION)));
        assertThrows(IllegalArgumentException.class,
                () -> new GameReaderWriter(new File(DIRECTORY_PATH + "NAME" + ".png")));
    }

    @Test
    public void saveTest() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> grw.save(null));
        grw.save(expectedGame1);
        grw.save(expectedGame2);
    }

    @Test
    public void getDataTest() throws IOException {
        assertDoesNotThrow(() -> grw.getData(null));
        assertDoesNotThrow(() -> grw.getData(new File("NOT A REAL PATH")));

        Game actualGame1 = grw.load(grw.getData(tempFile).get(0));
        assertTrue(compareGames(expectedGame1, actualGame1));

        Game actualGame2 = grw.load(grw.getData(tempFile).get(1));
        assertTrue(compareGames(expectedGame2, actualGame2));
    }

    private Game getNewGame() {
        return new Game("", "");
    }

    private boolean compareGames(Game expectedGame, Game actualGame) {
        return (expectedGame.getTurn() == actualGame.getTurn()
                && expectedGame.getWinner() == actualGame.getWinner()
                && compareBoards(expectedGame.getBoard(), actualGame.getBoard()));
    }

    private boolean compareBoards(Board expectedBoard, Board actualBoard) {
        return comparePieces(expectedBoard.getGrid(), actualBoard.getGrid())
                && expectedBoard.getTurn() == actualBoard.getTurn()
                && compareAllPieceConfigurations(expectedBoard.getPieceConfiguration(),
                        actualBoard.getPieceConfiguration());
    }

    private boolean comparePieces(Piece[][] expectedGrid, Piece[][] actualGrid) {
        if (expectedGrid.length != actualGrid.length) {
            return false;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece expectedPiece = expectedGrid[i][j];
                Piece actualPiece = actualGrid[i][j];

                if (expectedPiece != null && actualPiece != null
                        && (expectedPiece.getClass() != actualPiece.getClass()
                                || expectedPiece.hasMoved() != actualPiece.hasMoved()
                                || expectedPiece.getColour() != actualPiece.getColour())) {
                    return false;
                } else if (expectedPiece == null && actualPiece != null
                        || expectedPiece != null && actualPiece == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareAllPieceConfigurations(PieceConfiguration expectedPC, PieceConfiguration actualPC) {
        expectedPC = expectedPC.getRoot();
        actualPC = actualPC.getRoot();

        if (!comparePieceConfigurations(expectedPC, actualPC))
            return false;
        while (expectedPC.hasNextGame()) {
            expectedPC = expectedPC.getNextGame();
            actualPC = actualPC.getNextGame();

            if (!comparePieceConfigurations(expectedPC, actualPC))
                return false;
        }
        return true;
    }

    private boolean comparePieceConfigurations(PieceConfiguration expectedPC, PieceConfiguration actualPC) {
        if (!comparePieces(expectedPC.getPieceGrid(), actualPC.getPieceGrid())
                || expectedPC.isBoardRotated() != actualPC.isBoardRotated()
                || expectedPC.getTurn() != actualPC.getTurn()) {
            return false;
        }
        return true;
    }

    private void setupGame1() {
        expectedGame1 = getNewGame();
        Board board = expectedGame1.getBoard();

        // Utfører en rekke flytt på et tomt brett, slik at det kan lagres og
        // deretter sammenlignes med det som leses
        // Henter først inn alle brikker jeg skal bruke:
        Piece whiteKingPawn = board.getPiece(new Coordinate(6, 4));
        Piece blackQueenPawn = board.getPiece(new Coordinate(1, 3));
        Piece whiteQueen = board.getPiece(new Coordinate(7, 3));
        Piece blackQueen = board.getPiece(new Coordinate(0, 3));
        Object[][] moveInfo = {
                { whiteKingPawn, new Coordinate(4, 4) },
                { blackQueenPawn, new Coordinate(3, 3) },
                { whiteKingPawn, new Coordinate(3, 3) },
                { blackQueen, new Coordinate(3, 3) },
                { whiteQueen, new Coordinate(5, 5) },
                { blackQueen, new Coordinate(5, 5) },
        };
        for (Object[] move : moveInfo) {
            board.movePiece((Piece) move[0], (Coordinate) move[1]);
        }
    }

    private void setupGame2() {
        // Dette spillet skal være et fullført spill, og deretter sammenligner
        // det som ble lagret med det som ble lastet.
        expectedGame2 = getNewGame();
        Board board = expectedGame2.getBoard();

        Piece whiteRightBishopPawn = board.getPiece(new Coordinate(6, 5));
        Piece whiteRightKnightPawn = board.getPiece(new Coordinate(6, 6));
        Piece blackKingPawn = board.getPiece(new Coordinate(1, 4));
        Piece blackQueen = board.getPiece(new Coordinate(0, 3));

        Object[][] moveInfo = {
            { whiteRightBishopPawn, new Coordinate(4, 5) },
            { blackKingPawn, new Coordinate(2, 4) },
            { whiteRightKnightPawn, new Coordinate(4, 6) },
            { blackQueen, new Coordinate(4, 7) }
            
        };
        for (Object[] move : moveInfo) {
            board.movePiece((Piece) move[0], (Coordinate) move[1]);
        }
    }
}
