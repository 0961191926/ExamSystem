package org.example.VIew;

import org.example.Controller.ExamController;
import org.example.Controller.MultipartUploader;
import org.example.ExamSystem;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UploadView {
    private ExamController examController;
    private ExamSystem system;

    public UploadView(ExamSystem system) {
        this.system = system;
        this.examController = system.getExamController();
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
            data[i][0] = "Q" + (i + 1) ;
            data[i][1] = 0; // 默认分数
            data[i][2] = ""; // 默认答案为空
        }

        JTable table = new JTable(data, columnNames); // 表格 GUI
        JScrollPane scrollPane = new JScrollPane(table);

        JButton saveButton = new JButton("保存設置");
        saveButton.addActionListener(e -> {
            List<String> settings = new ArrayList<>();
            List<String> answers = new ArrayList<>();
            int totalScore = 0;
            try {
                StringBuilder formattedResult = new StringBuilder();
                formattedResult.append("ExamTitle: upload\n\n");
                settings.add("ExamTitle: upload");
                settings.add("Public/Private?: Public");
                settings.add("TotalScore: null");
                settings.add("Number of questions: " + questionCount);
                settings.add("\nArrangement:\n");
                answers.add("ExamTitle: upload");
                System.out.println("行數:" + table.getRowCount());
                for (int i = 0; i < table.getRowCount(); i++) {
                    String question = (String) table.getValueAt(i, 0); // 获取“题目”
                    String score=(String)table.getValueAt(i,1);
                    String answer = (String) table.getValueAt(i, 2); // 获取“答案”




                    // 添加到设置格式（不包含答案）
                    settings.add("Question: " + question + "\nScore: " + score);

                    // 将答案单独存入答案列表
                    answers.add("Question: " + question);
                    answers.add("Answer: " + answer);
                }


                // 构建最终结果字符串

                StringBuilder formattedResult2 = new StringBuilder();

                formattedResult2.append("Title: \n");
                formattedResult2.append("Public/Private?: Public\n");
                formattedResult2.append("TotalScore: ").append(totalScore).append("\n\n");
                formattedResult2.append("Number of questions: ").append(table.getRowCount()).append("\n\n");
                formattedResult2.append("Arrangement:\n");

                for (String setting : settings) {
                    formattedResult2.append(setting).append("\n");
                }
                // 自動生成文件名 (基於上傳檔案的名稱生成後綴)
                String originalFileName = uploadedFile.getName();
                String baseFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.')); // 去掉檔名的副檔名

                // 生成自訂文件名
                String answerFileName = baseFileName + "_answer.txt";
                String settingFileName = baseFileName + "_setting.txt";


                // 保存到文件
                saveAnswersToFile(answers, answerFileName);
                saveSettingsToFile(settings, settingFileName);
                saveUploadInfoToFile(uploadedFile.getName(), settingFileName, answerFileName);
                uploadAllFiles(
                        uploadedFile,                 // 考卷文件
                        answerFileName,               // Answer 文件
                        settingFileName,              // Settings 文件
                        "http://localhost:8080/upload" // 伺服器地址（可替換）
                );

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
    private void uploadAllFiles(File examFile, String answerFilePath, String settingFilePath, String serverUrl) {
        try {
            // 验证文件是否存在
            if (!examFile.exists()) {
                throw new IOException("Exam file does not exist: " + examFile.getAbsolutePath());
            }
            if (!Files.exists(Paths.get(answerFilePath))) {
                throw new IOException("Answer file does not exist: " + answerFilePath);
            }
            if (!Files.exists(Paths.get(settingFilePath))) {
                throw new IOException("Setting file does not exist: " + settingFilePath);
            }

            // 提取 dirName（从文件名去掉扩展名）
            String dirName = examFile.getName().substring(0, examFile.getName().lastIndexOf('.'));
            System.out.println("dirName: " + dirName);

            // 使用 MultipartUploader
            MultipartUploader uploader = new MultipartUploader();
            uploader.setDirName(dirName) // 设置 dirName
                    .addFile("examFile", examFile.toPath()) // 添加考卷文件
                    .addFile("answerFile", Paths.get(answerFilePath)) // 添加答案文件
                    .addFile("settingFile", Paths.get(settingFilePath)); // 添加设置文件

            // 打印上传目标 URL
            System.out.println("Server URL: " + serverUrl);

            // 执行文件上传
            String response = uploader.upload(serverUrl);

            // 显示提示
            JOptionPane.showMessageDialog(null, response, "文件上传成功", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "文件上传失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // 打印完整异常详情
        }
    }
}