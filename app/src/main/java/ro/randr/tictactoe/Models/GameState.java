package ro.randr.tictactoe.Models;

public class GameState {

    private TicTac playerType;
    private boolean isYourTurn;
    private boolean areYouReady;
    private boolean isOpponentReady;
    private String connectedEndPoint;

    private static GameState instance;

    private GameState() {
        connectedEndPoint = "";
        init(TicTac.TAC);
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // region getters an setters
    public TicTac getPlayerType() {
        return playerType;
    }

    public void setPlayerType(TicTac playerType) {
        this.playerType = playerType;
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }

    public boolean isAreYouReady() {
        return areYouReady;
    }

    public void setAreYouReady(boolean areYouReady) {
        this.areYouReady = areYouReady;
    }

    public boolean isOpponentReady() {
        return isOpponentReady;
    }

    public void setOpponentReady(boolean opponentReady) {
        isOpponentReady = opponentReady;
    }

    public String getConnectedEndPoint() {
        return connectedEndPoint;
    }

    public void setConnectedEndPoint(String connectedEndPoint) {
        this.connectedEndPoint = connectedEndPoint;
    }
    // endregion

    private void init(TicTac you) {
        playerType = you;
        isYourTurn = false;
        areYouReady = false;
        isOpponentReady = false;
    }

    public void reInit(TicTac you) {
        init(you);
    }
}
