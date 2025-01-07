package org.example.VIew;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaperSettingView {
    private JFrame frame;
    private List<String> questions;
    private Map<String, JSpinner> questionSpinners;
    private Map<String, Integer> questionScores;
    private JSpinner totalScoreSpinner;
    private boolean isSettingsSaved = false; // Add this flag

    // Add getter for the flag
    public boolean isSettingsSaved() {
        return isSettingsSaved;
    }

    public PaperSettingView(List<String> questions) {
        this.questions = questions;
        this.questionSpinners = new HashMap<>();
        this.questionScores = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("題目分數設定");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2));

        // 初始化總分設定
        JLabel totalScoreLabel = new JLabel("總分設定: ");
        totalScoreSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 1000, 1));

        // 初始化每題分數設定
        for (String question : questions) {
            JLabel questionLabel = new JLabel("題目: " + question);
            questionLabel.setHorizontalAlignment(SwingConstants.LEFT);

            // 創建分數調整的 JSpinner
            JSpinner scoreSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1)); // 預設每題分數為 5
            questionSpinners.put(question, scoreSpinner);
            questionScores.put(question, 5);

            // 添加 ChangeListener 更新 Map 的值
            scoreSpinner.addChangeListener(e -> questionScores.put(question, (Integer) scoreSpinner.getValue()));

            panel.add(questionLabel);
            panel.add(scoreSpinner);
        }

        // 儲存設定的按鈕
        JButton saveButton = new JButton("儲存設定");
        saveButton.addActionListener(e -> {
            for (Map.Entry<String, JSpinner> entry : questionSpinners.entrySet()) {
                questionScores.put(entry.getKey(), (Integer) entry.getValue().getValue());
            }

            JOptionPane.showMessageDialog(frame,
                    "設定已儲存！\n總分: " + totalScoreSpinner.getValue() +
                            "\n題目分數: " + questionScores.toString());

            isSettingsSaved = true; // Set the flag to true when settings are saved
            frame.dispose();
        });

        // 界面布局
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(totalScoreLabel);
        topPanel.add(totalScoreSpinner);

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(panel), BorderLayout.CENTER);
        frame.add(saveButton, BorderLayout.SOUTH);
    }

    public Map<String, Integer> getQuestionScores() {
        for (Map.Entry<String, JSpinner> entry : questionSpinners.entrySet()) {
            questionScores.put(entry.getKey(), (Integer) entry.getValue().getValue());
        }
        return questionScores;
    }

    public int getTotalScore() {
        return (Integer) totalScoreSpinner.getValue();
    }

    public void display() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
