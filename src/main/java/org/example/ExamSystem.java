package org.example;

import org.example.Communicator.ClientServerCommunicator;
import org.example.Controller.AccountController;
import org.example.Controller.ExamController;
import org.example.Controller.MultipartUploader;
import org.example.VIew.*;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamSystem {
    private AccountController accountController;
    private ExamController examController;
    private LoginView loginUI;
    private ExamView mainSystemUI;
    private Map<String, Map<String, List<String>>> studentAnswers = new HashMap<>(); // 儲存學生的答案 // 儲存學生的答案

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamSystem());
    }

    public ExamSystem() {
        String serverUrl = "http://localhost:8080"; // 服务器的URL
        ClientServerCommunicator communicator = new ClientServerCommunicator(serverUrl);
        this.accountController = new AccountController(communicator);
        this.examController= new ExamController(communicator);
        this.loginUI = new LoginView(this.accountController);
        showLoginUI();
    }

    public void showLoginUI() {
        loginUI.display();
    }

    public void showMainSystem(String username, String role) {
        loginUI.dismiss();
        mainSystemUI = new ExamView(this, username, role);
        mainSystemUI.display();
    }

    public AccountController getAccountController() {
        return accountController;
    }
    public ExamController getExamController() {
        return examController;
    }



    public PaperStatisticsView getPaperStatisticsView() {
        return new PaperStatisticsView();
    }

    public TeacherStatisticsView getTeacherStatisticsView() {
        return new TeacherStatisticsView();
    }

    public ResultStatisticsView getResultStatisticsView() {
        return new ResultStatisticsView(null);
    }

    // 提交答案的方法

    public void submitAnswer(String username, String examName, String answer) {
        // 确保学生的答案数据结构存在
        studentAnswers.putIfAbsent(username, new HashMap<>());

        // 获取当前考试的答案列表
        Map<String, List<String>> userExams = studentAnswers.get(username);
        List<String> examAnswers = userExams.computeIfAbsent(examName, k -> new ArrayList<>());

        // 记录答案
        examAnswers.add(answer);

        // 格式化答案
        StringBuilder formattedAnswer = new StringBuilder();
        formattedAnswer.append("Student: ").append(username);
        formattedAnswer.append(", Exam: ").append(examName).append("\n");
        examAnswers.forEach(ans -> formattedAnswer.append(ans).append("\n"));

        // 如果答案包含 "//end"，保存到文件并清除缓存，同时通过 MultipartUploader 上传
        if (answer.trim().equalsIgnoreCase("//end")) {
            try {
                String cleanedExamName = examName.contains(".")
                        ? examName.substring(0, examName.lastIndexOf('.'))
                        : examName;
                // 文件名称为 "username_examName.txt"
                String fileName = username + "_" + cleanedExamName + ".txt";
                File file = new File(fileName);

                // 写入文件
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(formattedAnswer.toString());
                }

                // 打印保存成功信息
                System.out.println("答案已保存至文件: " + file.getAbsolutePath());

                // 使用 MultipartUploader 上传文件
                MultipartUploader uploader = new MultipartUploader();
                uploader.setDirName(cleanedExamName); // 设置 dirName 为 examName
                uploader.addFile("file", file.toPath()); // 添加文件
                String serverUrl = "http://localhost:8080/upload"; // 替换为实际的上传接口地址
                String response = uploader.upload(serverUrl);

                // 打印上传成功信息
                System.out.println("文件上传成功，服务器响应: " + response);

                // 清除当前考试的答案缓存
                userExams.remove(examName);
                examController.postExam(fileName);
            } catch (IOException | InterruptedException e) {
                System.err.println("保存或上传答案时发生错误: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            // 打印当前的答案内容
            System.out.println(formattedAnswer.toString());
        }
    }





}
