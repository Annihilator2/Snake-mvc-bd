package org.Maxim_SNAKE.model;

import org.Maxim_SNAKE.core.SnakeRecord;

import java.util.ArrayList;

public interface ClientCallback {

    void setRecords(ArrayList<SnakeRecord> records);
}
