package org.Maxim_SNAKE.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.Maxim_SNAKE.core.SnakeRecord;
import org.Maxim_SNAKE.model.Model;
import org.Maxim_SNAKE.core.GameHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Контроллер для всех игровых взаимодействий
public class Controller implements IController {

    @FXML
    private Button showRecordsButton;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Label scoreValueLabel;

    private Model model;
    private GameHandler gameLoopHandler;
    @FXML
    private ChoiceBox<String> colorBackgroundChoiceBox;
    @FXML
    private ChoiceBox<String> colorChoiceBox;

    @FXML
    private ChoiceBox<String> difficultyChoiceBox;

    @FXML
    private ChoiceBox<String> snakeColorChoiceBox;

    @FXML
    private ChoiceBox<String> fruitColorChoiceBox;

    private void initializeColorChoiceBox() {
        List<String> colorNames = new ArrayList<>();
        colorNames.add("Red");
        colorNames.add("Green");
        colorNames.add("Blue");
        colorNames.add("Black");
        colorNames.add("Chocolate");
        colorNames.add("Dark Orange");

        colorChoiceBox.setItems(FXCollections.observableList(colorNames));
        snakeColorChoiceBox.setItems(FXCollections.observableList(colorNames));
        fruitColorChoiceBox.setItems(FXCollections.observableList(colorNames));

        colorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Color color = getColorFromName(newValue);
            gameLoopHandler.setGridColor(color);
        });

        snakeColorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Color color = getColorFromName(newValue);
            gameLoopHandler.setSnakeColor(color);
        });

        fruitColorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Color color = getColorFromName(newValue);
            gameLoopHandler.setFruitColor(color);
        });

        showRecordsButton.setOnMouseClicked(e -> {
            requireRecords();
        });

    }

    private void requireRecords() {
        model.requireRecords();
    }
    public static Color getColorFromName(String colorName) {
        switch (colorName) {
            case "Red":
                return Color.RED;
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            case "Black":
                return Color.BLACK;
            case "Chocolate":
                return Color.CHOCOLATE;
            case "Dark Orange":
                return Color.DARKORANGE;
            case "WHITE":
                return Color.WHITE;
            case "CORAL":
                return Color.CORAL;
            default:
                return null;
        }
    }
    private void initializeDifficultyChoiceBox() {
        List<String> difficultyLevels = new ArrayList<>();
        difficultyLevels.add("Easy");
        difficultyLevels.add("Medium");
        difficultyLevels.add("Hard");

        difficultyChoiceBox.setItems(FXCollections.observableList(difficultyLevels));
        difficultyChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int difficultyLevel = getDifficultyLevelFromName(newValue);
            GameHandler.setDifficultyLevel(difficultyLevel);
        });
    }

    private int getDifficultyLevelFromName(String difficultyName) {
        switch (difficultyName) {
            case "Easy":
                return 1;
            case "Medium":
                return 2;
            case "Hard":
                return 3;
            default:
                return 1;
        }
    }

    @FXML
    public void initialize() {
        model = new Model(this);
        gameLoopHandler = new GameHandler(gameCanvas, model);
        gameLoopHandler.start();
        initializeColorChoiceBox();
        scoreValueLabel.textProperty().bind(model.getScore().asString());
        initializeDifficultyChoiceBox();
    }

    @Override
    public void showDialogRecords(ArrayList<SnakeRecord> records) {
        records.sort(Comparator.comparingInt(o -> o.score));
        Collections.reverse(records);

        ArrayList<String> stringRecords = new ArrayList<>();

        for (SnakeRecord sr: records) {
            stringRecords.add(sr.toString());
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Records");

        ListView<String> listView = new ListView<>(FXCollections.observableArrayList(stringRecords));

        dialog.getDialogPane().setContent(listView);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> dialog.close());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }
}

