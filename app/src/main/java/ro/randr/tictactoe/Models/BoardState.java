package ro.randr.tictactoe.Models;

import java.util.Observable;

import ro.randr.tictactoe.Utils.WinType;
import ro.randr.tictactoe.Utils.Winner;

public class BoardState extends Observable {
    private final CellModel[][] board;
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
    }

    public void reInitGame() {
        initValues();
        initCells();
    }

    private void initCells() {
        for (int yPos = 0; yPos < 3; yPos++) {
            for (int xPos = 0; xPos < 3; xPos++) {
                board[yPos][xPos] = new CellModel(xPos, yPos, TicTac.NONE);
            }
        }
    }
}
