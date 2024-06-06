import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.util.Timer;

public class Clock {
    private JLabel dateLabel;

    public void perform() {
        JFrame mainFrame = new JFrame("時計");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 120);
        mainFrame.setLayout(new GridLayout(1, 1));

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Yu Mincho Demibold", Font.BOLD, 48));
        dateLabel.setForeground(Color.BLACK);
        mainFrame.add(dateLabel);
        mainFrame.setVisible(true);

        TimerTask aTask = new TimerTask() {
            public void run() {
                Date aDate = new Date();
                dateLabel.setText(aDate.toString());
            }
        };

        Timer aTimer = new Timer();
        aTimer.scheduleAtFixedRate(aTask, 0, 1000);
    }

    // 正解時の時計色変更
    public void correctChange() {
        dateLabel.setForeground(Color.RED);
    }

    // 不正解時の時計色変更
    public void wrongChange() {
        dateLabel.setForeground(Color.BLUE);
    }

    // リセット時の時計色変更
    public void resetChange(){
        dateLabel.setForeground(Color.BLACK);
    }
}
