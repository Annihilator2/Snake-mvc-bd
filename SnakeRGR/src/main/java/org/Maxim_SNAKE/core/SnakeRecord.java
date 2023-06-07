package org.Maxim_SNAKE.core;

public class SnakeRecord {
    public String playerName;
    public int score;

    public SnakeRecord(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Имя:"+ playerName + " " + "Счет:" + score;
    }
}
