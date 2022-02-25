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
    List<Square> squareList;
    Colour turn = Colour.WHITE;
    

    public Game(List<Square> squareList) {
        this.squareList = squareList;
        board = new Board(squareList);
    }

    public Game(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public Colour getTurn() {
        return turn;
    }

    public void makeMove(Piece piece, int[] toCoordinates) {
        board.movePiece(piece, toCoordinates);
        System.out.println(piece + " moved to " + toCoordinates);
    }

    
}
