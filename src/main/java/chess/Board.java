package chess;

import java.util.ArrayList;
import java.util.List;

public class Board {
    
    private List<ArrayList<Piece>> grid;
    private Board previousBoard;
    private Board nextBoard;

    public Board(List<ArrayList<Piece>> grid) {
        this.grid = grid;
    }

    public Board() {
        
    }
}
