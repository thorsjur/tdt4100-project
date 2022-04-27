package chess.model;

import java.time.LocalDateTime;

import chess.model.Board.Coordinate;

public class Game {

    private Board board;
    private LocalDateTime date = LocalDateTime.now();
    private String playerOneName;
    private String playerTwoName;
    private Colour turn = Colour.WHITE;
    private Colour winner;
    private boolean gameFinished;

    public record GameData(
            String date,
            String playerOneName,
            String playerTwoName,
            Colour winner,
            int moves,
            PieceConfiguration currentPieceConfiguration) {
    };

    public Game(String playerOneName, String playerTwoName) {
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        board = new Board(turn);
    }

    public Game(Colour winner, PieceConfiguration pieceConfiguration) {
        PieceConfiguration currentPieceConfiguration = pieceConfiguration.getCurrentPieceConfiguration();
        board = new Board(currentPieceConfiguration.getTurn());
        board.setGrid(currentPieceConfiguration.getPieceGrid());
        board.setPieceConfiguration(currentPieceConfiguration);

        if (winner != null) {
            this.winner = winner;
            gameFinished = true;
            setTurn(winner);
        } else {
            setTurn(currentPieceConfiguration.getTurn());
        }

        for (Piece piece : currentPieceConfiguration) {
            if (piece != null) piece.setBoard(board);
        }
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

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public Square getSelectedSquare() {
        return board.getSelectedSquare();
    }

    public int getNumberOfMoves() {
        return board.getPieceConfiguration().countPieceConfigurations();
    }

    public void nextTurn() {
        turn = ((turn == Colour.WHITE) ? Colour.BLACK : Colour.WHITE);
    }

    public void setTurn(Colour turn) {
        this.turn = turn;
        this.board.setTurn(turn);
    }

    public void makeMove(Piece piece, Coordinate toCoordinates) {
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
        boolean checkmated = board.isKingMated(turn, false);
        boolean stalemated = board.isKingMated(turn, true);

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

    public boolean isValidMove(Piece piece, Coordinate toCoordinates) {
        return board.isValidMove(new Move(piece.getCoordinates(), toCoordinates));
    }

    public boolean isFinished() {
        return gameFinished;
    }


    public boolean canPromotePawn(Square square) {
        Piece piece = square.getPiece();
        return piece instanceof Pawn && ((Pawn) piece).canPromote();
    }

}
