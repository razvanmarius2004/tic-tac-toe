package ro.randr.tictactoe.Models;

import ro.randr.tictactoe.Enums.TicTac;

public class CellModel {
    private final int x;
    private final int y;
    private TicTac type;

    public CellModel(int x, int y, TicTac type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TicTac getType() {
        return type;
    }

    public void setType(TicTac type) {
        this.type = type;
    }
}
