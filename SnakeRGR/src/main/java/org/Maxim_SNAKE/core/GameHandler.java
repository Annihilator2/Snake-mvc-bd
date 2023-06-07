package org.Maxim_SNAKE.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.Maxim_SNAKE.model.Model;

import java.io.IOException;
import java.util.ArrayList;


// Управляет игровым циклом и выполняет все действия
// которые должны выполняться во время каждого цикла, например
// обработка входных данных, обновление представления и обновление модели.
public class GameHandler extends AnimationTimer {

    private Canvas canvas;
    private static Color backgroundColor=Color.WHITE;
    private GraphicsContext gc;
    private Model model;
    private static int difficultyLevell=1;
    private ArrayList<String> keysPressed;
    private ArrayList<String> previousKeysPressed;
    private long timeToMove = (long)(0.1 * Math.pow(10, 9));
    private Long lastMoveTimestamp;
    private Color gridColor=Color.BLACK;
    private Color fruitColor=Color.RED;
    private Color SnakeColor=Color.GREEN;
    public GameHandler(Canvas canvas, Model model) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.model = model;
        this.keysPressed = new ArrayList<>();
        this.previousKeysPressed = new ArrayList<>();
    }

    public void setGridColor(Color color) {
        gridColor = color;
    }


    public void setFruitColor(Color ColorF) {
      fruitColor = ColorF;
    }

    public void setSnakeColor(Color ColorS) {
        SnakeColor = ColorS;
    }

    public int getDifficultyLevel() {
        return difficultyLevell;
    }

    public static void setDifficultyLevel(int difficultyLevel) {
        difficultyLevell = difficultyLevel;
    }

   //Очищает и снова рисует пользовательский интерфейс, обновляя его при необходимости.
    void drawView() {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawGrid();
        drawFruit();
        drawSnake();

        if(model.hasLost()) {
            gc.setFont(new Font("System", 20));
            gc.setFill(Color.BLACK);
            gc.fillText("Ты проиграл! Нажмите R для перезапуска.", (canvas.getWidth()/2) - 125, canvas.getHeight()/2, 300);
        } else if(model.isPaused()) {
            gc.setFont(new Font("System", 20));
            gc.setFill(Color.BLACK);
            gc.fillText("Пауза! Нажмите P еще раз, чтобы снять паузу.", (canvas.getWidth()/2) - 150, canvas.getHeight()/2, 300);
        }
    }

  //Рисует сетку для пользовательского интерфейса.
    void drawGrid() {
        double vertSpace = canvas.getWidth() / Model.NUM_COLUMNS;
        double horSpace = canvas.getHeight() / Model.NUM_ROWS;
        for(double currX = vertSpace; currX<canvas.getWidth(); currX+=vertSpace) {
            gc.setStroke(gridColor);
            gc.strokeLine(currX,0, currX, canvas.getHeight());
        }

        for(double currY = horSpace; currY<canvas.getHeight(); currY+=horSpace) {
            gc.setStroke(gridColor);
            gc.strokeLine(0, currY, canvas.getWidth(), currY);
        }
    }

    //Этот метод рисует все сегменты змеи.
    void drawSnake() {
        ArrayList<Pair<Integer,Integer>> snakeSegments = model.getSnake().getAllSegments();
        double colSpace = canvas.getWidth() / Model.NUM_COLUMNS;
        double rowSpace = canvas.getHeight() / Model.NUM_ROWS;

        for(Pair<Integer,Integer> coords : snakeSegments) {
            gc.setFill(SnakeColor);
            gc.fillRect(colSpace * coords.getValue(), rowSpace * coords.getKey(), colSpace, rowSpace);
        }
    }

    void drawFruit() {
        Pair<Integer,Integer> fruitLocation = model.getFruitLocation();
        double colSpace = canvas.getWidth() / Model.NUM_COLUMNS;
        double rowSpace = canvas.getHeight() / Model.NUM_ROWS;

        if(fruitLocation != null) {
            gc.setFill(fruitColor);
            gc.fillRect(colSpace * fruitLocation.getValue(), rowSpace * fruitLocation.getKey(), colSpace, rowSpace);
        }
    }




    void handleInput() {
        ArrayList<String> actionableInput = new ArrayList<>(keysPressed);

        if(!previousKeysPressed.isEmpty() && !actionableInput.isEmpty()) {
            if(actionableInput.get(0).equals(previousKeysPressed.get(0))) {
                actionableInput.remove(0);
            }
        }

        if(!actionableInput.isEmpty()) {
            String keyInput = actionableInput.get(0);

            switch(keyInput) {

                case "W":
                    model.getSnake().setDirection(Snake.UP);
                    break;


                case "S":
                    model.getSnake().setDirection(Snake.DOWN);
                    break;


                case "A":
                    model.getSnake().setDirection(Snake.LEFT);
                    break;


                case "D":
                    model.getSnake().setDirection(Snake.RIGHT);
                    break;

                case "P":
                    model.togglePause();
                    break;

                case "R":
                    if(model.hasLost()) {
                        model.reset();
                        lastMoveTimestamp = null;
                    }
            }
        }
    }

    //Этот метод выполняется во время каждого цикла игрового цикла.
    //param l Текущая системная временная метка в наносекундах.
    @Override
    public void handle(long l) {
        if(canvas.getScene().getOnKeyPressed() == null) {
            canvas.getScene().setOnKeyPressed(keyEvent -> {
                if(!keysPressed.contains(keyEvent.getCode().toString()))
                    keysPressed.add(keyEvent.getCode().toString());
            });

            canvas.getScene().setOnKeyReleased(keyEvent -> {
                keysPressed.remove(keyEvent.getCode().toString());
            });
        }

        if(!keysPressed.isEmpty()) {
            handleInput();
        }

        drawView();

        if(lastMoveTimestamp == null) {
            lastMoveTimestamp = l;
        }

        if (!model.isPaused() && !model.hasLost()) {
            if (l - lastMoveTimestamp >= timeToMove / getDifficultyLevel()) {
                try {
                    model.updateModel();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                lastMoveTimestamp = l;
            }
        }




        previousKeysPressed.clear();
        previousKeysPressed.addAll(keysPressed);
    }


}
