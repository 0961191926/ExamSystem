package org.example;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ExamSystem {
    private UserManager userManager;
    private ExamManager examManager;
    private LoginView loginUI;
    private ExamView mainSystemUI;
    private Map<String, Map<String, String>> studentAnswers = new HashMap<>(); // 儲存學生的答案

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExamSystem());
    }

    public ExamSystem() {
        this.userManager = new UserManager();
        this.examManager = new ExamManager();
        this.loginUI = new LoginView(this);
        showLoginUI();
    }

    public void showLoginUI() {
        loginUI.display();
    }

    public void showMainSystem(String username, String role) {
        mainSystemUI = new ExamView(this, username, role);
        mainSystemUI.display();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ExamManager getExamManager() {
        return examManager;
    }

    public PaperStatisticsView getPaperStatisticsView() {
        return new PaperStatisticsView();
    }

    public TeacherStatisticsView getTeacherStatisticsView() {
        return new TeacherStatisticsView();
    }

    public ResultStatisticsView getResultStatisticsView() {
        return new ResultStatisticsView();
    }

    // 提交答案的方法
    public void submitAnswer(String username, String examName, String answer) {
        // 確保學生的答案資料夾存在
        studentAnswers.putIfAbsent(username, new HashMap<>());

        // 把學生的答案記錄下來
        studentAnswers.get(username).put(examName, answer);

        // 這裡可以加入更多處理邏輯，例如計分等
        System.out.println("學生 " + username + " 在考卷 " + examName + " 中提交了答案：" + answer);
    }


}
