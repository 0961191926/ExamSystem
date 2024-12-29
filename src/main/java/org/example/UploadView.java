package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UploadView {
    private ExamController examController;

    public UploadView() {
        this.examController = new ExamController();
    }

    public void upload() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(null, "考卷已成功上傳: " + selectedFile.getName());

            // 提示用户选择手动设置还是上传答案文件
            int userChoice = JOptionPane.showOptionDialog(
                    null,
                    "您想要如何設置答案？",
                    "選擇設置答案方式",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Object[]{"手動設置", "上傳答案文件"},
                    "手動設置"
            );

            if (userChoice == 0) {
                // 手动设置答案与分数
                String questionCountStr = JOptionPane.showInputDialog(null, "請輸入題目數量:", "題目數量", JOptionPane.QUESTION_MESSAGE);
                if (questionCountStr != null && !questionCountStr.isEmpty()) {
                    try {
                        int questionCount = Integer.parseInt(questionCountStr);
                        createScoreSettingFrame(questionCount, selectedFile);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "請輸入有效的數字!", "錯誤", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else if (userChoice == 1) {
                // 上传答案文件
                uploadAnswerFile(selectedFile);
            }
        }
    }

    private void uploadAnswerFile(File examFile) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File answerFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(null, "答案已成功上傳: " + answerFile.getName());

            try {
                // 提取答案
                List<String> answers = examController.extractAnswersFromFile(answerFile);
                List<String> settings = new ArrayList<>();

                // 默认分数设定
                for (int i = 1; i <= answers.size(); i++) {
                    settings.add("第" + i + "題,分數10");
                }

                // 保存到文件
                saveAnswersToFile(answers, "answer.txt");
                saveSettingsToFile(settings, "setting.txt");
                saveUploadInfoToFile(examFile.getName(), "setting.txt", "answer.txt");

                JOptionPane.showMessageDialog(null, "答案文件解析成功! 所有資料已保存。");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "解析答案文件失敗: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void createScoreSettingFrame(int questionCount, File uploadedFile) {
        JFrame frame = new JFrame("題目分數與答案設置");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"題目", "分數", "答案"};
        Object[][] data = new Object[questionCount][3];
        for (int i = 0; i < questionCount; i++) {
            data[i][0] = "第 " + (i + 1) + " 題";
            data[i][1] = 0; // 默认分数
            data[i][2] = ""; // 默认答案为空
        }

        JTable table = new JTable(data, columnNames); // 表格 GUI
        JScrollPane scrollPane = new JScrollPane(table);

        JButton saveButton = new JButton("保存設置");
        saveButton.addActionListener(e -> {
            List<String> settings = new ArrayList<>();
            List<String> answers = new ArrayList<>();

            try {
                for (int i = 0; i < questionCount; i++) {
                    String questionNumber = table.getValueAt(i, 0).toString();
                    int score = Integer.parseInt(table.getValueAt(i, 1).toString());
                    String answer = table.getValueAt(i, 2).toString();

                    settings.add(questionNumber + ",分數" + score);
                    answers.add(questionNumber + ",答案" + answer);
                }

                // 保存到文件
                saveAnswersToFile(answers, "answer.txt");
                saveSettingsToFile(settings, "setting.txt");
                saveUploadInfoToFile(uploadedFile.getName(), "setting.txt", "answer.txt");

                JOptionPane.showMessageDialog(frame, "設置已保存! 所有資料已保存至文件。", "成功", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "保存設置失敗: " + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(saveButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void saveAnswersToFile(List<String> answers, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String answer : answers) {
                writer.println(answer);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "保存答案資料失敗: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveSettingsToFile(List<String> settings, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String setting : settings) {
                writer.println(setting);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "保存設定資料失敗: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveUploadInfoToFile(String examFileName, String settingFileName, String answerFileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("upload.txt", true))) {
            writer.println("考卷文件: " + examFileName);
            writer.println("答案文件: " + answerFileName);
            writer.println("設置文件: " + settingFileName);
            writer.println("上傳時間: " + LocalDateTime.now());
            writer.println("--------------------------");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "保存上傳資訊失敗: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}