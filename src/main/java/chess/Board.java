package chess;

import java.util.List;

public class Board {

    private Square[][] chessBoard = new Square[8][8];
    private Board previousBoard;
    private Board nextBoard;
    private Square selectedSquare;
    private Colour turn;

    public Board(List<Square> squareList, Square[][] chessBoard) {
        this.chessBoard = chessBoard;
    }

    public Board(List<Square> squareList, Colour turn) {
        this.initializeBoard(squareList);
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

    public static Colour getColourOfSquare(int[] coordinates) {
        int row = coordinates[0];
        int col = coordinates[1];
        return (row + col) % 2 == 0 ? Colour.WHITE : Colour.BLACK;
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

    public void movePiece(Piece piece, int[] toCoordinates) {
        int[] coordinatesOfPiece = getCoordinatesOfPiece(piece);
        piece.registerMove();
        setPiece(piece, toCoordinates);
        removePiece(coordinatesOfPiece);
        turn = turn == Colour.WHITE ? Colour.BLACK : Colour.WHITE;
        rotateBoard();
        System.out.println(getKing(turn).isThreatened() ? getKing(turn).toString() + " threatened" : "no threats");
        rotateBoard();
    }

    public boolean isValidMove(Piece piece, int[] toCoordinates) {
        if (piece == null) {
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

    private void setPiece(Piece piece, int[] toCoordinates) {
        int toRow = toCoordinates[0];
        int toCol = toCoordinates[1];
        getChessBoard()[toRow][toCol].setPiece(piece);
    }

    private void removePiece(int[] coordinates) {
        getChessBoard()[coordinates[0]][coordinates[1]].setPiece(null);
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

    private King getKing(Colour colour) {
        for (Piece[] row : getGrid()) {
            for (Piece piece : row) {
                if (piece != null && piece.getColour() == colour && piece instanceof King) return (King) piece;
            }
        }
        return null;
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
        String newString = "";
        for (Piece[] row : getGrid()) {
            newString += "\n";
            for (Piece piece : row) {
                if (piece != null) {
                    newString += " " + piece.toString() + " ";
                }
            }
        }
        return newString;
    }
}
