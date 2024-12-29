package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 手動出題UI
public class QuestionGenerationView {

    private final List<String> questions = new ArrayList<>();
    private final Map<String, String> answers = new HashMap<>();

    private JTextArea questionListArea; // To store and display questions and answers
    private JTextField examTitleField;  // To input the exam title

    public void display() {
        JFrame createExamFrame = new JFrame("手動出題");
        createExamFrame.setSize(800, 600);
        createExamFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createExamFrame.setLayout(new BorderLayout());

        // 題型選擇面板
        JPanel selectionPanel = new JPanel(new BorderLayout());

        // 左側的考卷題目輸入區域
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(new JLabel("考卷題目:"));
        examTitleField = new JTextField(20);
        leftPanel.add(examTitleField);
        selectionPanel.add(leftPanel, BorderLayout.WEST);

        // 右側的下拉選單和按鈕
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(new JLabel("選擇題型:"));
        JComboBox<String> questionTypeCombo = new JComboBox<>(
                new String[]{"True/False", "Multiple Choice", "Short Answer"});
        JButton selectButton = new JButton("創建題目面板");
        rightPanel.add(questionTypeCombo);
        rightPanel.add(selectButton);
        selectionPanel.add(rightPanel, BorderLayout.EAST);

        createExamFrame.add(selectionPanel, BorderLayout.NORTH);

        // 題目面板
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new GridBagLayout());
        createExamFrame.add(questionPanel, BorderLayout.CENTER);

