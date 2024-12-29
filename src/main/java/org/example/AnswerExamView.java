package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AnswerExamView {
    private String examName;
    private List<String> examQuestions;  // 假設每一題是一個 String

    public AnswerExamView(String examName, List<String> examQuestions) {
        this.examName = examName;
        this.examQuestions = examQuestions;
    }

    public void display() {
        // 創建一個新的 JFrame 顯示考試內容
        JFrame examFrame = new JFrame(examName); // 設定考試頁面的標題為考卷名稱
        examFrame.setSize(800, 600);
        examFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 設定關閉窗口時不退出程式

        // 顯示考卷內容（題目）
        JList<String> questionList = new JList<>(examQuestions.toArray(new String[0]));
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // 設置只選擇一題
        questionList.setVisibleRowCount(10);  // 顯示最多 10 題
        JScrollPane questionScrollPane = new JScrollPane(questionList);
        questionScrollPane.setPreferredSize(new Dimension(760, 400));

        examFrame.add(questionScrollPane, BorderLayout.CENTER);

        // 添加答題區域
        JPanel answerPanel = new JPanel();
        answerPanel.setLayout(new BorderLayout());

        // 用戶輸入答案的區域
        JTextArea answerArea = new JTextArea(5, 20);  // 用來輸入答案的區域
        JScrollPane answerScrollPane = new JScrollPane(answerArea);
        answerPanel.add(answerScrollPane, BorderLayout.CENTER);

        // 提交答案按鈕
        JButton submitButton = new JButton("繳交答案");
        submitButton.addActionListener(e -> {
            String answer = answerArea.getText().trim();
            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(examFrame, "請填寫答案後再提交！", "錯誤", JOptionPane.ERROR_MESSAGE);
            } else {
                // 模擬提交答案處理
                JOptionPane.showMessageDialog(examFrame, "答案已提交！", "成功", JOptionPane.INFORMATION_MESSAGE);
                examFrame.dispose(); // 提交後關閉答題界面
            }
        });

        // 繳交按鈕
        JPanel submitPanel = new JPanel();
        submitPanel.add(submitButton);
        answerPanel.add(submitPanel, BorderLayout.SOUTH);

        // 將答題區域加入 frame
        examFrame.add(answerPanel, BorderLayout.SOUTH);

        // 顯示界面
        examFrame.setVisible(true);
    }
}
