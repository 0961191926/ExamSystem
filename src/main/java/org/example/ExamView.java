package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ExamView {
    private JFrame frame;
    private ExamSystem system;
    private String currentUsername;
    private String userRole;
    private Map<String, String> examContentMap;

    public ExamView(ExamSystem system, String username, String role) {
        this.system = system;
        this.currentUsername = username;
        this.userRole = role;
        this.examContentMap = new HashMap<>();
        loadExamContents();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("考試系統");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        JButton profileButton = new JButton(currentUsername);
        profileButton.addActionListener(e -> showStatisticsUI());
        profileButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        profileButton.setPreferredSize(new Dimension(100, 50));

        JTextField searchBar = new JTextField("enter paper to search");
        searchBar.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 18));
        searchBar.setPreferredSize(new Dimension(300, 50));

        JButton searchButton = new JButton("查詢");
        searchButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        searchButton.setPreferredSize(new Dimension(100, 50));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchBar, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        searchButton.addActionListener(e -> {
            String searchQuery = searchBar.getText().trim();
            if (examContentMap.containsKey(searchQuery)) {
                showExamDetails(searchQuery);
            } else {
                JOptionPane.showMessageDialog(frame, "未找到考卷：" + searchQuery, "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });

        topBar.add(profileButton, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.EAST);

        // Center panel
        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        List<String> examNames = new ArrayList<>(examContentMap.keySet());
        for (String examName : examNames) {
            JButton examButton = new JButton(examName);
            examButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
            examButton.setPreferredSize(new Dimension(600, 50));
            examButton.addActionListener(e -> showExamDetails(examName));
            centerPanel.add(examButton);
        }
        JScrollPane centerScrollPane = new JScrollPane(centerPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        JButton createExamButton = new JButton("出題考卷");
        JButton uploadExamButton = new JButton("上傳考卷");
        configureButton(createExamButton, uploadExamButton);

        JButton showAllExamsButton = new JButton("顯示所有考卷");
        showAllExamsButton.addActionListener(e -> {
            String allExams = String.join("\n", examContentMap.keySet());
            JOptionPane.showMessageDialog(frame, "已載入的考卷名稱：\n" + allExams, "考卷列表", JOptionPane.INFORMATION_MESSAGE);
        });
        searchPanel.add(showAllExamsButton, BorderLayout.WEST);


        bottomPanel.add(createExamButton);
        bottomPanel.add(uploadExamButton);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(centerScrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadExamContents() {
        List<String> filePaths = Arrays.asList(
                "PY.txt",
                "PY2.txt"
        );

        for (String filePath : filePaths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder content = new StringBuilder();
                String examName = null;
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue; // 跳過空行

                    // 假設每份考卷的第一行是名稱，後續是內容
                    if (examName == null) {
                        examName = line; // 第一行作為考卷名稱
                    } else {
                        content.append(line).append("\n"); // 累積考卷內容
                    }
                }

                // 將最後一份考卷存入 Map
                if (examName != null && content.length() > 0) {
                    examContentMap.put(examName, content.toString().trim());
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
            }
        }
    }


    private void configureButton(JButton createExamButton, JButton uploadExamButton) {
        createExamButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        createExamButton.setPreferredSize(new Dimension(150, 50));

        uploadExamButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        uploadExamButton.setPreferredSize(new Dimension(150, 50));

        if ("student".equals(userRole)) {
            createExamButton.setEnabled(false);
            uploadExamButton.setEnabled(false);
        } else {
            createExamButton.addActionListener(e -> createExam());
            uploadExamButton.addActionListener(e -> uploadExam());
        }
    }

    private void showStatisticsUI() {
        if ("student".equals(this.userRole)) {
            showStudentStats();
        } else if ("teacher".equals(this.userRole)) {
            showTeacherStats();
        } else {
            JOptionPane.showMessageDialog(this.frame, "您沒有權限查看統計資料!", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStudentStats() {
        StatisticsView statsUI = new StatisticsView(this.system.getResultStatisticsView());
        statsUI.display();
    }

    private void showTeacherStats() {
        StatisticsView statsUI = new StatisticsView(this.system.getPaperStatisticsView(),
                this.system.getTeacherStatisticsView());
        statsUI.display();
    }

    private void showExamDetails(String examName) {
        String examContent = examContentMap.get(examName);
        if (examContent != null) {
            // 新增一個視窗顯示題目和作答區
            JFrame examFrame = new JFrame("考卷: " + examName);
            examFrame.setSize(800, 600);
            examFrame.setLayout(new BorderLayout());

            // 題目區域
            JTextArea questionArea = new JTextArea(examContent);
            questionArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 18));
            questionArea.setEditable(false);
            JScrollPane questionScrollPane = new JScrollPane(questionArea);

            // 作答區域
            JTextArea answerArea = new JTextArea();
            answerArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 18));
            JScrollPane answerScrollPane = new JScrollPane(answerArea);

            // 提交按鈕
            JButton submitButton = new JButton("提交答案");
            submitButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
            submitButton.addActionListener(e -> {
                String answer = answerArea.getText().trim();
                if (answer.isEmpty()) {
                    JOptionPane.showMessageDialog(examFrame, "請填寫答案後再提交！", "錯誤", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 將答案記錄或傳遞給後端系統處理
                    system.submitAnswer(currentUsername, examName, answer);
                    JOptionPane.showMessageDialog(examFrame, "答案已提交！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    examFrame.dispose(); // 提交後關閉窗口
                }
            });

            JPanel answerPanel = new JPanel(new BorderLayout());
            answerPanel.add(new JLabel("作答區:"), BorderLayout.NORTH);
            answerPanel.add(answerScrollPane, BorderLayout.CENTER);
            answerPanel.add(submitButton, BorderLayout.SOUTH);

            // 添加到窗口
            examFrame.add(questionScrollPane, BorderLayout.CENTER);
            examFrame.add(answerPanel, BorderLayout.SOUTH);
            examFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(frame, "找不到這份考卷的內容！", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void createExam() {
        QuestionGenerationView createExam = new QuestionGenerationView();
        createExam.display();
    }

    private void uploadExam() {
        UploadView uploadExam = new UploadView();
        uploadExam.upload();
    }

    public void display() {
        frame.setVisible(true);
    }
}
