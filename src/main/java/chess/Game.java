package chess;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

public class Game {

    private Board board;
    private final Calendar date = new GregorianCalendar();
    private String playerOne;
    private String playerTwo;
    private List<Move> moves = new ArrayList<>();
    private Colour turn = Colour.WHITE;

    public Game(List<Square> squareList) {
        board = new Board(squareList, turn);
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

    public void nextTurn() {
        turn = turn == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
    }

    public void makeMove(Piece piece, int[] toCoordinates) {
        System.out.println(piece + " moved to " + board.getPiece(toCoordinates));
        board.movePiece(piece, toCoordinates);
    }

}
