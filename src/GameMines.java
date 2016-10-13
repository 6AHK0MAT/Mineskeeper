import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Timer;

/**
 * Created by mva on 13.10.2016.
 */


public class GameMines extends JFrame {

    final String TITLE_OF_PROGRAMM = "Mines";   //Переменная не будет переопределяться или изменяться в процессе работы программы
    final String SIGN_OF_FLAG = "f"; //Флаг
    final int BLOCK_SIZE = 30; //Размер блока
    final int FIELD_SIZE = 9; //Размер поля
    final int FIELD_DX = 6; //Примерная ширина рамок
    final int FIELD_DY = 28 + 17; //Примерная ширина заголовка
    final int START_LOCATOIN = 200;
    final int MOUSE_BUTTON_LEFT = 1; //Константа при нажатии на кнопку мышки
    final int MOUSE_BUTTON_RIGHT = 1;
    final int NUMBER_OF_MINES = 10; //Количество мин
    final int[] COLOR_OF_NUMBERS = {0x0000FF, 0x008000, 0xFF0000, 0x800000, 0x0}; //Массив цветов для цифр
    Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE]; //Двухмерный массив объектов клеточки
    Random random = new Random();
    int countOpenedCells; //Количество открытых ячеек
    boolean youWon, bangMine; //Вы выиграли, вы подорвались
    int bangX, bangY; //Координаты взрыва

    //Главный метод
    public static void main(String[] args) {
        new GameMines(); //В момент старта программы создается объект
    }

    //Конструктор
    GameMines() {
        setTitle(TITLE_OF_PROGRAMM); //Метод устанавливающий заголовок окна
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(START_LOCATOIN, START_LOCATOIN, FIELD_SIZE * BLOCK_SIZE + FIELD_DX, FIELD_SIZE * BLOCK_SIZE + FIELD_DY); //Стартовое положение окна
        setResizable(false); //отключаем возможность масштабирования окна
        TimerLabel timerLabel = new TimerLabel();
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Canvas canvas = new Canvas(); //Создаем панель
        canvas.setBackground(Color.white); //Цвет фона

        //Прослушиватель мыши (используем метод класса Canvas addMouseListener
        //Параметром является объект MouseAdapter
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            //Переопределяем метод mouseReleased (перегружаем, переписываем)(входящим параметром является MouseEvent)
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e); //Вызываем метод mouseReleased у родительского класса
                int x = e.getX() / BLOCK_SIZE; //Обращаемся к переменной е(объект MouseEvent) получаем абсолютные координаты клика нашей мышки, делим на размер блока и получаем относительные координаты
                int y = e.getY() / BLOCK_SIZE;
                if (e.getButton() == MOUSE_BUTTON_LEFT && !bangMine && !youWon) //Проверяем нажатали левая кнопка мышки (щелчок) и мина не взорвалась и я не победил
                    //Если это поле не открыто, то я эт ополе открываю
                    if (field[y][x].isNotOpen()) {
                        openCells(x, y);
//                        field[y][x].open();
                        youWon = countOpenedCells == FIELD_SIZE * FIELD_SIZE - NUMBER_OF_MINES; //Если открыты все ячейки то я победил (количество ячеек - количество бомб, получаем количество свободных ячеек

                        //Если взорвалась мина, то запоминаю координаты ячейки где мина взорвалась
                        if (bangMine) {
                            bangX = x;
                            bangY = y;
                        }
                    }
                //Если нажал на правую кнопку мыши
                if (e.getButton() == MOUSE_BUTTON_RIGHT) field[y][x].inverseFlag(); //У поля инвертируется флаг
                field[y][x].inverseFlag();
                    if (bangMine || youWon) timerLabel.stopTimer();
                canvas.repaint(); //Перерисовывается экран
            }
        });
        add(BorderLayout.CENTER, canvas); //Устанавливаем в центр
        add(BorderLayout.SOUTH, timerLabel);
        setVisible(true); //Делаем окно видимым
        initField(); //Инициализируем поле

    }

    //Метод циклическое открывание пустых ячеек
    //Рекурсивный метод (внутри он вызывает сам себя) ВАЖНО УСЛОВИЕ ВОЗВРАТА (возврата)!!!
    void openCells(int x, int y) {
        if (x < 0 || x > FIELD_SIZE - 1 || y < 0 || y > FIELD_SIZE - 1) //Плохие координаты
            return;
        if (!field[y][x].isNotOpen()) //Если ячейка уже открыта
            return;
        field[y][x].open(); //Открываем ячейку
        if (field[y][x].getCountBomb() > 0 || bangMine) //Если количество бомб больше 0 или бомба взорвалась (если ячейка не пустая)
            return;
        for (int dx = -1; dx < 2; dx++) {//двойной цикл по соседним 8 ячейкам)
            for (int dy = -1; dy < 2; dy++) {
                openCells(x + dx, y + dy);

            }

        }
    }


    //Метод инициализации поля
    void initField() {                                                      // инициализация игрового поля
        int x;                                                              // create cells for the field
        int y;                                                              // create cells for the field
        int countMines = 0;

        //Проходим по полю и создает каждую клетку (объект cell) двухмерный массив объектов (свойства и методы описываются в классе cell)
        for (x = 0; x < FIELD_SIZE; x++) {
            for (y = 0; y < FIELD_SIZE; y++) {
                field[y][x] = new Cell();
            }
        }
        // Генерируем миины и расставляем их рандомно
        while (countMines < NUMBER_OF_MINES) {
            do {
                x = random.nextInt(FIELD_SIZE);
                y = random.nextInt(FIELD_SIZE);
            } while (field[y][x].isMined());
            field[y][x].mine();
            countMines++;
        }
        // Считаем мины вокруг, перебираются объекты которые находятся в массиве
        for (x = 0; x < FIELD_SIZE; x++) {
            for (y = 0; y < FIELD_SIZE; y++) {
                if (!field[y][x].isMined()) {
                    int count = 0;
                    for (int dx = -1; dx < 2; dx++) {
                        for (int dy = -1; dy < 2; dy++) {
                            int nX = x + dx;
                            int nY = y + dy;
                            if (nX < 0 || nY < 0 || nX > FIELD_SIZE - 1 || nY > FIELD_SIZE - 1) {
                                nX = x;
                                nY = y;
                            }
                            count += (field[nY][nX].isMined()) ? 1 : 0;
                        }
                        field[y][x].setCountBomb(count);
                    }
                }
            }
        }
    }


    class Cell {
        private boolean isOpen, isMine, isFlag;
        private int countBombNear;

        void open() {
            isOpen = true;
            bangMine = isMine;
            if (!isMine) countOpenedCells++;
        }

        boolean isNotOpen() {
            return !isOpen; //Проверяем открыта ли ячейка
        }

        void inverseFlag() {
            isFlag = !isFlag;
        }

        boolean isMined() {
            return isMine;
        }

        //Минируем ячейку
        void mine() {
            isMine = true;
        }

        //Устанавливаем и подсчитываем кол-во бомб
        void setCountBomb(int count) {
            countBombNear = count;
        }

        int getCountBomb() {
            return countBombNear;
        }

        //Метод рисования бомбы
        void paintBomb(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRect(x * BLOCK_SIZE + 7, y * BLOCK_SIZE + 10, 18, 10);
            g.fillRect(x * BLOCK_SIZE + 11, y * BLOCK_SIZE + 6, 10, 18);
            g.fillRect(x * BLOCK_SIZE + 9, y * BLOCK_SIZE + 8, 14, 14);
            g.setColor(Color.white);
            g.fillRect(x * BLOCK_SIZE + 11, y * BLOCK_SIZE + 10, 4, 4);
        }

        //Метод рисования строки, оборажает цифру или f
        void paintString(Graphics g, String str, int x, int y, Color color) {
            g.setColor(color);
            g.setFont(new Font("", Font.BOLD, BLOCK_SIZE));
            g.drawString(str, x * BLOCK_SIZE + 8, y * BLOCK_SIZE + 26);
        }

        void paint(Graphics g, int x, int y) {
            g.setColor(Color.lightGray);
            g.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);//Рисуем праямоугольник
            if (!isOpen) {      //Если ячейка не открыта
                if ((bangMine || youWon) && isMine) { //Если мины взорвались или я победил или ячейка минирована, то рисуется бомба черного цвета
                    paintBomb(g, x, y, Color.black);
                } else {
                    g.setColor(Color.lightGray); //Рисуется прямоугольник
                    g.fill3DRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, true);
                    if (isFlag) {
                        paintString(g, SIGN_OF_FLAG, x, y, Color.red);//Поверх рисуется флаг
                    }
                }
            } else {
                if (isMine) {//Если ячейка открыта
                    paintBomb(g, x, y, bangMine ? Color.red : Color.black); //Если в ячейке бомба, то рисуется бомба
                } else {
                    if (countBombNear > 0) {
                        paintString(g, Integer.toString(countBombNear), x, y, new Color(COLOR_OF_NUMBERS[countBombNear - 1]));
                    }
                }
            }

        }

    }


    class TimerLabel extends JLabel {                                // label with stopwatch
        Timer timer = new Timer();

        TimerLabel() {
            timer.scheduleAtFixedRate(timerTask, 0, 1000);           // TimerTask task, long delay, long period
        }

        TimerTask timerTask = new TimerTask() {
            volatile int time;
            Runnable refresher = new Runnable() {
                @Override
                public void run() {
                    TimerLabel.this.setText(String.format("%02d:%02d", time / 60, time % 60));
                }
            };

            public void run() {
                time++;
                SwingUtilities.invokeLater(refresher);
            }
        };

        void stopTimer() {
            timer.cancel();
        }
    }


    class Canvas extends JPanel {
        @Override //Переопределяем метод paint
        //Выходным параметром метода paint является Graphics
        public void paint(Graphics g) {
            super.paint(g); //super вызов родительского метода
            for (int x = 0; x < FIELD_SIZE; x++) {
                for (int y = 0; y < FIELD_SIZE; y++) {
                    field[y][x].paint(g, x, y); //Вызываем метод отрисовки объекта
                }
            }
        }
    }
}
