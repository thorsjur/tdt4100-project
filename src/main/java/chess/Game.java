package chess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
        board.nextTurn();
    }

    public void makeMove(Piece piece, int[] toCoordinates) {
        board.movePiece(piece, toCoordinates);
        nextTurn();
    }

    public void disableBoardRotation() {
        board.setBoardRotation(false);
    }

    public void enableBoardRotation() {
        board.setBoardRotation(true);
    }

}
