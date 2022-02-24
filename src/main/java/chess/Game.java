package chess;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class Game {
    
    private Board board;
    private Calendar date;
    String playerOne;
    String playerTwo;
    List<Move> moves = new ArrayList<>();

    public Game() {
        board = new Board();
    }

    public Game(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    
}
