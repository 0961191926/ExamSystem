package org.example;

import javax.swing.*;
import java.io.File;
import java.util.Arrays; // Import Arrays to create a list of questions for testing
import java.util.List; // Add this import for List

// 上傳考卷UI
public class UploadView {
    public UploadView() {
        // Constructor doesn't require any parameters
    }

    public void upload() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(null, "考卷已成功上傳: " + selectedFile.getName());

            // Example hardcoded questions list for testing
            List<String> questions = Arrays.asList("Question 1", "Question 2", "Question 3");

            // Call method to create and display the score setting frame
            createScoreSettingFrame(questions);
        }
    }

    private void createScoreSettingFrame(List<String> questions) {
        // Create and display the ScoreSettingFrame
        PaperSettingView scoreSettingFrame = new PaperSettingView(questions);
        scoreSettingFrame.display(); // Show the score setting frame
    }
}
