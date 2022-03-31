package ro.randr.tictactoe.Models;

public class IsGameOverModel {
    private final boolean isGameOver;

    public IsGameOverModel(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
