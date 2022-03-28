package ro.randr.tictactoe.Models;

public class ScoreModel {
    private int yourScore;
    private int opponentScore;

    private static ScoreModel instance;

    private ScoreModel() {
        yourScore = 0;
        opponentScore = 0;
    }

    public static ScoreModel getInstance() {
        if (instance == null) {
            instance = new ScoreModel();
        }
        return instance;
    }

    public int getYourScore() {
        return yourScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void increaseYourScore() {
        yourScore++;
    }

    public void increaseOpponentScore() {
        opponentScore++;
    }

    public void reset() {
        yourScore = 0;
        opponentScore = 0;
    }
}
