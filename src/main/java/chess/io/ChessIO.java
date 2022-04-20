package chess.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import chess.model.Game;
import chess.model.Game.GameData;

public interface ChessIO {

    public Game load(GameData data);

    public List<GameData> getData(File file) throws IOException;

    public void save(Game game) throws IOException;
    
}
