package ro.randr.tictactoe.Models;

import java.util.Observable;

import ro.randr.tictactoe.Utils.WinType;
import ro.randr.tictactoe.Utils.Winner;

public class BoardState extends Observable {
    private final CellModel[][] board;
    private CellModel changedCell;
    private boolean isGameOver;
    private WinType winType;
    private Winner winner;

    // region getters and setters

    public CellModel[][] getBoard() {
        return board;
    }

    public WinType getWinType() {
        return winType;
    }

    public void setWinType(WinType winType) {
        this.winType = winType;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public static void setInstance(BoardState instance) {
        BoardState.instance = instance;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public CellModel getChangedCell() {
        return changedCell;
    }

    public void setChangedCell(CellModel changedCell) {
        this.changedCell = changedCell;
    }

    // endregion

    private static BoardState instance;

    private BoardState() {
        board = new CellModel[3][3];
        initValues();
        initCells();
    }

    public static BoardState getInstance() {
        if (instance == null) {
            instance = new BoardState();
        }
        return instance;
    }

    private void initValues() {
        isGameOver = false;
        winner = Winner.NONE;
        winType = WinType.NONE;
        changedCell = null;
    }

    public void reInitGame() {
        initValues();
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
                    winner = Winner.TIC;
                    winType = WinType.HORIZONTAL_0;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.HORIZONTAL_0;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[1][0].getType() == board[1][1].getType() && board[1][1].getType() == board[1][2].getType()) {
            switch (board[1][0].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.HORIZONTAL_1;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.HORIZONTAL_1;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[2][0].getType() == board[2][1].getType() && board[2][1].getType() == board[2][2].getType()) {
            switch (board[2][0].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.HORIZONTAL_2;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.HORIZONTAL_2;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[0][0].getType() == board[1][0].getType() && board[1][0].getType() == board[2][0].getType()) {
            switch (board[0][0].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.VERTICAL_0;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.VERTICAL_0;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[0][1].getType() == board[1][1].getType() && board[1][1].getType() == board[2][1].getType()) {
            switch (board[0][1].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.VERTICAL_1;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.VERTICAL_1;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[0][2].getType() == board[1][2].getType() && board[1][2].getType() == board[2][2].getType()) {
            switch (board[0][2].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.VERTICAL_2;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.VERTICAL_2;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[0][0].getType() == board[1][1].getType() && board[1][1].getType() == board[2][2].getType()) {
            switch (board[0][0].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.NW_TO_SE;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.NW_TO_SE;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }

        if (board[2][0].getType() == board[1][1].getType() && board[1][1].getType() == board[0][2].getType()) {
            switch (board[2][0].getType()) {
                case TIC:
                    winner = Winner.TIC;
                    winType = WinType.SW_TO_NE;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
                case TAC:
                    winner = Winner.TAC;
                    winType = WinType.SW_TO_NE;
                    isGameOver = true;
                    GameState.getInstance().setAreYouReady(false);
                    GameState.getInstance().setOpponentReady(false);
                    setChanged();
                    notifyObservers();
                    break;
            }
        }
        if (isDraw()) {
            winner = Winner.DRAW;
            isGameOver = true;
            GameState.getInstance().setAreYouReady(false);
            GameState.getInstance().setOpponentReady(false);
            setChanged();
            notifyObservers();
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

        changedCell = new CellModel(x, y, type);
        setChanged();
        notifyObservers();
    }
}
