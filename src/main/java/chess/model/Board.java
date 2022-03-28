package chess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chess.model.Move.MoveType;
import chess.model.Piece.Direction;

public class Board {

    private Square[][] chessBoard = new Square[8][8];
    private PieceConfiguration pieceConfiguration;
    private Square selectedSquare;
    private Colour turn;
    private boolean isBoardRotationEnabled;
    private boolean isBoardRotated = false;

    public record Coordinate(int row, int column) {

        public Coordinate add(Coordinate coordinate) {
            return new Coordinate(row + coordinate.row(), column + coordinate.column());
        }

        public Coordinate subtract(Coordinate coordinate) {
            return new Coordinate(row - coordinate.row(), column - coordinate.column());
        }

        public Coordinate addVector(int[] vector) {
            if (vector.length != 2)
                throw new IllegalArgumentException("Not a valid vector!");

            return new Coordinate(row + vector[0], column + vector[1]);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Coordinate))
                return false;

            Coordinate coordinate = (Coordinate) o;
            return row() == coordinate.row() && column() == coordinate.column();
        }
    }

    public Board(Colour turn) {
        this.initializeBoard();
        this.turn = turn;
        this.isBoardRotationEnabled = true;

        pieceConfiguration = new PieceConfiguration(getGrid());
    }

    public void setTurn(Colour turn) {
        this.turn = turn;
    }

    public Square[][] getChessBoard() {
        return chessBoard;
    }

    public Piece[][] getGrid() {
        Piece[][] grid = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = chessBoard[i][j].getPiece();
            }
        }
        return grid;
    }

    public void setGrid(Piece[][] grid) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoard[i][j].setPiece(grid[i][j]);
            }
        }
    }

    public Piece getPiece(Coordinate coordinates) {
        try {
            return getGrid()[coordinates.row()][coordinates.column()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Square getSquare(Coordinate coordinate) {
        try {
            return chessBoard[coordinate.row()][coordinate.column()];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Coordinate getPieceCoordinate(Piece piece) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        Stream.of(chessBoard)
                .forEach(row -> coordinates.add(Stream.of(row)
                        .filter(square -> square.getPiece() == piece)
                        .map(square -> square.getCoordinate())
                        .findFirst()
                        .orElse(null)));

        return coordinates.stream()
                .filter(e -> e != null)
                .findFirst()
                .orElse(null);
    }

    public Colour getTurn() {
        return turn;
    }

    public PieceConfiguration getPieceConfiguration() {
        return pieceConfiguration;
    }

    public Square getSelectedSquare() {
        return selectedSquare;
    }

    public void setBoardRotation(boolean boardRotationEnabled) {
        goToCurrentPieceConfiguration();

        if (this.isBoardRotationEnabled == boardRotationEnabled) {
            return;
        }
        if ((isBoardRotated && !boardRotationEnabled)
                || (!isBoardRotated && boardRotationEnabled && getTurn() == Colour.BLACK)) {
            rotateBoard();
        }
        this.isBoardRotationEnabled = boardRotationEnabled;
        if (selectedSquare != null) {
            Square.deselectSelectedSquare(this);
        }
    }

    public void setPieceConfiguration(PieceConfiguration pieceConfiguration) {
        this.pieceConfiguration = pieceConfiguration;
    }

    public void goToCurrentPieceConfiguration() {
        while (pieceConfiguration.hasNextGame()) {
            pieceConfiguration = pieceConfiguration.getNextGame();
        }

        isBoardRotated = pieceConfiguration.isBoardRotated();
        setGrid(pieceConfiguration.getPieceGrid());
        turn = pieceConfiguration.getTurn();

        restoreRotation();
    }

    public void goToPreviousPieceConfiguration() {
        if (pieceConfiguration.hasPreviousGame()) {
            pieceConfiguration = pieceConfiguration.getPreviousGame();
            isBoardRotated = pieceConfiguration.isBoardRotated();
            setGrid(pieceConfiguration.getPieceGrid());

            restoreRotation();
        }
    }

    public void goToNextPieceConfiguration() {
        if (pieceConfiguration.hasNextGame()) {
            pieceConfiguration = pieceConfiguration.getNextGame();
            isBoardRotated = pieceConfiguration.isBoardRotated();
            setGrid(pieceConfiguration.getPieceGrid());

            restoreRotation();
        }
    }

    public void movePiece(Piece piece, Coordinate toCoordinates) {
        Move move = Move.getMove(piece, toCoordinates);
        if (!isValidMove(move)) {
            return;
        }
        Coordinate pieceCoordinate = getPieceCoordinate(piece);

        if (move.getType() == MoveType.CASTLE) {
            int distance = toCoordinates.subtract(pieceCoordinate).column();
            boolean isLongCastle = toCoordinates.column() == 2 || toCoordinates.column() == 5;
            Rook rook = ((King) piece).getCastleRook(isLongCastle);
            Coordinate rookCoordinates = rook.getCoordinates();
            Coordinate newRookCoordinates = pieceCoordinate.add(new Coordinate(0, (distance < 0 ? -1 : 1)));

            setPiece(rook, newRookCoordinates);
            removePiece(rookCoordinates);
        }
 
        if (move.getType() == MoveType.EN_PASSANT) {

            // Bonden som skal tas er to ruter under der din bonde ender opp
            Coordinate opponentPawnCoordinate = toCoordinates
                    .addVector(Direction.DOWN.getDirectionVector(this))
                    .addVector(Direction.DOWN.getDirectionVector(this));

            System.out.println(opponentPawnCoordinate);
            removePiece(opponentPawnCoordinate);
        }

        setPiece(piece, toCoordinates);
        removePiece(pieceCoordinate);

        resetRecentlyMovedPawns();
        piece.registerMove();

        resetSquareStates();
        Square.deselectSelectedSquare(this);

        pieceConfiguration = new PieceConfiguration(pieceConfiguration, getGrid(),
                ((turn == Colour.WHITE) ? Colour.BLACK : Colour.WHITE), isBoardRotated);
    }

    public boolean isBoardRotationEnabled() {
        return isBoardRotationEnabled;
    }

    public void nextTurn() {
        turn = ((turn == Colour.WHITE) ? Colour.BLACK : Colour.WHITE);
        if (isBoardRotationEnabled) {
            rotateBoard();
        }
    }

    public boolean checkBoardForCheck() {
        boolean threatened = getKing(turn).isThreatened();
        return threatened;
    }

    public boolean checkNextBoardForCheck(Piece piece, Coordinate toCoordinates) {
        Piece[][] tempGrid = getGrid();
        boolean threatened = false;

        mitigatedMovePiece(piece, toCoordinates);
        if (getKing(turn) != null)
            threatened = checkBoardForCheck();
        setGrid(tempGrid);

        return threatened;
    }

    public boolean isValidMove(Move move) {
        Piece piece = getPiece(move.getFromCoordinates());
        if (piece == null || checkNextBoardForCheck(piece, move.getToCoordinates())) {
            return false;
        }
        return piece.getValidMoves().stream()
                    .anyMatch(m -> move.equals(m));
    }

    public void selectSquare(Coordinate coordinates) {
        selectedSquare = getSquare(coordinates);
    }

    public void selectSquare(Square square) {
        selectedSquare = square;
    }

    public void deselectSquare() {
        if (selectedSquare == null) {
            return;
        }
        Square.deselectSelectedSquare(this);
    }

    public void resetSquareStates() {
        Stream.of(chessBoard)
                .forEach(row -> Stream.of(row)
                        .filter(square -> selectedSquare == null || selectedSquare != square)
                        .forEach(square -> square.setState(Square.State.DEFAULT)));

    }

    public void rotateBoard() {
        isBoardRotated = !isBoardRotated;

        Piece[][] grid = getGrid();
        Piece[][] gridCopy = new Piece[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                gridCopy[i][j] = grid[7 - i][7 - j];
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoard[i][j].setPiece(gridCopy[i][j]);
            }
        }
    }

    public boolean isKingMated(boolean stalemate) {
        King king = getKing(getTurn());
        return king.isMated(stalemate);
    }

    public boolean isSquareThreatened(Square square) {
        // Da denne metoden benytter seg av å midlertidig plassere en brikke og deretter
        // fjerne den, må den sjekke om ruten er tom fra før av
        // Hvis den ikke er tom, vil den fjerne en brikke (som ikke er ønsket)
        // Dette betyr denne ikke kan sjekke om en okkupert rute er truet, men det er en
        // funksjonalitet som ikke er relevant for koden

        if (square.getPiece() != null)
            return false;

        King tempKing = new King(turn, this);
        square.setPiece(tempKing);
        boolean threatened = tempKing.isThreatened();
        square.removePiece();

        return threatened;
    }

    public void promotePawn(Pawn pawn, char piece) {
        if (!pawn.canPromote()) {
            return;
        }
        Colour pawnColour = pawn.getColour();
        Piece newPiece;

        switch (piece) {
            case 'Q':
                newPiece = new Queen(pawnColour, this);
                break;
            case 'R':
                newPiece = new Rook(pawnColour, this);
                break;
            case 'B':
                newPiece = new Bishop(pawnColour, this);
                break;
            case 'K':
                newPiece = new Knight(pawnColour, this);
                break;
            default:
                newPiece = new Pawn(pawnColour, this);
        }
        setPiece((newPiece), pawn.getCoordinates());

        // PieceConfiguration må registrere at bonden er promotert, men da dette er
        // etter turen er byttet, må brettet roteres før den settes, og så tilbake igjen
        if (isBoardRotationEnabled) {
            rotateBoard();
            pieceConfiguration.setPieceGrid(getGrid());
            rotateBoard();
        } else {
            pieceConfiguration.setPieceGrid(getGrid());
        }
    }

    private King getKing(Colour colour) {
        return Stream.of(getGrid())
            .flatMap(row -> Stream.of(row))
            .filter(piece -> piece != null && piece.getColour() == colour && piece instanceof King)
            .map(piece -> ((King) piece))
            .findFirst()
            .orElse(null);
    }

    private void setPiece(Piece piece, Coordinate toCoordinates) {
        getChessBoard()[toCoordinates.row()][toCoordinates.column()].setPiece(piece);
    }

    private void removePiece(Coordinate coordinates) {
        getChessBoard()[coordinates.row()][coordinates.column()].setPiece(null);
    }

    private void mitigatedMovePiece(Piece piece, Coordinate toCoordinates) {
        Coordinate coordinates = piece.getCoordinates();
        setPiece(piece, toCoordinates);
        removePiece(coordinates);
    }

    private void initializeSquares() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square tempSquare = new Square(new Coordinate(i, j));
                tempSquare.setBoard(this);
                chessBoard[i][j] = tempSquare;
            }
        }
    }

    private void initializeBoard() {
        initializeSquares();

        // Hvis dette er er nytt spill, må den renske de eksisterende brikkene
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoard[i][j].setPiece(null);
            }
        }

        // Setter bønder
        for (int i = 0; i < chessBoard.length; i++) {
            chessBoard[1][i].setPiece(new Pawn(Colour.BLACK, this));
            chessBoard[6][i].setPiece(new Pawn(Colour.WHITE, this));
        }

        // Setter tårn
        chessBoard[0][0].setPiece(new Rook(Colour.BLACK, this));
        chessBoard[0][7].setPiece(new Rook(Colour.BLACK, this));
        chessBoard[7][0].setPiece(new Rook(Colour.WHITE, this));
        chessBoard[7][7].setPiece(new Rook(Colour.WHITE, this));

        // setter springere
        chessBoard[0][1].setPiece(new Knight(Colour.BLACK, this));
        chessBoard[0][6].setPiece(new Knight(Colour.BLACK, this));
        chessBoard[7][1].setPiece(new Knight(Colour.WHITE, this));
        chessBoard[7][6].setPiece(new Knight(Colour.WHITE, this));

        // setter løpere
        chessBoard[0][2].setPiece(new Bishop(Colour.BLACK, this));
        chessBoard[0][5].setPiece(new Bishop(Colour.BLACK, this));
        chessBoard[7][2].setPiece(new Bishop(Colour.WHITE, this));
        chessBoard[7][5].setPiece(new Bishop(Colour.WHITE, this));

        // setter konger
        chessBoard[0][4].setPiece(new King(Colour.BLACK, this));
        chessBoard[7][4].setPiece(new King(Colour.WHITE, this));

        // setter dronninger
        chessBoard[0][3].setPiece(new Queen(Colour.BLACK, this));
        chessBoard[7][3].setPiece(new Queen(Colour.WHITE, this));
    }

    private void restoreRotation() {
        if ((turn == Colour.WHITE && isBoardRotated)
                || (turn == Colour.BLACK && !isBoardRotated && isBoardRotationEnabled)
                || (turn == Colour.BLACK && isBoardRotated && !isBoardRotationEnabled)) {

            rotateBoard();
        }
    }

    private void resetRecentlyMovedPawns() {
        Stream.of(getGrid())
            .flatMap(row -> Stream.of(row))
            .filter(piece -> piece instanceof Pawn)
            .forEach(piece -> ((Pawn) piece).resetRecentlyMovedTwoSquares());
    }

    @Override
    public String toString() {
        Map<Object, Object> displayMap = Stream.of(new String[][] {
                { "WhitePawn", "♟" },
                { "BlackPawn", "♙" },
                { "WhiteRook", "♜" },
                { "BlackRook", "♖" },
                { "WhiteKnight", "♞" },
                { "BlackKnight", "♘" },
                { "WhiteBishop", "♝" },
                { "BlackBishop", "♗" },
                { "WhiteQueen", "♛" },
                { "BlackQueen", "♕" },
                { "WhiteKing", "♚" },
                { "BlackKing", "♔" },
                { null, " " },
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        String newString = "";
        for (Piece[] row : getGrid()) {
            newString += "\n";
            for (Piece piece : row) {
                if (piece != null) {
                    newString += " | " + displayMap.get(piece.toString());
                } else {
                    newString += "  | ";
                }
            }
        }
        return newString;
    }
    
}
