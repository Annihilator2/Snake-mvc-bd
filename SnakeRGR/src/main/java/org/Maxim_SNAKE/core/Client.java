package org.Maxim_SNAKE.core;

import javafx.application.Platform;
import org.Maxim_SNAKE.model.ClientCallback;
import org.Maxim_SNAKE.model.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private boolean isConnect = false;
    private String clientName;
    private ObjectOutputStream outputStream;
    private ClientCallback clientCallback;
    public ArrayList<SnakeRecord> records = new ArrayList<>();

    public Client(ClientCallback clientCallback) {
        this.clientCallback = clientCallback;
    }

    public void sendResults(String playerName, String score) {
        new Thread(() -> {
            try {
                String str = playerName + " " + score;
                outputStream.writeObject(str);
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public void start(String address, int port, String name) {
        try {
            if (isConnect) return;

            Socket socket = new Socket(address, port);
            clientName = name;
            System.out.println("Подключено к серверу");
            isConnect = true;

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            // Отправляем сигнал серверу с именем клиента
            outputStream.writeObject(clientName);
            outputStream.flush();

            // Запускаем поток для чтения сообщений от сервера
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String str = (String) inputStream.readObject();
                        records.clear();
                        String[] splited = str.split("%");
                        for (int i = 0; i < splited.length; i++) {
                            String playerName = splited[i].split(" ")[0];
                            int score = Integer.parseInt(splited[i].split(" ")[1]);
                            SnakeRecord sr = new SnakeRecord(playerName, score);
                            records.add(sr);
                        }
                        Platform.runLater(() -> {
                            clientCallback.setRecords(records);
                        });
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Сервер недоступен");
        }
    }

    public void getRecords() {
        new Thread(() -> {
            try {
                outputStream.writeObject("getrecords");
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
