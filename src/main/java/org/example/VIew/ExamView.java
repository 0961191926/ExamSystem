package org.example.VIew;

import org.example.Controller.ExamController;
import org.example.ExamSystem;
import org.example.VIew.ResultStatisticsView;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private final List<String>examContentMap;

    public ExamView(ExamSystem system, String username, String role) {
        this.system = system;
        this.currentUsername = username;
        this.userRole = role;

        examContentMap=system.getExamController().loadExamNames();
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

        JTextField searchBar = new JTextField("");
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

            if (searchQuery.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "搜索內容不能為空！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int foundIndex = -1;
            for (int i = 0; i < examContentMap.size(); i++) {
                if (examContentMap.get(i).equalsIgnoreCase(searchQuery)) {
                    foundIndex = i;
                    break;
                }
            }

            if (foundIndex != -1) {
                String foundExam = examContentMap.remove(foundIndex);
                examContentMap.add(0, foundExam);
                refreshExamButtons();
            } else {
                JOptionPane.showMessageDialog(frame, "未找到與 \"" + searchQuery + "\" 匹配的考卷。", "錯誤", JOptionPane.ERROR_MESSAGE);
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
            // 重新初始化 examContentMap
            examContentMap.clear();
            examContentMap.addAll(system.getExamController().loadExamNames());

            // 更新中央按鈕區域
            refreshExamButtons();

            // 顯示考卷列表
            String allExams = String.join("\n", examContentMap);
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
        if (examContentMap.isEmpty()) {
            // 處理空數據，顯示提示信息
            JLabel noExamLabel = new JLabel("目前沒有可用的考卷");
            noExamLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
            noExamLabel.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(noExamLabel);
        } else {
            // 遍歷列表並創建對應的按鈕
            for (String examName : examContentMap) {
                // 創建代表此考卷的按鈕
                JButton examButton = new JButton(examName);
                examButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
                examButton.setPreferredSize(new Dimension(600, 50));

                // 設置按鈕點擊事件
                examButton.addActionListener(e -> {
                    // 點擊後執行的動作，這裡僅打印考卷名稱
                    ExamController examController = system.getExamController();

                    try {
                        // 調用 fetchExamFile 方法，獲取返回的文件路徑
                        String info=examController.getSettingsInfo(examName);
                        System.out.println(info);

                        String filePath = examController.fetchExamFile(examName);

                        // 用返回的文件路徑初始化 File 對象
                        File examFile = new File(filePath);

                        // 調用 showExamDetails 方法
                        showExamDetails(examName, examFile,extractNumberOfQuestions(info));
                    } catch (IOException k) {
                        System.err.println("檔案操作發生 IO 異常：" + k.getMessage());
                        k.printStackTrace(); // 打印詳細異常訊息
                    } // 如果需要文件支持，傳 null 或其他邏輯
                });

                // 添加到面板
                centerPanel.add(examButton);
            }
        }
        return new JScrollPane(centerPanel);
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

            showStudentStats();

    }

    private void showStudentStats() {
        JFrame statsFrame = new JFrame("學生統計資料");
        statsFrame.setSize(800, 600);
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ResultStatisticsView statsView = new ResultStatisticsView(currentUsername);
        statsFrame.add(statsView);

        statsFrame.setVisible(true);
    }

    private void showTeacherStats() {
        StatisticsView statsUI = new StatisticsView(this.system.getPaperStatisticsView(),
                this.system.getTeacherStatisticsView());
        statsUI.display();
    }

    private void showExamDetails(String examName, File examFile ,Integer questionAmount) {
        try {
            // 使用 ExamController 提取文件內容
            ExamController examController = system.getExamController();

            String examContent = examController.extractContentFromFile(examFile);

            // 取得考卷總題數的變數
            int questioncount= questionAmount;
            // 正在寫的題號計數器 初始為第一題
            final int[] currentQuestionIndex = {1};

            if (examContent != null) {
                // 創建 UI 展示題目和答案區域
                JFrame examFrame = new JFrame("考試: " + examName);
                examFrame.setSize(800, 600);
                examFrame.setLayout(new BorderLayout());
                // 動態題號標籤（新增部分）
                JLabel questionLabel = new JLabel("第 1 題 / 共 " + questionAmount + " 題");
                questionLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
                questionLabel.setHorizontalAlignment(SwingConstants.CENTER);

                // 題目區域
                JTextArea questionArea = new JTextArea(examContent);
                questionArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 18));
                questionArea.setEditable(false);
                JScrollPane questionScrollPane = new JScrollPane(questionArea);

                // 作答區域
                JTextArea answerArea = new JTextArea("");
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
                        system.submitAnswer(currentUsername, examName, "//end");
                        JOptionPane.showMessageDialog(examFrame, "答案已提交！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        examFrame.dispose();
                    }
                });
                submitButton.setEnabled(false);

                // 下一題
                JButton nextQuestionButton = new JButton("下一題");
                nextQuestionButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
                nextQuestionButton.addActionListener(e -> {
                    String answer = answerArea.getText().trim();
                    if (answer.isEmpty()) {
                        JOptionPane.showMessageDialog(examFrame, "尚未回答該題目！", "錯誤", JOptionPane.ERROR_MESSAGE);
                    } else if (currentQuestionIndex[0] == questionAmount) {
                        JOptionPane.showMessageDialog(examFrame, "已答至最後一題", "錯誤", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // 将答案记录或传递给后端系统处理
                        system.submitAnswer(currentUsername, examName, answer);
                        if (currentQuestionIndex[0] < questionAmount) {
                            currentQuestionIndex[0]++;
                            questionLabel.setText("第 " + currentQuestionIndex[0] + " 題 / 共 " + questionAmount + " 題"); // 更新題號
                            answerArea.setText(""); // 清空作答區域

                            JOptionPane.showMessageDialog(examFrame, "已回答第 " + currentQuestionIndex[0] + " 題", "成功", JOptionPane.INFORMATION_MESSAGE);

                            // 如果是最後一題，禁用「下一題」按鈕，啟用「提交答案」按鈕
                            if (currentQuestionIndex[0] == questionAmount) {
                                nextQuestionButton.setEnabled(false);
                                submitButton.setEnabled(true);

                            }
                        }

                    }
                });

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(nextQuestionButton);
                buttonPanel.add(submitButton);

                JPanel answerPanel = new JPanel(new BorderLayout());
                answerPanel.add(new JLabel("作答區:"), BorderLayout.NORTH);
                answerPanel.add(answerScrollPane, BorderLayout.CENTER);
                answerPanel.add(buttonPanel, BorderLayout.SOUTH);
                examFrame.add(questionLabel, BorderLayout.NORTH);
                examFrame.add(questionScrollPane, BorderLayout.CENTER);
                examFrame.add(answerPanel, BorderLayout.SOUTH);
                examFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(frame, "找不到這份考卷的內容！", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "打開考卷失敗：" + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void refreshExamButtons() {
        // 清空 frame 中的中央面板並重新初始化
        JScrollPane existingScrollPane = (JScrollPane) frame.getContentPane().getComponent(1);
        JPanel centerPanel = (JPanel) existingScrollPane.getViewport().getView();

        // 清空現有按鈕
        centerPanel.removeAll();

        // 檢查是否有考卷數據，如果有則重新生成按鈕，否則顯示提示
        if (examContentMap.isEmpty()) {
            JLabel noExamLabel = new JLabel("目前沒有可用的考卷");
            noExamLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
            noExamLabel.setHorizontalAlignment(SwingConstants.CENTER);
            centerPanel.add(noExamLabel);
        } else {
            for (String examName : examContentMap) {
                // 創建新的按鈕
                JButton examButton = new JButton(examName);
                examButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 24));
                examButton.setPreferredSize(new Dimension(600, 50));

                // 定義按鈕點擊行為
                examButton.addActionListener(e -> {

                    ExamController examController = system.getExamController();

                    try {
                        // 調用 fetchExamFile 方法，獲取返回的文件路徑
                        String info=examController.getSettingsInfo(examName);
                        System.out.println(info);
                        String filePath = examController.fetchExamFile(examName);

                        // 用返回的文件路徑初始化 File 對象
                        File examFile = new File(filePath);

                        // 調用 showExamDetails 方法
                        showExamDetails(examName, examFile,extractNumberOfQuestions(info));
                    } catch (IOException k) {
                        System.err.println("檔案操作發生 IO 異常：" + k.getMessage());
                        k.printStackTrace(); // 打印詳細異常訊息
                    } // 如果需要文件支持，傳 null 或其他邏輯
                });

                // 添加按鈕到中心面板
                centerPanel.add(examButton);
            }
        }

        // 刷新面板以顯示更新後的內容
        centerPanel.revalidate();
        centerPanel.repaint();
    }



    private void createExam() {
        QuestionGenerationView createExam = new QuestionGenerationView();
        createExam.display();
    }
    private int extractNumberOfQuestions(String responseJson) {
        // 提取返回中 "settings" 的字段
        String settings = responseJson.replaceAll(".*\"settings\":\"(.*?)\".*", "$1");

        // 使用正則表達式提取 "Number of questions:" 後的數字
        String numberOfQuestions = settings.replaceAll(".*Number of questions: (\\d+).*", "$1");

        // 將提取的數字轉換為 int 並返回
        return Integer.parseInt(numberOfQuestions);
    }

    private void uploadExam() {
        UploadView uploadExam = new UploadView(system);
        uploadExam.upload();
    }

    public void display() {
        frame.setVisible(true);
    }
}