        // 顯示問題列表的區域
        questionListArea = new JTextArea();
        questionListArea.setEditable(false);
        questionListArea.setLineWrap(true);
        questionListArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(questionListArea);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        // 使用分割窗格
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, questionPanel, scrollPane);
        splitPane.setDividerLocation(300);
        createExamFrame.add(splitPane, BorderLayout.CENTER);

        // 儲存按鈕
        JButton saveButton = new JButton("儲存考卷");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        createExamFrame.add(buttonPanel, BorderLayout.SOUTH);

        selectButton.addActionListener(e -> {
            String questionType = (String) questionTypeCombo.getSelectedItem();
            createQuestionPanel(questionType, questionPanel);
        });

        saveButton.addActionListener(e -> {
            String examTitle = examTitleField.getText().trim();
            if (!examTitle.isEmpty()) {
                saveExamToFile(examTitle); // 儲存考卷到檔案
                new PaperSettingView(questions).display(); // 顯示分數設定視窗
                createExamFrame.dispose(); // 關閉 CreateExam 視窗
                clearExamData(); // 清除問題與答案
            } else {
                JOptionPane.showMessageDialog(createExamFrame, "請輸入考卷名稱！", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });

        createExamFrame.setVisible(true);
    }

    private void createQuestionPanel(String questionType, JPanel questionPanel) {
        questionPanel.removeAll();
        questionPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 問題輸入欄位
        gbc.gridx = 0;
        gbc.gridy = 0;
        questionPanel.add(new JLabel("問題:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField questionField = new JTextField(20);
        questionPanel.add(questionField, gbc);

        // 根據題型動態生成不同的面板
        switch (questionType) {
            case "True/False":
                addTrueFalseQuestionPanel(questionPanel, gbc, questionField);
                break;
            case "Multiple Choice":
                addMultipleChoiceQuestionPanel(questionPanel, gbc, questionField);
                break;
            case "Short Answer":
                addShortAnswerQuestionPanel(questionPanel, gbc, questionField);
                break;
        }

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    private void addTrueFalseQuestionPanel(JPanel panel, GridBagConstraints gbc, JTextField questionField) {
        ButtonGroup tfGroup = new ButtonGroup();
        JRadioButton trueButton = new JRadioButton("True");
        JRadioButton falseButton = new JRadioButton("False");
        tfGroup.add(trueButton);
        tfGroup.add(falseButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("答案:"), gbc);

        gbc.gridx = 1;
        JPanel tfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfPanel.add(trueButton);
        tfPanel.add(falseButton);
        panel.add(tfPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("新增題目");
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            String answer = trueButton.isSelected() ? "True" : falseButton.isSelected() ? "False" : "";
            if (!questionField.getText().trim().isEmpty() && !answer.isEmpty()) {
                String question = questionField.getText().trim();
                questionListArea.append("問題: " + question + " 答案: " + answer + "\n");
                questions.add(question);
                answers.put(question, answer);
                questionField.setText("");
                tfGroup.clearSelection();
            } else {
                JOptionPane.showMessageDialog(panel, "請填寫問題和選擇答案。", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addMultipleChoiceQuestionPanel(JPanel panel, GridBagConstraints gbc, JTextField questionField) {
        // Create a list to store option fields and checkboxes
        List<JTextField> optionFields = new ArrayList<>();
        List<JCheckBox> answerCheckBoxes = new ArrayList<>();
        String[] optionLabels = {"A", "B", "C", "D"};

        // Initialize the first set of options
        for (int i = 0; i < 4; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1; // Starting from 1 to avoid overlap with the question label
            panel.add(new JLabel("選項 " + optionLabels[i] + ":"), gbc);

            gbc.gridx = 1;
            JTextField optionField = new JTextField(20);
            optionFields.add(optionField);
            panel.add(optionField, gbc);

            // Add a checkbox for selecting the answer
            gbc.gridx = 2;
            JCheckBox answerCheckBox = new JCheckBox();
            answerCheckBoxes.add(answerCheckBox);
            panel.add(answerCheckBox, gbc);
        }

        // Button to dynamically add more options
        JButton addOptionButton = new JButton("新增選項");
        gbc.gridx = 1;
        gbc.gridy = 5 + optionFields.size(); // Position after the initial 4 options
        panel.add(addOptionButton, gbc);

        // ActionListener to add new option
        addOptionButton.addActionListener(e -> {
            // 新增選項欄位
            JTextField newOptionField = new JTextField(20);
            optionFields.add(newOptionField);

            // 新增答案選擇框
            JCheckBox newAnswerCheckBox = new JCheckBox();
            answerCheckBoxes.add(newAnswerCheckBox);

            // 更新位置
            gbc.gridx = 0;
            gbc.gridy = optionFields.size() + 1; // 根據選項數量來調整位置
            panel.add(new JLabel("選項 " + (char) ('A' + optionFields.size() - 1) + ":"), gbc);

            gbc.gridx = 1;
            panel.add(newOptionField, gbc);

            gbc.gridx = 2;
            panel.add(newAnswerCheckBox, gbc);

            // 重新排列布局
            panel.revalidate();
            panel.repaint();
        });

        // Button to add multiple choice question to the list
        JButton addButton = new JButton("新增題目");
        gbc.gridx = 1;
        gbc.gridy = 6 + optionFields.size(); // Position after options and add button
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            StringBuilder selectedAnswers = new StringBuilder();
            for (int i = 0; i < answerCheckBoxes.size(); i++) {
                if (answerCheckBoxes.get(i).isSelected()) {
                    selectedAnswers.append(optionLabels[i]).append(" ");
                }
            }

            // 確保填寫了問題並選擇了答案
            if (!questionField.getText().trim().isEmpty() && selectedAnswers.length() > 0) {
                String question = questionField.getText().trim();
                questionListArea.append("問題: " + question + " 答案: " + selectedAnswers.toString().trim() + "\n");
                questions.add(question);
                answers.put(question, selectedAnswers.toString().trim());
                questionField.setText(""); // 清空問題欄位
                for (JCheckBox checkBox : answerCheckBoxes) {
                    checkBox.setSelected(false); // 清空所有選擇框
                }
            } else {
                JOptionPane.showMessageDialog(panel, "請填寫問題和選擇答案。", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void addShortAnswerQuestionPanel(JPanel panel, GridBagConstraints gbc, JTextField questionField) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("答案:"), gbc);

        gbc.gridx = 1;
        JTextField answerField = new JTextField(20);
        panel.add(answerField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("新增題目");
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            if (!questionField.getText().trim().isEmpty() && !answerField.getText().trim().isEmpty()) {
                String question = questionField.getText().trim();
                String answer = answerField.getText().trim();
                questionListArea.append("問題: " + questionField.getText().trim() + " 答案: " + answerField.getText().trim() + "\n");
                questions.add(question); // 將題目加入清單
                answers.put(question, answer); // 將答案加入清單
                questionField.setText(""); // 清空問題輸入框
                answerField.setText(""); // 清空答案輸入框
            } else {
                JOptionPane.showMessageDialog(panel, "請填寫問題和答案。", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void saveExamToFile(String examTitle) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(examTitle + ".txt"))) {
            writer.write("考卷標題: " + examTitle + "\n\n");
            for (String question : questions) {
                writer.write("問題: " + question + "\n");
                writer.write("答案: " + answers.get(question) + "\n\n");
            }
            JOptionPane.showMessageDialog(null, "考卷已儲存！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "儲存考卷時出錯！", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearExamData() {
        questions.clear();
        answers.clear();
        questionListArea.setText("");
    }
}
