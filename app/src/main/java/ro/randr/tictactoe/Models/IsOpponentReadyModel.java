package ro.randr.tictactoe.Models;

public class IsOpponentReadyModel {
    private final boolean isOpponentReady;

    public IsOpponentReadyModel(boolean isOpponentReady) {
        this.isOpponentReady = isOpponentReady;
    }

    public boolean isOpponentReady() {
        return isOpponentReady;
    }
}
