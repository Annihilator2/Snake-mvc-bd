package org.Maxim_SNAKE.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.Maxim_SNAKE.controller.IController;
import org.Maxim_SNAKE.core.Client;
import org.Maxim_SNAKE.core.Snake;
import org.Maxim_SNAKE.core.SnakeRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Random;

//Модель верхнего уровня, представляющая все игровое состояние.
public class Model implements ClientCallback {

    private final Client client = new Client(this);
    private IController controller;
    public static final int NUM_ROWS = 21;
    public static final int NUM_COLUMNS = 20;
    static final int SNAKE_SEGMENT = 1;
    static final int FRUIT = 2;
    public static String playerName;

    //Змея, управляемая игроком.
    private Snake snake;

    //Сетка, содержащая сегменты змеи и фрукты.
    private int[][] grid;


    //Пауза.
    boolean paused = false;


    //Проиграл ли игрок.
    boolean hasLost = false;

    //Положение на доске, в котором находится фрукт
    //находится.
    Pair<Integer, Integer> fruitLocation;

    public static IntegerProperty score;

    public Model(IController controller) {
        this.controller = controller;
        this.score = new SimpleIntegerProperty(0);
        this.snake = new Snake();
        grid = new int[NUM_ROWS][NUM_COLUMNS];
        client.start("localhost", 3000, playerName);
        addSnakeToGrid();
        addFruitToGrid();
    }
    public void savePlayerRecord(String playerName, String score) throws IOException {
        String record = playerName + "," + score + System.lineSeparator();
        Path recordsFile = Path.of("records.txt");

        // Добавить запись в файл
        Files.write(recordsFile, record.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        // тут написать клиент
        client.sendResults(playerName, score);

    }

//Очищает все позиции сетки, содержащие соответствующие сегменты змейки.

    private void addSnakeToGrid() {
        for (Pair<Integer, Integer> coords : snake.getAllSegments()) {
            grid[coords.getKey()][coords.getValue()] = SNAKE_SEGMENT;
        }
    }

  //Очищает все позиции сетки, содержащие соответствующие сегменты змейки.
    private void removeSnakeFromGrid() {
        for (Pair<Integer, Integer> coords : snake.getAllSegments()) {
            grid[coords.getKey()][coords.getValue()] = 0;
        }
    }

//Возвращает змею
    public Snake getSnake() {
        return snake;
    }

 // Возвращает значение true, если игрок проиграет игру во время следующего хода
 // В противном случае возвращает значение false
    private boolean checkLoss(Pair<Integer, Integer> nextHeadPosition, ArrayList<Pair<Integer, Integer>> nextSegments) {
        int nextHeadRow = nextHeadPosition.getKey();
        int nextHeadColumn = nextHeadPosition.getValue();


        //если заголовок находится за пределами поля, верните значение true.
        //если голова находится в том же положении, что и у змеи
        //сегмент, возвращает значение true.
        if (nextHeadRow < 0 || nextHeadRow >= NUM_ROWS || nextHeadColumn < 0 || nextHeadColumn >= NUM_COLUMNS ||
                nextSegments.contains(nextHeadPosition)) {
            return true;
        } else {
            return false;
        }
    }

   //Обновляет текущую модель во время игрового цикла.
    public void updateModel() throws IOException {


        //если игрок проиграл,
        //змея должна перестать двигаться.
        if (!hasLost) {
            Pair<Integer, Integer> nextHeadPosition = snake.getNextHeadPosition();
            ArrayList<Pair<Integer, Integer>> nextSegments = snake.getNextSegmentsPositions();

            if (checkLoss(nextHeadPosition, nextSegments)) {
                hasLost = true;

                savePlayerRecord(Model.playerName, String.valueOf(Model.getScore().getValue()));

            } else {
                removeSnakeFromGrid();
                snake.move();
                addSnakeToGrid();

                if (fruitLocation == null) {
                    addFruitToGrid();
                } else {
                    //фрукт съедается.
                    //мы должны вырастить змею,
                    //удалить плод и добавить
                    //новый сегмент змейки в сетке.
                    if (nextHeadPosition.equals(fruitLocation)) {
                        fruitLocation = null;
                        Pair<Integer, Integer> newSegmentLocation = snake.createNewSegment();
                        grid[newSegmentLocation.getKey()][newSegmentLocation.getValue()] = SNAKE_SEGMENT;
                        score.setValue(score.get() + 1);
                    }
                }
            }
        }

    }

    // Положите фрукт на свободное место на решетке.
    private void addFruitToGrid() {
        ArrayList<Pair<Integer, Integer>> possibleLocations = new ArrayList<>();

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == 0) {
                    possibleLocations.add(new Pair<>(r, c));
                }
            }
        }

        Random random = new Random();
        int index = random.nextInt(possibleLocations.size());

        fruitLocation = possibleLocations.get(index);
        grid[fruitLocation.getKey()][fruitLocation.getValue()] = FRUIT;
    }

   //Возвращает игру в исходное состояние.
    public void reset() {
        score.setValue(0);
        this.snake = new Snake();
        grid = new int[NUM_ROWS][NUM_COLUMNS];
        fruitLocation = null;
        paused = false;
        hasLost = false;
    }

    public void togglePause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean hasLost() {
        return hasLost;
    }

    public Pair<Integer, Integer> getFruitLocation() {
        return fruitLocation;
    }

    public static IntegerProperty getScore() {
        return score;
    }

    public void requireRecords() {
        client.getRecords();
    }

    @Override
    public void setRecords(ArrayList<SnakeRecord> records) {
        controller.showDialogRecords(records);
    }
}
