package org.example;

public class Question {
    private String questionText;
    private String correctAnswer;
    private String type; // e.g., "multiple-choice", "fill-in-the-blank"
    private int score;

    public Question(String questionText, String correctAnswer, String type, int score) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.type = type;
        this.score = score;
    }

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getType() {
        return type;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return questionText + " (" + type + ")";
    }
}

