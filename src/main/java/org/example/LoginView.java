package org.example;

import javax.swing.*;
import java.awt.event.*;

// 登入UI
public class LoginView {
    private JFrame frame;
    private ExamSystem system;

    public LoginView(ExamSystem system) {
        this.system = system;
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("登入");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        JLabel accountLabel = new JLabel("Account:");
        frame.add(accountLabel);
        JTextField accountField = new JTextField(15);
        frame.add(accountField);
        JLabel passwordLabel = new JLabel("Password:");
        frame.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField(15);
        frame.add(passwordField);
        JButton loginButton = new JButton("Login");
        frame.add(loginButton);
        JButton registerButton = new JButton("Register");
        frame.add(registerButton);

        loginButton.addActionListener(e -> login(accountField.getText(), new String(passwordField.getPassword())));

        // Register action
        registerButton.addActionListener(e -> {
            String username = accountField.getText();
            String password = new String(passwordField.getPassword());
            String role = JOptionPane.showInputDialog(frame, "Enter Role (e.g., Admin, User)");

            if (system.getUserManager().registerUser(username, password, role)) {
                JOptionPane.showMessageDialog(frame, "Registration successful.");
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed. Username might already exist.");
            }
        });

        // Layout adjustment
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = frame.getWidth();
                int frameHeight = frame.getHeight();

                accountLabel.setBounds(frameWidth / 6, frameHeight / 10, frameWidth / 4, frameHeight / 15);
                accountField.setBounds(frameWidth / 2, frameHeight / 10, frameWidth / 3, frameHeight / 15);
                passwordLabel.setBounds(frameWidth / 6, frameHeight / 5, frameWidth / 4, frameHeight / 15);
                passwordField.setBounds(frameWidth / 2, frameHeight / 5, frameWidth / 3, frameHeight / 15);
                loginButton.setBounds(frameWidth / 4, frameHeight / 3, frameWidth / 5, frameHeight / 15);
                registerButton.setBounds(frameWidth / 2, frameHeight / 3, frameWidth / 5, frameHeight / 15);
            }
        });

        frame.setVisible(true);
    }

    private void login(String username, String password) {
        if (system.getUserManager().validateLogin(username, password)) {
            String role = system.getUserManager().getUserRole(username); // 取得角色
            frame.dispose(); // 關閉登入畫面
            system.showMainSystem(username, role); // 顯示主畫面，並傳遞角色
        } else {
        }
    }

    public void display() {
        frame.setVisible(true);
    }
}
