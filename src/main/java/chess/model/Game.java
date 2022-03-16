package chess.model;

import java.time.LocalDateTime;
import java.util.List;

public class Game {

    private Board board;
    private LocalDateTime date = LocalDateTime.now();
    private String playerOneName = "Player One";
    private String playerTwoName = "Player Two";
    private Colour turn = Colour.WHITE;
    private Colour winner;
    private boolean gameFinished;

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

    public Colour getWinner() {
        return winner;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public Square getSelectedSquare() {
        return board.getSelectedSquare();
    }

    public int getNumberOfMoves() {
        return board.getPieceConfiguration().countPieceConfigurations();
    }

    public void nextTurn() {
        turn = ((turn == Colour.WHITE) ? Colour.BLACK : Colour.WHITE);
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

    public boolean checkForMate() {
        boolean checkmated = board.isKingMated(false);
        boolean stalemated = board.isKingMated(true);

        if (checkmated || stalemated) {
            gameFinished = true;
        }
        if (checkmated) {
            winner = ((turn == Colour.WHITE) ? Colour.BLACK : Colour.WHITE);
            return true;
        } else {
            winner = null;
            return stalemated;
        }
    }

    public boolean isValidMove(Piece piece, int[] toCoordinates) {
        return board.isValidMove(piece, toCoordinates);
    }

    public boolean isFinished() {
        return gameFinished;
    }

}
