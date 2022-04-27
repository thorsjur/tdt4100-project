package util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chess.io.GameReaderWriter;
import chess.model.Game;
import chess.model.Game.GameData;

public class GameStateLoader {

    private final static String DIRECTORY_PATH = "src/test/resources/test_games/";
    private final static String FILE_EXTENSION = ".txt";

    public enum GameState {
        MATED,
        MATE_READY,
        STALEMATED,
        STALEMATE_READY,
        ELIGIBLE_FOR_PROMOTION,
        KING_TEST_ONE,
        KING_TEST_TWO,
        CASTLE,
        BISHOP_TEST,
        KNIGHT_TEST,
        PAWN_TEST,
        PROMOTION_READY,
        EN_PASSANT,
        QUEEN_TEST,
        ROOK_TEST
    }

    public static Game getGame(GameState state) {
        GameReaderWriter grw = new GameReaderWriter();
        String fileName;

        switch (state) {
            case MATED:
                fileName = "mated";
                break;
            case MATE_READY:
                fileName = "mate_ready";
                break;
            case ELIGIBLE_FOR_PROMOTION:
                fileName = "eligible_for_promotion";
                break;
            case STALEMATED:
                fileName = "stalemated";
                break;
            case STALEMATE_READY:
                fileName = "stalemate_ready";
                break;
            case KING_TEST_ONE:
                fileName = "king_test_one";
                break;
            case KING_TEST_TWO:
                fileName = "king_test_two";
                break;
            case CASTLE:
                fileName = "castle";
                break;
            case BISHOP_TEST:
                fileName = "bishop_test";
                break;
            case KNIGHT_TEST:
                fileName = "knight_test";
                break;
            case PAWN_TEST:
                fileName = "pawn_test";
                break;
            case PROMOTION_READY:
                fileName = "promotion_ready";
                break;
            case EN_PASSANT:
                fileName = "en_passant";
                break;
            case QUEEN_TEST:
                fileName = "queen_test";
                break;
            case ROOK_TEST:
                fileName = "rook_test";
                break;
            default:
                throw new IllegalArgumentException("Not a valid game state!");
        }

        List<GameData> gameData;
        try {
            gameData = grw.getData(new File(DIRECTORY_PATH + fileName + FILE_EXTENSION));
        } catch (IOException e) {
            System.out.println("Unable to load game");
            return new Game("", "");
        }

        return grw.load(gameData.get(0));
    }
}
