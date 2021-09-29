package ru.muffinnorth.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class MainForm extends JFrame{
    private final int MAX_HIST = 5;
    private static Logger logger = Logger.getLogger("MainForm");
    private JPanel rootPanel;
    private JPanel canvasPanel;

    public JTextField getSquareField() {
        return squareField;
    }

    private JTextField squareField;
    private JButton acceptButton;
    private JButton resetButton;
    private JTextField radiusField;
    private JLabel lastDoLabel;
    private Point prevPoint;
    private JMenu menuHistory;
    private JMenuItem[] radiusHistory = new JMenuItem[MAX_HIST];

    public boolean IsGridChecked() {
        return itemGrid.getState();
    }

    private JCheckBoxMenuItem itemGrid;
    private int histIndex = 1;

    private JMenu getMenuFile(){
        JMenu menu = new JMenu("Файл");
        menuHistory = new JMenu("История");
        JMenuItem itemExit = new JMenuItem("Выход");
        itemExit.addActionListener(e -> System.exit(0));
        menu.add(menuHistory);
        menu.add(new JSeparator());
        menu.add(itemExit);

        return menu;
    }
    private JMenu getMenuView(){
        JMenu menu = new JMenu("Вид");
        itemGrid = new JCheckBoxMenuItem("Сетка", true);
        menu.add(itemGrid);
        return menu;
    }
    private JMenu getMenuHelp(){
        JMenu menu = new JMenu("Помощь");
        JMenuItem itemHowTo = new JMenuItem("Как пользоваться...");
        menu.add(itemHowTo);
        JMenuItem itemAbout = new JMenuItem("Об авторе");
        itemAbout.addActionListener(e -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.example.com"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                }
            }
        });
        menu.add(itemAbout);
        return menu;
    }


    public MainForm(){
        logger("Запуск приложения");
        setContentPane(rootPanel);
        setTitle("SquareJCircle");
        setSize(920,640);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getMenuFile());
        menuBar.add(getMenuView());
        menuBar.add(getMenuHelp());
        this.setJMenuBar(menuBar);;

        Canvas canvas = new Canvas(this);
        logger("Создание поля отображения");
        canvasPanel.add(canvas);
        setMinimumSize(new Dimension(640,480));
        canvasPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                canvas.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                try {
                    canvas.setTranslation(prevPoint.x - e.getX(), prevPoint.y - e.getY());
                    logger("Перемещение на X: " + (prevPoint.x - e.getX()) + " Y: " + (prevPoint.y - e.getY()));
                }catch (NullPointerException npe) {
                }
                prevPoint = e.getPoint();
            }
        });
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                prevPoint = null;
                canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        canvasPanel.addMouseWheelListener(e -> canvas.setScaleDiff((double)e.getWheelRotation() / 100));
        acceptButton.addActionListener(e -> {
            try{
                double newRadius = Double.parseDouble(radiusField.getText());
                if(newRadius >= 0 && newRadius <= 1000000){
                    canvas.setRadius(newRadius);

                    JMenuItem item = new JMenuItem(String.valueOf(newRadius));
                    item.addActionListener(e1 -> {
                        logger("Загрузка из истории значения радиуса...");
                        canvas.setRadius(newRadius);
                        radiusField.setText(String.valueOf(newRadius));
                    });
                    try {
                        menuHistory.remove(radiusHistory[histIndex-1]);
                    }catch (NullPointerException exception){
                        System.out.println(exception);
                    }
                    radiusHistory[histIndex-1] = item;
                    menuHistory.add(item);
                    if(histIndex >= MAX_HIST){
                        histIndex = 1;
                    }
                    histIndex++;



                    logger("Установленно новое значение радиуса");
                }else{
                    logger("Число не попадает в ограничения от 0 до 1000000: " + radiusField.getText());
                    JOptionPane.showMessageDialog(null, "Число не попадает в ограничения от 0 до 1000000!");
                }
            }catch (NumberFormatException nfe){
                logger("Ошибка в формате числа: " + radiusField.getText());
                JOptionPane.showMessageDialog(null, "Неверный формат ввода радиуса!");
            }
        });

        radiusField.setText(String.valueOf(canvas.getRadius()));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.reset();
                logger("Сброс положения");
            }
        });
    }

    public void logger(String msg){
        logger.info(msg);
        lastDoLabel.setText(msg);
    }


    public static void main(String[] args) {
        new MainForm();
    }
}
