package chess.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import chess.model.Bishop;
import chess.model.Board;
import chess.model.Colour;
import chess.model.Game;
import chess.model.King;
import chess.model.Knight;
import chess.model.Pawn;
import chess.model.Piece;
import chess.model.PieceConfiguration;
import chess.model.Queen;
import chess.model.Rook;
import chess.model.Game.GameData;

/*
* Struktur av lagring/loading:
*   - Dato (DD:MM:YYYY)
*   - Navn på spiller en
*   - Navn på spiller to
*   - Vinner (WHITE/BLACK/null)
*   - Antall flytt
*   - Brikkekonfigurasjoner
* 
* Hvert felt skilles med semi-kolon
* EksempeL: 14:03:2022;Per Arne;Odd Per;WHITE;23;Brikkeconfig. (forklaring under)
* 
* Under brikkekonfigurasjoner ligger alle tidligere tilstander for brettet, og er en liste med tre bokstaver i serie x64 (8x8)
* Før brikkenes tre bokstaver, er det én bit hvorvidt brettet er rotert og én bit som representerer hvem sin tur (Hvit -> 1, Sort -> 0)
* 
* For de tre bokstavene vil den første være én bit (0/1), som representerer fargen (Hvit -> 1, Sort -> 0)
* Den andre bokstaven er også én bit, som sier hvorvidt brikken har beveget seg (relevant for kongen, tårn og bønder)
* Den tredje er en karakter som representerer typen brikke:
*   'K' :   Konge
*   'Q' :   Dronning
*   'R' :   Tårn
*   'B' :   Løper
*   'N' :   Springer
*   'P' :   Bonde
* Eksempel: 01R representerer et sort tårn som har begevd seg.
* En serie med tre nuller (000) er samme som tomt felt (null)
* 
* Altå kan brikkekonfigurasjonen se ut som noe slik:
*   -   01R01N01B00Q01K01B01N01R00P ... 10N10R,01R01N01B00Q01K01B01N01R00P ... 00010R, ...
* */

public class GameReaderWriter implements ChessIO {

    public static final String DIRECTORY_PATH = "src/main/resources/games/";
    public static final String DEFAULT_FILE_NAME = "games.txt";
    public static final String FILE_EXTENSION = ".txt";
    public static final char SEPARATOR = ';';
    public static final char SUB_SEPARATOR = ',';

    private String fileName;

    public GameReaderWriter() {
        this.fileName = DIRECTORY_PATH + DEFAULT_FILE_NAME;
    }

    public GameReaderWriter(String fileName) {
        this.fileName = DIRECTORY_PATH + fileName;

        if (!this.fileName.endsWith(FILE_EXTENSION)) {
            this.fileName += FILE_EXTENSION;
        }
    }

    @Override
    public Game load(GameData data) {
        return new Game(data.winner(), data.currentPieceConfiguration());
    }

    @Override
    public List<GameData> getData(File file) throws IOException{
        if (file == null || !file.exists())
            file = new File(DIRECTORY_PATH + DEFAULT_FILE_NAME);
        List<GameData> dataList;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        dataList = reader.lines()
                    .map(e -> e.split(String.valueOf(SEPARATOR)))
                    .map(strArr -> new GameData(
                            strArr[0],
                            strArr[1],
                            strArr[2],
                            Colour.parseColour(strArr[3]),
                            Integer.parseInt(strArr[4]),
                            buildPieceConfigurationFromDataArray(strArr[5].split(String.valueOf(SUB_SEPARATOR)))))
                    .collect(Collectors.toList());
        reader.close();

        return dataList;
    }

    @Override
    public void save(Game game) throws IOException {
        StringBuilder fileAppend = new StringBuilder();
        game.getBoard().goToCurrentPieceConfiguration();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy", Locale.getDefault());
        fileAppend.append(formatter.format(game.getDate()) + SEPARATOR); 

        fileAppend.append(game.getPlayerOneName() + SEPARATOR + game.getPlayerTwoName() + SEPARATOR);

        Colour winner = game.getWinner();
        fileAppend.append(((winner == null) ? "null" : winner.name()) + SEPARATOR);

        fileAppend.append(String.valueOf(game.getNumberOfMoves()) + SEPARATOR);

        fileAppend.append(getBoardConfigurationsAsString(game));

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append(fileAppend.toString() + "\n");
        writer.close();
    }

