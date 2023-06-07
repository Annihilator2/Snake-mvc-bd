module org.jpierre {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens org.Maxim_SNAKE to javafx.fxml;
    opens org.Maxim_SNAKE.controller to javafx.fxml;
    exports org.Maxim_SNAKE;
    exports org.Maxim_SNAKE.controller;
}
