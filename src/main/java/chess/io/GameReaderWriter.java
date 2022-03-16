package chess.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import chess.model.Colour;
import chess.model.Game;

/*
* Struktur av lagring/loading:
*   - Dato (DD:MM:YYYY)
*   - Navn på spiller en
*   - Navn på spiller to
*   - Tur (WHITE/BLACK)
*   - Vinner (WHITE/BLACK/null)
*   - Antall flytt
*   - Brikkekonfigurasjoner
* 
* Hvert felt skilles med semi-kolon
* EksempeL: 14:03:2022;Per Arne;Odd Per;WHITE;null;23;Brikkeconfig. (forklaring under)
* 
* Under brikkekonfigurasjoner ligger alle tidligere tilstander for brettet, og er en liste med tre bokstaver i serie x64 (8x8)
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
    public Game load() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void save(Game game) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy", Locale.getDefault());
        writer.append(formatter.format(game.getDate()) + SEPARATOR); 

        writer.append(game.getPlayerOneName() + SEPARATOR + game.getPlayerTwoName() + SEPARATOR);

        writer.append(game.getTurn().name() + SEPARATOR);

        Colour winner = game.getWinner();
        writer.append(((winner == null) ? "null" : winner.name()) + SEPARATOR);

        writer.append(String.valueOf(game.getNumberOfMoves()));
        
        writer.append("\n");
        writer.close();
    }

}
