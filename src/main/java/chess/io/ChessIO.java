package chess.io;

import java.io.IOException;

import chess.model.Game;

public interface ChessIO {

    public Game load() throws IOException;

    public void save(Game game) throws IOException;
    
}
