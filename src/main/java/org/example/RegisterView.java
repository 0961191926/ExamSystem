package org.example;

import javax.swing.*;

// 註冊UI
public class RegisterView {
    private JFrame frame;
    private ExamSystem system;

    public RegisterView(ExamSystem system) {
        this.system = system;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("註冊");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        // Component declarations and layout...

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            // Registration logic
            frame.dispose();
            system.showLoginUI();
        });
    }

    public void display() {
        frame.setVisible(true);
    }
}