    private String getBoardConfigurationsAsString(Game game) {
        Board board = game.getBoard();
        PieceConfiguration pc = board.getPieceConfiguration();
        StringBuilder sb = new StringBuilder();

        // Går til root
        while (pc.hasPreviousGame()) pc = pc.getPreviousGame();

        while(true) {
            StringBuilder boardStateSB = new StringBuilder();

            boardStateSB.append(pc.isBoardRotated() ? "1" : "0");
            boardStateSB.append((pc.getTurn() == Colour.WHITE) ? "1" : "0");

            for (Piece piece : pc) {
                String pieceString;
                
                if (piece == null) {
                    pieceString = "000";
                    boardStateSB.append(pieceString);
                    continue;
                }

                pieceString = (piece.getColour() == Colour.WHITE) ? "1" : "0";
                pieceString += (piece.hasMoved()) ? "1" : "0";

                switch(piece.getClass().getSimpleName()) {
                    case "Rook":
                        pieceString += 'R';
                        break;
                    case "Queen":
                        pieceString += 'Q';
                        break;
                    case "Bishop":
                        pieceString += 'B';
                        break;
                    case "Knight":
                        pieceString += 'N';
                        break;
                    case "King":
                        pieceString += 'K';
                        break;
                    case "Pawn":
                        pieceString += 'P';
                        break;
                }
                boardStateSB.append(pieceString);
            }
            sb.append(boardStateSB.toString() + SUB_SEPARATOR);

            if (!pc.hasNextGame()) {
                break;
            }

            pc = pc.getNextGame();
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private PieceConfiguration buildPieceConfigurationFromDataArray(String[] dataArray) {
        if (dataArray.length == 0) {
            throw new IllegalStateException("No board to build");
        }
        PieceConfiguration root = buildPieceConfigurationFromData(dataArray[0]);

        for (int i = 1; i < dataArray.length; i++) {
            PieceConfiguration nextPieceConfiguration = buildPieceConfigurationFromData(dataArray[i]);
            root.setNextGame(nextPieceConfiguration);
            root = nextPieceConfiguration;
        }

        // Her er det strengt tatt ikke root, men ytterste node som returneres
        return root;
    }

    private PieceConfiguration buildPieceConfigurationFromData(String data) {
        boolean isRotated = data.charAt(0) == '1';
        Colour turn = (data.charAt(1) == '1') ? Colour.WHITE : Colour.BLACK;

        List<Piece> pieces = new ArrayList<>();
        for (int i = 2; i < data.length(); i += 3) {
            String pieceDescription = data.substring(i, i + 3);
            Colour pieceColour = pieceDescription.charAt(0) == '1' ? Colour.WHITE : Colour.BLACK;
            boolean hasMoved = pieceDescription.charAt(1) == '1';

            Piece piece;
            switch(pieceDescription.charAt(2)) {
                case 'R':
                    piece = new Rook(pieceColour, null);
                    break;
                case 'Q':
                    piece = new Queen(pieceColour, null);
                    break;
                case 'B':
                    piece = new Bishop(pieceColour, null);
                    break;
                case 'N':
                    piece = new Knight(pieceColour, null);
                    break;
                case 'K':
                    piece = new King(pieceColour, null);
                    break;
                case 'P':
                    piece = new Pawn(pieceColour, null);
                    break;
                default:
                    piece = null;
            }

            if (piece != null && hasMoved) piece.registerMove();
            pieces.add(piece);
        }

        if (pieces.size() != 64) {
            throw new IllegalStateException("NOT 64 PIECES!");
        }

        Piece[][] pieceGrid = new Piece[8][8];
        int row = 0;
        int col = 0;
        for (int i = 0; i < 64; i++) {
            pieceGrid[row][col] = pieces.get(i);

            if (col < 7) {
                col++;
            } else if (row < 7) {
                col = 0;
                row++;
            }
        }

        return new PieceConfiguration(null, pieceGrid, turn, isRotated);
    }
}
