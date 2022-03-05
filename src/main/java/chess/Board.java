package chess;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;

public class Board {

    private Square[][] chessBoard = new Square[8][8];
    private Piece[][] previousGrid;
    private Piece[][] nextGrid;
    private Square selectedSquare;
    private Colour turn;
    private boolean boardRotationEnabled;
    private boolean boardRotated = false;
    private boolean isChanged = true;

    public Board(List<Square> squareList, Square[][] chessBoard) {
        this.chessBoard = chessBoard;
    }

    public boolean isChanged() {
        return isChanged;
    }
 
    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public Board(List<Square> squareList, Colour turn) {
        this.initializeBoard(squareList);
        this.turn = turn;
        this.boardRotationEnabled = true;
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

    public Piece getPiece(int[] coordinates) {
        try {
            return getGrid()[coordinates[0]][coordinates[1]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

    }

    public Square getSquare(int[] coordinates) {
        try {
            return chessBoard[coordinates[0]][coordinates[1]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int[] getCoordinatesOfPiece(Piece piece) {
        if (piece == null)
            return null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getGrid()[i][j] == piece) {
                    int[] coordinates = { i, j };
                    return coordinates;
                }
            }
        }
        return null;
    }

    public Colour getTurn() {
        return turn;
    }

    public void setBoardRotation(boolean boardRotationEnabled) {
        if ( (boardRotated || boardRotationEnabled) && (! boardRotated || ! boardRotationEnabled) && this.boardRotationEnabled != boardRotationEnabled) {
            rotateBoard();
        }
        this.boardRotationEnabled = boardRotationEnabled;
        if (selectedSquare != null) {
            getSelectedSquare().deselectSquare();
        }
        
    }

    public void movePiece(Piece piece, int[] toCoordinates) {
        if (! isValidMove(piece, toCoordinates)) {
            return;
        }
        int[] coordinatesOfPiece = getCoordinatesOfPiece(piece);
        int distance = (toCoordinates[1] - piece.getCoordinates()[1]);
        if (piece instanceof King && Math.abs(distance) == 2) {
            boolean longCastle = toCoordinates[1] == 2 || toCoordinates[1] == 5;
            Rook rook = ((King) piece).getCastleRook(longCastle);
            int[] rookCoordinates = rook.getCoordinates();
            int[] newRookCoordinates = { coordinatesOfPiece[0], coordinatesOfPiece[1] + (distance < 0 ? -1 : 1) };
            setPiece(rook, newRookCoordinates);
            removePiece(rookCoordinates);
        }

        piece.registerMove();
        setPiece(piece, toCoordinates);
        removePiece(coordinatesOfPiece);
        removeAllHighlights();
        getSelectedSquare().deselectSquare();
    }

    public boolean isBoardRotationEnabled() {
        return boardRotationEnabled;
    }

    public void nextTurn() {
        turn = turn == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
        if (boardRotationEnabled) {
            rotateBoard();
        }
    }
    
    public boolean checkBoardForCheck() {
        boolean threatened = getKing(turn).isThreatened();
        return threatened;
    }


    public boolean checkNextBoardForCheck(Piece piece, int[] toCoordinates) {
        Piece[][] tempGrid = getGrid();
        boolean threatened = false;
        mitigatedMovePiece(piece, toCoordinates);
        if (getKing(turn) != null) threatened = checkBoardForCheck();
        setGrid(tempGrid);
        return threatened;
    }

    public boolean isValidMove(Piece piece, int[] toCoordinates) {
        if (piece == null) {
            return false;
        }
        if (checkNextBoardForCheck(piece, toCoordinates)) {
            return false;
            
        }
        List<Move> moveList = piece.getValidMoves();
        for (Move move : moveList) {
            if (move.equals(new Move(getCoordinatesOfPiece(piece), toCoordinates))) {
                return true;
            }
        }
        return false;
    }

    public Square getSelectedSquare() {
        return selectedSquare;
    }

    public void selectSquare(int[] coordinates) {
        selectedSquare = getSquare(coordinates);
    }

    public void selectSquare(Square square) {
        selectedSquare = square;
    }

    public void deselectSquare() {
        if (selectedSquare == null) {
            return;
        }
        selectedSquare.deselectSquare();
    }

    public void removeAllHighlights() {
        for (Square[] row : chessBoard) {
            for (Square square : row) {
                if (selectedSquare != null && selectedSquare == square) {
                    continue;
                }
                square.removeHighlight();
            }
        }
    }

    public void rotateBoard() {
        boardRotated = ! boardRotated;
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
        isChanged = true;
    }

    public boolean isKingMated() {
        King king = getKing(getTurn());
        return king.isMated();
    }

    private King getKing(Colour colour) {
        for (Piece[] row : getGrid()) {
            for (Piece piece : row) {
                if (piece != null && piece.getColour() == colour && piece instanceof King) return (King) piece;
            }
        }
        return null;
    }

    public boolean isSquareThreatened(Square square) {
        if (square.getPiece() != null) return false;
        King tempKing = new King(turn, this);
        
        square.setPiece(tempKing);
        boolean threatened = tempKing.isThreatened();
        square.removePiece();
        return threatened;
    }

    private void setPiece(Piece piece, int[] toCoordinates) {
        System.out.println(piece + " set to " + toCoordinates[0] + " | " + toCoordinates[1]);
        int toRow = toCoordinates[0];
        int toCol = toCoordinates[1];
        getChessBoard()[toRow][toCol].setPiece(piece);
        isChanged = true;
    }

    private void removePiece(int[] coordinates) {
        getChessBoard()[coordinates[0]][coordinates[1]].setPiece(null);
        isChanged = true;
    }

    private void mitigatedMovePiece(Piece piece, int[] toCoordinates) {
        int[] coordinates = piece.getCoordinates();
        setPiece(piece, toCoordinates);
        removePiece(coordinates);
    }

    private void initializeBoard(List<Square> squareList) {
        for (Square square : squareList) {
            int[] coordinate = square.getCoordinates();
            chessBoard[coordinate[0]][coordinate[1]] = square;
            square.setBoard(this);
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
                    newString += " | " + displayMap.get(piece.toString()) + " | ";
                } else {
                    newString += " | " + " " + " | ";
                }
            }
        }
        return newString;
    }
}
