package ro.randr.tictactoe.Observables;

import java.util.Observable;

import ro.randr.tictactoe.Models.CellModel;
import ro.randr.tictactoe.Enums.TicTac;
import ro.randr.tictactoe.Enums.WinType;
import ro.randr.tictactoe.Enums.Winner;
import ro.randr.tictactoe.Models.IsGameOverModel;
import ro.randr.tictactoe.Models.IsOpponentReady;
import ro.randr.tictactoe.Models.ScoreModel;

public class GameStateObservable extends Observable {
    private final CellModel[][] board;
    private boolean isGameOver;
    private WinType winType;
    private Winner winner;
    private TicTac playerType;
    private boolean isYourTurn;
    private boolean areYouReady;
    private boolean isOpponentReady;
    private String connectedEndPoint;
    private ScoreModel score;

    // region getters and setters

    public CellModel[][] getBoard() {
        return board;
    }

    public WinType getWinType() {
        return winType;
    }

    public Winner getWinner() {
        return winner;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

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
        setChanged();
        notifyObservers(new IsOpponentReady(true));
    }

    public String getConnectedEndPoint() {
        return connectedEndPoint;
    }

    public void setConnectedEndPoint(String connectedEndPoint) {
        this.connectedEndPoint = connectedEndPoint;
    }

    public ScoreModel getScore() {
        return score;
    }

    // endregion

    private static GameStateObservable instance;

    private GameStateObservable() {
        board = new CellModel[3][3];
        score = ScoreModel.getInstance();
        initValues(TicTac.TAC, false);
        initCells();
    }

    public static GameStateObservable getInstance() {
        if (instance == null) {
            instance = new GameStateObservable();
        }
        return instance;
    }

    private void initValues(TicTac yourType, boolean areYouReady) {
        isGameOver = false;
        winner = Winner.NONE;
        winType = WinType.NONE;
        playerType = yourType;
        if (yourType == TicTac.TIC) {
            isYourTurn = true;
        } else {
            isYourTurn = false;
        }
        this.areYouReady = areYouReady;
    }

    public void reInitGame(TicTac yourTpe, boolean areYouReady) {
        initValues(yourTpe, areYouReady);
        initCells();
        setChanged();
        notifyObservers();

    }

    private void initCells() {
        for (int yPos = 0; yPos < 3; yPos++) {
            for (int xPos = 0; xPos < 3; xPos++) {
                board[yPos][xPos] = new CellModel(xPos, yPos, TicTac.NONE);
            }
        }
    }

    public void checkAndSetWinner() {
        if (board[0][0].getType() == board[0][1].getType() && board[0][1].getType() == board[0][2].getType()) {
            switch (board[0][0].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.HORIZONTAL_0);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.HORIZONTAL_0);
                    break;
            }
        }

        if (board[1][0].getType() == board[1][1].getType() && board[1][1].getType() == board[1][2].getType()) {
            switch (board[1][0].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.HORIZONTAL_1);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.HORIZONTAL_1);
                    break;
            }
        }

        if (board[2][0].getType() == board[2][1].getType() && board[2][1].getType() == board[2][2].getType()) {
            switch (board[2][0].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.HORIZONTAL_2);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.HORIZONTAL_2);
                    break;
            }
        }

        if (board[0][0].getType() == board[1][0].getType() && board[1][0].getType() == board[2][0].getType()) {
            switch (board[0][0].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.VERTICAL_0);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.VERTICAL_0);
                    break;
            }
        }

        if (board[0][1].getType() == board[1][1].getType() && board[1][1].getType() == board[2][1].getType()) {
            switch (board[0][1].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.VERTICAL_1);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.VERTICAL_1);
                    break;
            }
        }

        if (board[0][2].getType() == board[1][2].getType() && board[1][2].getType() == board[2][2].getType()) {
            switch (board[0][2].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.VERTICAL_2);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.VERTICAL_2);
                    break;
            }
        }

        if (board[0][0].getType() == board[1][1].getType() && board[1][1].getType() == board[2][2].getType()) {
            switch (board[0][0].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.NW_TO_SE);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.NW_TO_SE);
                    break;
            }
        }

        if (board[2][0].getType() == board[1][1].getType() && board[1][1].getType() == board[0][2].getType()) {
            switch (board[2][0].getType()) {
                case TIC:
                    setWin(Winner.TIC, WinType.SW_TO_NE);
                    break;
                case TAC:
                    setWin(Winner.TAC, WinType.SW_TO_NE);
                    break;
            }
        }
        if (!isGameOver && isDraw()) {
            setWin(Winner.DRAW, WinType.NONE);
        }
    }

    private boolean isDraw() {
        for (int yPos = 0; yPos < 3; yPos++) {
            for (int xPos = 0; xPos < 3; xPos++) {
                if (board[yPos][xPos].getType() == TicTac.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    public void modifyCell(int y, int x, TicTac type) {
        board[y][x].setType(type);

        CellModel changedCell = new CellModel(x, y, type);
        setChanged();
        notifyObservers(changedCell);
    }

    private void setWin(Winner winner, WinType winType) {
        this.winner = winner;
        this.winType = winType;
        isGameOver = true;
        areYouReady = false;
        isOpponentReady = false;
        setChanged();
        notifyObservers(new IsGameOverModel(true));
    }
}
