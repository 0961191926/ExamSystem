package org.example.VIew;

import org.example.Controller.AccountController;
import org.example.ExamSystem;

import javax.swing.*;
import java.awt.event.*;

// 登入UI
import java.awt.*;

// 登入UI
public class LoginView {
    private JFrame frame;
    private AccountController accountController;

    public LoginView(AccountController accountController) {
        this.accountController = accountController;
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


            if (accountController.handleRegisterRequest(username, password, "student" )) {
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
        System.out.println("Attempting to execute handleLoginRequest...");
        if (this.accountController.handleLoginRequest(username, password)) {
            System.out.println("handleLoginRequest executed successfully!");
            String role = this.accountController.handleGetUserRoleRequest(username);
            this.frame.dispose(); // 關閉登入視窗

            // 呼叫 ExamSystem 的 showMainSystem 方法來顯示主系統畫面
            SwingUtilities.invokeLater(() -> {
                ExamSystem examSystem = new ExamSystem(); // 假設 ExamSystem 是主要的系統類別
                examSystem.showMainSystem(username, role);
            });
        } else {
            System.out.println("handleLoginRequest execution failed or returned false.");
            JOptionPane.showMessageDialog((Component) null, "Login failed.");
        }
    }
    public void dismiss() {
        frame.setVisible(false);
    }





    public void display() {
        frame.setVisible(true);
    }
}
