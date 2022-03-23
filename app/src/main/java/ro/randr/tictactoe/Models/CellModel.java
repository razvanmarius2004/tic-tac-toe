package ro.randr.tictactoe.Models;

public class CellModel {
    private int x;
    private int y;
    private TicTac type;

    public CellModel(int x, int y, TicTac type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public TicTac getType() {
        return type;
    }

    public void setType(TicTac type) {
        this.type = type;
    }
}
