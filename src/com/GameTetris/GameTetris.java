package com.GameTetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameTetris {
    final String name = "Tetris";
    final int size_brick = 25; // размер одного блока
    final int size_radius = 15; // закругленность кирпичика

    final int size_width = 10; // размер игрового поля
    final int size_height = 20;

    final int size_DX = 17;
    final int size_DY = 39;

    final int LEFT = 37; //стрелки
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int delay = 550; // задержка анимации падения
    final int[][][] SHAPES; //этот массив содержит параметры размеров и цветов каждой фигурки состоящей из кирпичиков
    {
        SHAPES = new int[][][]{
                {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}, {4, 0x00f000}}, // прямая
                {{0, 0, 0, 0}, {0, 1, 1, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {4, 0xf0f000}}, // квадрат
                {{1, 0, 0, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x0000f0}}, // L
                {{0, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf0a000}}, // L
                {{0, 1, 1, 0}, {1, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0x00f0f0}}, // S
                {{1, 1, 1, 0}, {0, 1, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xa000f0}}, // T
                {{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {3, 0xf00000}}  // Z
        };
    }
    final int[] SCORES;
    int game_SCORES;
    int[][] mine; // массив который определяет куда падают фигуры. с его помощью определяем заполненность игрового поля
    JFrame frame;
    playing_FIELD playing_field;
    Random random;
    Figure brick;
    {
        mine = new int[size_height + 1][size_width];
        game_SCORES = 0;
        SCORES = new int[]{100, 300, 900, 2700}; // колличество очков за закрытие 1,2,3 или 4-х строк соответственно
        playing_field = new playing_FIELD();
        random = new Random();
        brick = new Figure();
    }
    boolean gameOver;
    {
        gameOver = false;
    }
    final int[][] GAME_OVER;
    {
        GAME_OVER = new int[][]{ //game over
                {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0},
                {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1},
                {1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
                {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0},
                {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
                {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0},
                {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0},
                {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0},
                {0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0}};
    }
    public static void main(String[] args) {
        // write your code here
        new GameTetris().launch();
    }
    void launch() {
        // основной цикл игры
        frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(size_width * size_brick + size_DX, size_height * size_brick + size_DY);
        frame.setLocationRelativeTo(null); //установил окно по середине
        frame.setResizable(false);
        playing_field.setBackground(Color.black); // определяю цвет фона
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { // метод вызывается по нажатию клавиши
                if (!gameOver) {
                    if (e.getKeyCode() == DOWN) brick.drop(); // стрелка вниз, фигура падает
                    if (e.getKeyCode() == UP) brick.rotate(); // стрелка вверх, фигура делает поворот
                    if (e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT) brick.move(e.getKeyCode()); //стрелка влево или вправо, фигура перемещается в одну из сторон
                }
                playing_field.repaint();
            }
        });
        frame.getContentPane().add(BorderLayout.CENTER, playing_field);
        frame.setVisible(true);
        Arrays.fill(mine[size_height], 1); // нижняя граница
        //главный цикл
        while (!gameOver){
            try {
                Thread.sleep(delay);  // вызываю задержку перед прорисовкой через обработчик прераваний
            } catch (Exception e) { e.printStackTrace(); }
            playing_field.repaint();
            if (brick.isTouchGround()) {  //проверяю коснулась ли фигура земли
                brick.leaveOnTheGround(); // оставляем фигуру на земле
                checkFilling(); //проверка заполнения строки
                brick = new Figure(); // новая фигура
                gameOver = brick.isCrossGround(); // проверка, есть ли место для новой фигуры, если нет, то игра заканчивается
            } else
                brick.stepDown();
        }
    }
    void checkFilling(){ //проверка заполнения строки
        int row = size_height - 1;
        int countFillRows = 0;
        while (row > 0) {
            int filled = 1;
            for (int col = 0; col < size_width; col++)
                filled *= Integer.signum(mine[row][col]);
            if (filled > 0) {
                countFillRows++;
                for (int i = row; i > 0; i--) System.arraycopy(mine[i-1], 0, mine[i], 0, size_width); //если строки заполнены, то убирает их
            } else
                row--;
        }
        if (countFillRows > 0) { //согласно колличеству убранных строк начисляем баллы (100 за строку)
            game_SCORES += SCORES[countFillRows - 1];
            frame.setTitle(name + " : " + game_SCORES); // отображаем счет в заголовке игрового окна
        }
    }
    class Figure {
        private ArrayList<Block> figure = new ArrayList<Block>(); //массив списков
        private int[][] turn_shape = new int[4][4];
        private int type, size, color; //тип, размер матрицы фигуры, цвет.
        private int x = 3, y = 0; // starting left up corner

        Figure() {
            type = random.nextInt(SHAPES.length);
            size = SHAPES[type][4][0];
            color = SHAPES[type][4][1];
            if (size == 4) y = -1;
            for (int i = 0; i < size; i++) //заполняем массив turn_shape
                System.arraycopy(SHAPES[type][i], 0, turn_shape[i], 0, SHAPES[type][i].length);
            createFromShape();
        }
        void move(int direction){ // передвижение фигуры влево, вправо
            if (!isTouchWall(direction)) {
                int dx = direction - 38; // влево = -1, вправо = 1
                for (Block block : figure) block.setX(block.getX() + dx);
                x += dx;
            }
        }
        void drop(){ //падение фигуры
            while (!isTouchGround()) stepDown(); // моментальное падение фигуры вниз
        }
        void rotate(){ //поворот
            rotateShape(RIGHT);
            if (!isWrongPosition()) { // если фигура не упирается в стену и ее можно повернуть
                figure.clear(); // удаляем текущуу фигуру
                createFromShape(); // и добавляем новую по уже повернутому массиву
            } else
                rotateShape(LEFT);
        }
        void rotateShape(int direction) {
            for (int i = 0; i < size/2; i++)
                for (int j = i; j < size-1-i; j++)
                    if (direction == RIGHT) { // поворот вправо
                        int tmp = turn_shape[size-1-j][i];
                        turn_shape[size-1-j][i] = turn_shape[size-1-i][size-1-j];
                        turn_shape[size-1-i][size-1-j] = turn_shape[j][size-1-i];
                        turn_shape[j][size-1-i] = turn_shape[i][j];
                        turn_shape[i][j] = tmp;
                    } else { // поворот влево
                        int tmp = turn_shape[i][j];
                        turn_shape[i][j] = turn_shape[j][size-1-i];
                        turn_shape[j][size-1-i] = turn_shape[size-1-i][size-1-j];
                        turn_shape[size-1-i][size-1-j] = turn_shape[size-1-j][i];
                        turn_shape[size-1-j][i] = tmp;
                    }
        }
        boolean isWrongPosition() {
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    if (turn_shape[y][x] == 1) {
                        if (y + this.y < 0) return true;  //проверка выхода фигуры за границы координат
                        if (x + this.x < 0 || x + this.x > size_width - 1) return true; //проверка выхода фигуры за границы координат
                        if (mine[y + this.y][x + this.x] > 0) return true; // проверка соприкасновения фигур
                    }
            return false;
        }
        void createFromShape(){ //цикл, проходит по массиву shape. Основываясь на массиве turn_shape строит фигуру
            for (int x = 0; x < size; x++)
                for (int y = 0; y < size; y++)
                    if (turn_shape[y][x] == 1) figure.add(new Block(x + this.x, y + this.y));
        }
        void paint(Graphics g) { //метод рисования
            for (Block block : figure) block.paint(g, color); // проходим по фигуре состоящей из массивов блоков и каждый блок рисуем
        }
        boolean isTouchGround(){
            for (Block block : figure) if (mine[block.getY() + 1][block.getX()] > 0) return true; // проверяю следующую строку перед фигурой 
            return false;
        }
        void leaveOnTheGround(){
            for (Block block : figure) mine[block.getY()][block.getX()] = color;
        }
        boolean isCrossGround(){
            for (Block block : figure) if (mine[block.getY()][block.getX()] > 0) return true;
            return false;
        }
        boolean isTouchWall(int direction) {
            for (Block block : figure) {
                if (direction == LEFT && (block.getX() == 0 || mine[block.getY()][block.getX() - 1] > 0)) return true;
                if (direction == RIGHT && (block.getX() == size_width - 1 || mine[block.getY()][block.getX() + 1] > 0)) return true;
            }
            return false;
        }
        void stepDown(){
            for (Block block : figure) block.setY(block.getY() + 1);
            y++;
        }
    }
    class Block { // обеспечивает работу со строительной единицей (блоком)
        private int x, y;
        public Block(int x, int y) {
            setX(x);
            setY(y);
        }
        void setX(int x) { this.x = x; }
        void setY(int y) { this.y = y; }
        int getX() { return x; }
        int getY() { return y; }
        void paint(Graphics g, int color) {
            g.setColor(new Color(color)); //устанавливаем цвет
            g.drawRoundRect(x* size_brick +1, y* size_brick +1, size_brick -2, size_brick -2, size_radius, size_radius); //рисуем фигуру
        }
    }
    public class playing_FIELD extends JPanel  { // обеспечивает отрисовку
            @Override
            public void paint(Graphics g) {
            super.paint(g); // вызываем метод paint чтобы была прорисовка старых фигур
                for (int x = 0; x < size_width; x++)
                    for (int y = 0; y < size_height; y++) {
                        if (x < size_width - 1 && y < size_height - 1) {
                            g.setColor(Color.lightGray);
                            g.drawLine((x+1)* size_brick -2, (y+1)* size_brick, (x+1)* size_brick +2, (y+1)* size_brick);
                            g.drawLine((x+1)* size_brick, (y+1)* size_brick -2, (x+1)* size_brick, (y+1)* size_brick +2);
                        }
                        if (mine[y][x] > 0) { //прорисовка фигур лежащих на земле
                            g.setColor(new Color(mine[y][x]));
                            g.fill3DRect(x* size_brick +1, y* size_brick +1, size_brick -1, size_brick -1, true);
                        }
                    }
                if (gameOver) {
                    g.setColor(Color.white);
                    for (int y = 0; y < GAME_OVER.length; y++)
                        for (int x = 0; x < GAME_OVER[y].length; x++)
                            if (GAME_OVER[y][x] == 1) g.fill3DRect(x*11+18, y*11+160, 10, 10, true);
                } else
                brick.paint(g);
            }
    }
}
