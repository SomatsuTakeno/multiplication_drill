import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main extends JFrame {
    /**
     * @questionLabel
     *                問題文
     */
    private JLabel questionLabel;
    /**
     * @answerField
     *              回答欄
     */
    private JTextField answerField;
    /**
     * @submitButton
     *               回答ボタン
     */
    private JButton submitButton;
    /**
     * @resetButton
     *              リセットボタン
     */
    private JButton restartButton;
    /**
     * @resultArea
     *             結果表示エリア
     */
    private JTextArea resultArea;
    /**
     * @count
     *        10周したら終了するカウンタ
     */
    private int count;
    /**
     * @score
     *        点数
     */
    private int score = 0;
    private Clock aClock; // Clockクラスのインスタンスを保持

    // 正解時と不正解時のオーディオクリップを格納するフィールド
    /**
     * @correctClip
     *              正解時の音を入れる箱
     */
    private Clip correctClip;
    /**
     * @wrongClip
     *            不正解時の音
     */
    private Clip wrongClip;

    public Main(Clock clock) {
        super("九九ドリル");// ウィンドウ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 1000); // ウィンドウサイズを調整
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
        restartButton.setBounds(20, 900, 200, 20);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartQuiz();// リセットボタンが押されたらrestartQuiz()を実行
            }
        });
        add(restartButton);

        resultArea = new JTextArea();// 結果表示エリア
        resultArea.setBounds(20, 80, 250, 800);
        resultArea.setEditable(false);
        add(resultArea);

        count = 0;
        resultArea.append("効果音素材　効果音ラボ様\n");
        resultArea.append("\n");
        resultArea.append("正解すると時計が赤色に、\n");
        resultArea.append("間違えると時計が青色になります。\n");
        resultArea.append("\n");
        askQuestion();

        // 正解時のオーディオクリップの初期化
        try {// wavファイルを代入してみる
            File correctAudioFile = new File("クイズ正解3.wav");
            AudioInputStream correctAudioStream = AudioSystem.getAudioInputStream(correctAudioFile);
            correctClip = AudioSystem.getClip();
            correctClip.open(correctAudioStream);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {// 失敗したらエラー
            ex.printStackTrace();
        }

        // 不正解時のオーディオクリップの初期化
        try {// wavファイルを代入してみる
            File wrongAudioFile = new File("クイズ不正解1.wav");
            AudioInputStream wrongAudioStream = AudioSystem.getAudioInputStream(wrongAudioFile);
            wrongClip = AudioSystem.getClip();
            wrongClip.open(wrongAudioStream);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {// 失敗したらエラー
            ex.printStackTrace();
        }
    }

    private void playCorrectSound() {// 正解音を鳴らす
        if (correctClip != null) {// クイズ正解3.wavが代入できていれば
            correctClip.stop();
            correctClip.setFramePosition(0);// 初めから
            correctClip.start();// 再生
        }
    }

    private void playWrongSound() {// 不正解音を鳴らす
        if (wrongClip != null) {// クイズ不正解1.wavが代入できていれば
            wrongClip.stop();
            wrongClip.setFramePosition(0);// 初めから
            wrongClip.start();// 再生
        }
    }

    private void askQuestion() {
        if (count < 10) {
            Random rand = new Random();
            int a = rand.nextInt(9) + 1;
            int b = rand.nextInt(9) + 1;
            questionLabel.setText(a + " X " + b + " = ?");// 問題文を表示
            answerField.setText("");// 回答欄をリセット
        } else {// 10周したら
            questionLabel.setText("");
            answerField.setEnabled(false);// 回答欄を無効にする
            submitButton.setEnabled(false);// 回答ボタンを無効にする
            resultArea.append("あなたの点数は" + score + "点です。\n");
        }
    }

    private void checkAnswer() {
        String answerText = answerField.getText();
        try {
            int d = Integer.parseInt(answerText);
            if (d == getCorrectAnswer()) {
                score += 10;
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
        count = 0; // カウンタをリセット
        askQuestion(); // 問題を再表示
        resultArea.setText(""); // 結果表示エリアリセット
        answerField.setEnabled(true); // 回答欄を有効にする
        submitButton.setEnabled(true); // 回答ボタンを有効にする
        resultArea.append("効果音素材　効果音ラボ様\n");
        resultArea.append("\n");
        resultArea.append("正解すると時計が赤色に、\n");
        resultArea.append("間違えると時計が青色になります。\n");
        resultArea.append("\n");
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
