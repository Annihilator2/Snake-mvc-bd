package org.Maxim_SNAKE.core;

import javafx.util.Pair;
import org.Maxim_SNAKE.model.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

//Класс, представляющий змею, управляемую игроком.
public class Snake {

    //Координаты головы змеи.
    //Ключ = строка. Значение = столбец
    private Pair<Integer, Integer> head;

    //Координаты всех остальных частей змеи.
    private ArrayList<Pair<Integer,Integer>> segments;

    //Список, записывающий предыдущие местоположения головки.
    private LinkedList<Pair<Integer,Integer>> previousLocations;

    //Направление, в котором движется змея.
    private int direction;

    //Направление, в котором двигалась змея во время
    //предыдущего хода.
    private int prevDirection;

    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    public Snake() {
        int vertMid = Model.NUM_ROWS / 2;
        int horMid = Model.NUM_COLUMNS / 2;

        head = new Pair<>(vertMid, horMid);

        segments = new ArrayList<>();
        segments.add(new Pair<>(vertMid, horMid-1));
        segments.add(new Pair<>(vertMid, horMid-2));

        previousLocations = new LinkedList<>();
        previousLocations.addAll(segments);

        direction = RIGHT;
        prevDirection = RIGHT;
    }

    //Возвращает список всех сегментов этой змеи, включая ее голову.
    public ArrayList<Pair<Integer,Integer>> getAllSegments() {
        ArrayList<Pair<Integer,Integer>> result = new ArrayList<>();
        result.add(head);
        result.addAll(segments);
        return result;
    }

   // Возвращает смещение координат для головы змеи
   // Следующий ход.
    private Pair<Integer, Integer> getDirectionOffset() throws RuntimeException {
        switch(direction) {
            case UP:
                return new Pair<>(-1, 0);

            case DOWN:
                return new Pair<>(1, 0);

            case LEFT:
                return new Pair<>(0, -1);

            case RIGHT:
                return new Pair<>(0, 1);

            default:
                throw new RuntimeException("Invalid direction: " + direction);
        }
    }

   //Перемещает змейку один раз в ее текущем направлении.
    public void move() {
        Pair<Integer, Integer> offset = getDirectionOffset();

        int newRow = head.getKey() + offset.getKey();
        int newCol = head.getValue() + offset.getValue();

        previousLocations.push(head);
        head = new Pair<>(newRow, newCol);

        Iterator<Pair<Integer,Integer>> previousLocationIterator = previousLocations.iterator();
        for(int i = 0; i<segments.size(); i++) {
            segments.set(i, previousLocationIterator.next());
        }
        prevDirection = direction;
    }

  // Возвращает положение головы после выполнения следующего хода.
  // Возвращает пару, представляющую строку и столбец заголовка после
  // Делает свой следующий ход.
    public Pair<Integer,Integer> getNextHeadPosition() {
        Pair<Integer, Integer> offset = getDirectionOffset();

        int newRow = head.getKey() + offset.getKey();
        int newCol = head.getValue() + offset.getValue();
        return new Pair<>(newRow, newCol);
    }

  // Возвращает список позиций, представляющих змею
  // Сегменты после того, как они сделали свой следующий ход.
    public ArrayList<Pair<Integer,Integer>> getNextSegmentsPositions() {
    //это представляет, как выглядели бы предыдущие местоположения, если бы мы
    //сделали следующий шаг.
        ArrayList<Pair<Integer,Integer>> futurePreviousLocations = new ArrayList<>();
        futurePreviousLocations.add(head);
        futurePreviousLocations.addAll(previousLocations);

        ArrayList<Pair<Integer,Integer>> result = new ArrayList<>();

        Iterator<Pair<Integer,Integer>> previousLocationIterator = futurePreviousLocations.iterator();
        for(int i = 0; i<segments.size(); i++) {
            result.add(previousLocationIterator.next());
        }

        return result;
    }

  // Добавляет новый сегмент к этой змейке и
  // Возвращает положение нового сегмента.
    public Pair<Integer,Integer> createNewSegment() {
        int numSegments = segments.size();
        Pair<Integer,Integer> newSegment = previousLocations.get(numSegments);
        segments.add(newSegment);
        return newSegment;
    }

    public void setDirection(int newDirection) {

        boolean isValidDirection = !((prevDirection == LEFT && newDirection == RIGHT) ||
                (prevDirection == RIGHT && newDirection == LEFT) ||
                (prevDirection == UP && newDirection == DOWN) ||
                (prevDirection == DOWN && newDirection == UP));

        //Если это новое направление не приведет к появлению змеи
        //двигаться внутрь себя, тогда мы сможем двигаться в нужном направлении.
        if(isValidDirection) {
            this.direction = newDirection;
        }
    }


}
