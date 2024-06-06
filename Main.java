import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main extends JFrame {
    private JLabel questionLabel;
    private JTextField answerField;
    private JButton submitButton;
    private JButton restartButton;
    private JTextArea resultArea;
    private int count;
    private Clock aClock; // Clockクラスのインスタンスを保持

    // 正解時と不正解時のオーディオクリップを格納するフィールド
    private Clip correctClip;
    private Clip wrongClip;

    public Main(Clock clock) {
        super("九九ドリル");// ウィンドウ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 700); // ウィンドウサイズを調整
        setLayout(null);

        aClock = clock; // Clockクラスのインスタンスを受け取る

        questionLabel = new JLabel();// 問題文
        questionLabel.setBounds(20, 20, 150, 20);
        add(questionLabel);

        answerField = new JTextField();// 入力欄
        answerField.setBounds(20, 50, 100, 20);
        add(answerField);

        submitButton = new JButton("回答");// 回答ボタン
        submitButton.setBounds(130, 50, 80, 20);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });
        add(submitButton);

        restartButton = new JButton("リセット");
        restartButton.setBounds(20, 600, 200, 20);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartQuiz();
            }
        });
        add(restartButton);

        resultArea = new JTextArea();// 結果表示エリア
        resultArea.setBounds(20, 80, 250, 500);
        resultArea.setEditable(false);
        add(resultArea);

        count = 0;
        askQuestion();

        // 正解時のオーディオクリップの初期化
        try {
            File correctAudioFile = new File("クイズ正解3.wav");
            AudioInputStream correctAudioStream = AudioSystem.getAudioInputStream(correctAudioFile);
            correctClip = AudioSystem.getClip();
            correctClip.open(correctAudioStream);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {// 失敗したら
            ex.printStackTrace();
        }

        // 不正解時のオーディオクリップの初期化
        try {
            File wrongAudioFile = new File("クイズ不正解1.wav");
            AudioInputStream wrongAudioStream = AudioSystem.getAudioInputStream(wrongAudioFile);
            wrongClip = AudioSystem.getClip();
            wrongClip.open(wrongAudioStream);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {// 失敗したら
            ex.printStackTrace();
        }
    }

    private void playCorrectSound() {// 正解音を鳴らす
        if (correctClip != null) {
            correctClip.stop();
            correctClip.setFramePosition(0);
            correctClip.start();
        }
    }

    private void playWrongSound() {// 不正解音を鳴らす
        if (wrongClip != null) {
            wrongClip.stop();
            wrongClip.setFramePosition(0);
            wrongClip.start();
        }
    }

    private void askQuestion() {
        if (count < 10) {
            Random rand = new Random();
            int a = rand.nextInt(9) + 1;
            int b = rand.nextInt(9) + 1;
            questionLabel.setText(a + " X " + b + " = ?");
            answerField.setText("");// 入力欄リセット
        } else {// 10周したら
            questionLabel.setText("");
            answerField.setEnabled(false);// 入力欄無効化
            submitButton.setEnabled(false);// 回答ボタン無効化
            resultArea.append("お疲れさまでした。\n");
        }
    }

    private void checkAnswer() {
        String answerText = answerField.getText();
        try {
            int d = Integer.parseInt(answerText);
            if (d == getCorrectAnswer()) {
                resultArea.append("正解！\n");
                resultArea.append("\n");
                aClock.correctChange(); // 時計の色を変更
                playCorrectSound(); // 正解時に正解音声を再生
            } else {
                resultArea.append("不正解\n");
                resultArea.append("正解は" + getCorrectAnswer() + "です。\n");
                resultArea.append("\n");
                aClock.wrongChange(); // 時計の色を変更
                playWrongSound(); // 不正解時に不正解音声を再生
            }
            count++;
            askQuestion();
        } catch (NumberFormatException ex) {// 数字以外が入力されたら
            JOptionPane.showMessageDialog(this, "数字を入力してください。", "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCorrectAnswer() {
        String[] questionParts = questionLabel.getText().split(" ");
        int a = Integer.parseInt(questionParts[0]);
        int b = Integer.parseInt(questionParts[2]);
        return a * b;// 正答を計算
    }

    private void restartQuiz() {
        count = 0; // カウントをリセット
        askQuestion(); // 問題を再表示
        resultArea.setText(""); // 結果表示
        aClock.resetChange();
    }

    public static void main(String[] args) {
        Clock aClock = new Clock();
        aClock.perform();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main(aClock).setVisible(true); // Clockクラスのインスタンスを渡す
            }
        });
    }
}
