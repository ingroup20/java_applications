package com.t1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimerApp {
    private JFrame frame;
    private JTextField timeInputField_h;
    private JTextField timeInputField_m;
    private JTextField timeInputField_s;
    private JLabel countdownLabel;
    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;

    private Timer timer;
    private int remainingSeconds;
    private boolean isBlinking;

    public CountdownTimerApp() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("倒數計時器");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        // 時間輸入欄位
        JPanel inputPanel = new JPanel();
        timeInputField_h = new JTextField("00",2);
        timeInputField_m = new JTextField("00",2);
        timeInputField_s = new JTextField("00",2);
        inputPanel.add(new JLabel("輸入時間 (時:分:秒):"));
        inputPanel.add(timeInputField_h);
        inputPanel.add(new JLabel(":"));
        inputPanel.add(timeInputField_m);
        inputPanel.add(new JLabel(":"));
        inputPanel.add(timeInputField_s);

        // 倒數顯示標籤
        countdownLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 32));
        countdownLabel.setOpaque(true);
        countdownLabel.setBackground(Color.WHITE);

        // 按鈕
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("開始");
        stopButton = new JButton("停止");
        resetButton = new JButton("重置");
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);

        frame.add(inputPanel);
        frame.add(countdownLabel);
        frame.add(buttonPanel);

        // 初始化按鈕事件
        initListeners();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initListeners() {
        startButton.addActionListener(e -> startCountdown());
        stopButton.addActionListener(e -> stopCountdown());
        resetButton.addActionListener(e -> resetCountdown());
    }

    private void startCountdown() {
        try {
            remainingSeconds = Integer.parseInt(timeInputField_h.getText())*60*60+
                    Integer.parseInt(timeInputField_m.getText())*60+
                    Integer.parseInt(timeInputField_s.getText());
            if (remainingSeconds <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "請輸入有效的正整數時間！");
            return;
        }

        stopCountdown(); // 防止多次啟動 Timer
        isBlinking = false;
        countdownLabel.setForeground(Color.BLACK);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingSeconds <= 0) {
                    endCountdown();
                } else {
                    remainingSeconds--;
                    updateCountdownLabel();
                }
            }
        }, 0, 1000);
    }

    private void stopCountdown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void resetCountdown() {
        stopCountdown();
        isBlinking = false;
        countdownLabel.setForeground(Color.BLACK);
        countdownLabel.setText("00:00:00");
        frame.toFront(); // 還原視窗狀態
    }

    private void updateCountdownLabel() {
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;

        countdownLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void endCountdown() {
        stopCountdown();
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setAlwaysOnTop(false);

        // 閃爍紅色文字
        isBlinking = true;
        Timer blinkTimer = new Timer();
        blinkTimer.scheduleAtFixedRate(new TimerTask() {
            private boolean isRed = false;

            @Override
            public void run() {
                if (!isBlinking) {
                    blinkTimer.cancel();
                    return;
                }

                countdownLabel.setForeground(isRed ? Color.BLACK : Color.RED);
                isRed = !isRed;
            }
        }, 0, 500);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CountdownTimerApp::new);
    }
}
