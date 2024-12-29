package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ExamView {
    private JFrame frame;
    private ExamSystem system;
    private String currentUsername;
    private String userRole;
    private final Map<String, File> examContentMap;

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
                File examFile = examContentMap.get(searchQuery);
                showExamDetails(searchQuery, examFile); // 傳遞文件和名稱
            } else {
                JOptionPane.showMessageDialog(frame, "未找到考卷：" + searchQuery, "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });

        topBar.add(profileButton, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.EAST);

        // Center panel
        JScrollPane centerScrollPane = createCenterPanel();

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
    private JScrollPane createCenterPanel() {
        // 使用 GridLayout 動態顯示所有考試按鈕（按列排列）
        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 動態按鈕布局

        // 遍歷考卷數據 Map (examContentMap)，生成按鈕
        for (Map.Entry<String, File> entry : examContentMap.entrySet()) {
            String examName = entry.getKey();  // 考卷名稱
            File examFile = entry.getValue(); // 對應考卷文件

            // 創建代表此考卷的按鈕
            JButton examButton = new JButton(examName);
            examButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
            examButton.setPreferredSize(new Dimension(600, 50));

            // 設置按鈕點擊事件，打開考卷內容界面
            examButton.addActionListener(e -> showExamDetails(examName, examFile)); // 傳入文件和名稱
            centerPanel.add(examButton); // 將按鈕添加到中心面板中
        }

        // 將創建的按鈕面板放置在滾動窗口
        return new JScrollPane(centerPanel);
    }

    private void loadExamContents() {
        // 定義考卷文件名稱列表
        List<String> filePaths = Arrays.asList("PY.txt", "Py2.txt", "中興109.docx");

        for (String filePath : filePaths) {
            try {
                File file = new File(getClass().getClassLoader().getResource(filePath).toURI());
                examContentMap.put(file.getName(), file); // 儲存文件名和對應文件
                System.out.println("Loaded exam: " + file.getName());
            } catch (Exception e) {
                System.err.println("無法加載考卷文件: " + filePath + " - " + e.getMessage());
            }
        }

        // 測試打印考卷列表
        examContentMap.forEach((name, content) -> System.out.println("Exam Loaded: " + name));
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

    private void showExamDetails(String examName, File examFile) {
        try {
            // 使用 ExamController 提取文件內容
            ExamController examController = new ExamController();
            String examContent = examController.extractContentFromFile(examFile);

            // 創建 UI 展示題目和答案區域
            JFrame examFrame = new JFrame("考試: " + examName);
            examFrame.setSize(800, 600);
            examFrame.setLayout(new BorderLayout());

            JTextArea questionArea = new JTextArea(examContent);
            questionArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));
            questionArea.setEditable(false);

            JScrollPane questionScrollPane = new JScrollPane(questionArea);

            JTextArea answerArea = new JTextArea();
            answerArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));

            JButton submitButton = new JButton("提交答案");
            submitButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
            submitButton.addActionListener(e -> {
                String answer = answerArea.getText().trim();
                if (!answer.isEmpty()) {
                    system.submitAnswer(currentUsername, examName, answer);
                    JOptionPane.showMessageDialog(examFrame, "答案已提交！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    examFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(examFrame, "請填寫答案後再提交！", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel answerPanel = new JPanel(new BorderLayout());
            answerPanel.add(new JLabel("作答區:"), BorderLayout.NORTH);
            answerPanel.add(new JScrollPane(answerArea), BorderLayout.CENTER);
            answerPanel.add(submitButton, BorderLayout.SOUTH);

            examFrame.add(questionScrollPane, BorderLayout.CENTER);
            examFrame.add(answerPanel, BorderLayout.SOUTH);
            examFrame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "打開考卷失敗：" + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
