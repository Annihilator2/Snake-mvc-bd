package org.Maxim_SNAKE.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;
import org.Maxim_SNAKE.App;
import org.Maxim_SNAKE.model.Model;

import java.io.IOException;
import java.util.Optional;

public class StartController {

    @FXML
    private void startGame() throws IOException {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Имя игрока");
        dialog.setHeaderText(null);
        dialog.setContentText("Пожалуйста, введите свое имя:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.isEmpty()) {
                try {
                    // Передайте имя на следующую сцену или сохраните его в модели
                    // Для простоты давайте предположим, что в классе Model есть статическое поле для имени игрока
                    Model.playerName = name;

                    App.setRoot("gamescene");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Пожалуйста, введите корректное имя!");
                alert.showAndWait();
            }
        });
    }

}
