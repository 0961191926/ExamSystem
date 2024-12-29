package org.example;

import java.io.*;
import java.util.*;

public class ExamManager {
    private final String EXAM_FILE = "exam_questions.txt"; // 考试文件
    private final Map<String, List<Question>> exams = new HashMap<>(); // 考试名 -> 问题列表
    private final Map<String, Map<String, String>> studentAnswers = new HashMap<>(); // 用户名 -> (问题 -> 答案)
    private final Map<String, Integer> questionScores = new HashMap<>(); // 问题 -> 分数
    private final Map<String, String> questionTypes = new HashMap<>(); // 问题 -> 类型

    /**
     * 构造函数 - 初始化加载考试
     */
    public ExamManager() {
        loadExamsFromFile();
    }

    /**
     * 保存考试到文件
     */
    public void saveExam(String examName, List<Question> questions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXAM_FILE, true))) {
            for (Question question : questions) {
                writer.write(examName + "::" + question.getQuestionText() + "::" + question.getType() + "::" + question.getScore());
                writer.newLine();
            }
            exams.put(examName, questions);
            System.out.println("Exam saved: " + examName);
        } catch (IOException e) {
            System.err.println("Error saving exam: " + e.getMessage());
        }
    }

    /**
     * 从文件加载考试
     */
    private void loadExamsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(EXAM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length < 4) continue; // 如果数据不完整，跳过该行

                String examName = parts[0].trim();
                String questionText = parts[1].trim();
                String questionType = parts[2].trim();
                int questionScore = Integer.parseInt(parts[3].trim());

                // 假设没有正确答案时使用一个默认值
                String correctAnswer = "DefaultAnswer"; // 你可以根据需要调整

                exams.putIfAbsent(examName, new ArrayList<>());
                Question question = new Question(questionText, correctAnswer, questionType, questionScore);
                exams.get(examName).add(question);

                questionScores.put(questionText, questionScore);
                questionTypes.put(questionText, questionType);
            }
            System.out.println("Exams loaded from file.");
        } catch (IOException e) {
            System.err.println("Error loading exams: " + e.getMessage());
        }
    }



    /**
     * 获取用户的答题记录
     */
    public Map<String, String> getStudentAnswers(String username) {
        return studentAnswers.getOrDefault(username, new HashMap<>());
    }

    /**
     * 获取指定考试的内容
     */
    public List<Question> getExam(String examName) {
        return exams.getOrDefault(examName, Collections.emptyList());
    }

    /**
     * 计算用户的考试总分
     */
    public int calculateTotalScore(String username) {
        Map<String, String> answers = studentAnswers.get(username);
        if (answers == null) return 0;

        int totalScore = 0;
        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String question = entry.getKey();
            String givenAnswer = entry.getValue();
            String correctAnswer = getCorrectAnswer(question);

            if (correctAnswer != null && correctAnswer.equalsIgnoreCase(givenAnswer)) {
                totalScore += questionScores.getOrDefault(question, 0);
            }
        }
        return totalScore;
    }

    /**
     * 获取问题的正确答案
     */
    private String getCorrectAnswer(String question) {
        for (List<Question> questionList : exams.values()) {
            for (Question q : questionList) {
                if (q.getQuestionText().equalsIgnoreCase(question)) {
                    return q.getCorrectAnswer();
                }
            }
        }
        return null;
    }

    /**
     * 列出用户已完成的考试
     */
    public List<String> getCompletedExams(String username) {
        Map<String, String> userAnswers = studentAnswers.get(username);
        if (userAnswers == null) return Collections.emptyList();

        List<String> completedExams = new ArrayList<>();
        for (String examName : exams.keySet()) {
            List<Question> examQuestions = exams.get(examName);
            boolean isCompleted = examQuestions.stream()
                    .allMatch(q -> userAnswers.containsKey(q.getQuestionText()));
            if (isCompleted) {
                completedExams.add(examName);
            }
        }
        return completedExams;
    }


}
