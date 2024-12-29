package org.example;
public class StudentAnswer {
    private String username;
    private Question question;
    private String givenAnswer;
    private int score;

    public StudentAnswer(String username, Question question, String givenAnswer) {
        this.username = username;
        this.question = question;
        this.givenAnswer = givenAnswer;
        this.score = givenAnswer.equals(question.getCorrectAnswer()) ? question.getScore() : 0;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public Question getQuestion() {
        return question;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public int getScore() {
        return score;
    }
}
