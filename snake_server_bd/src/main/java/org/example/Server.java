package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

class SnakeRecord {
    public String playerName;
    public int score;

    public SnakeRecord(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    @Override
    public String toString() {
        return playerName + " " + score;
    }
}

public class Server {
    private String dbURL = "jdbc:postgresql://localhost:5432/postgres";
    private String username = "root";
    private String password = "1111";
    private Connection connection;
    private Statement statement;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final ArrayList<SnakeRecord> records = new ArrayList<>();
    private int port = 3000;

    public void run() {
        try {
            // Установка соединения с базой данных
            connection = DriverManager.getConnection(dbURL, username, password);
            statement = connection.createStatement();

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент подключен");

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateRecordsFromDatabase() {
        records.clear();

        String selectQuery = "SELECT player_name, score FROM player_records ORDER BY score DESC LIMIT 10";

        try (ResultSet resultSet = statement.executeQuery(selectQuery)) {
            while (resultSet.next()) {
                String playerName = resultSet.getString("player_name");
                int score = resultSet.getInt("score");
                records.add(new SnakeRecord(playerName, score));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveRecordToDatabase(String playerName, int score) {
        try {
            // Проверка существования записи
            String checkQuery = "SELECT score FROM player_records WHERE player_name = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, playerName);
            ResultSet resultSet = checkStatement.executeQuery();

            // Если запись существует, обновляем ее
            if (resultSet.next()) {
                int existingScore = resultSet.getInt("score");
                if (score > existingScore) {
                    String updateQuery = "UPDATE player_records SET score = ? WHERE player_name = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, score);
                    updateStatement.setString(2, playerName);
                    updateStatement.executeUpdate();
                }
            }
            // Если записи не существует, вставляем новую
            else {
                String insertQuery = "INSERT INTO player_records (player_name, score) VALUES (?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, playerName);
                insertStatement.setInt(2, score);
                insertStatement.executeUpdate();
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private String name;
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        private void messageReceived(String str) {

            if (str.equals("getrecords")) {
                updateRecordsFromDatabase();

                StringBuilder query = new StringBuilder();
                for (SnakeRecord sr : records) {
                    query.append(sr.toString());
                    query.append("%");
                }
                query.deleteCharAt(query.length()-1);
                sendMessage(query.toString());
            } else {
                String[] splited = str.split(" ");
                String playerName = splited[0];
                int score = Integer.parseInt(splited[1]);

                saveRecordToDatabase(playerName, score);
                System.out.println(records);
            }
        }

        @Override
        public void run() {
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                String clientName = (String) inputStream.readObject();
                System.out.println("Клиент " + clientName + " подключен");
                name = clientName;

                while (true) {
                    String str = (String) inputStream.readObject();
                    System.out.println("Получено сообщение от " + clientName + ": " + str);
                    messageReceived(str);
                }

            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    clients.remove(this);
                    System.out.println("Клиент " + name + " отключен");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            try {
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
