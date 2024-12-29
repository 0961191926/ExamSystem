package org.example;

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

// 管理使用者註冊登入等
public class UserManager {
    private final String USER_FILE = "JavaExamMultiple1224Class/users.txt"; // 相對路徑
    private final Map<String, String> users = new HashMap<>();

    public UserManager() {
        loadUsersFromFile(); // Load users when initializing the UserManager
        if (users.isEmpty()) {
            // 如果用戶檔案是空的，創建預設帳號
            registerUser("123", "123", "admin");
            registerUser("peter", "0409", "teacher");
            registerUser("piyan", "sobig", "teacher");
            registerUser("student1", "1", "student");
            registerUser("user1", "password123", "Admin");
            registerUser("user2", "password456", "User");
        }
    }

    public boolean registerUser(String username, String password, String role) {
        // 檢查帳號、密碼和角色是否為空
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || role == null
                || role.isEmpty()) {
            JOptionPane.showMessageDialog(null, "帳號、密碼或角色不能為空");
            return false; // 如果任何一項為空，則返回 false
        }

        // 檢查用戶是否已存在
        if (users.containsKey(username)) {
            JOptionPane.showMessageDialog(null, "該帳號已經存在");
            return false; // 如果帳號已存在，則返回 false
        }

        // 儲存用戶資料（帳號：密碼:角色）
        users.put(username, password + ":" + role);
        saveUsersToFile(); // 註冊後保存用戶資料到檔案
        return true; // 註冊成功
    }

    public boolean validateLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "帳號和密碼不能為空");
            return false; // 如果帳號或密碼為空，返回 false
        }

        if (users.containsKey(username)) {
            String userData = users.get(username); // 取得帳號對應的資料 (密碼:角色)
            if (userData != null) {
                String[] parts = userData.split(":");
                if (parts.length == 2 && parts[0].equals(password)) { // 比對密碼部分
                    return true; // 登入成功
                } else {
                    JOptionPane.showMessageDialog(null, "登入失敗，密碼錯誤");
                    return false; // 密碼錯誤
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "登入失敗，帳號不存在");
        }
        return false; // 帳號不存在
    }

    public String getUserRole(String username) {
        String[] userData = users.get(username).split(":");
        return userData[1]; // Return the role
    }

    private void loadUsersFromFile() {
        File userFile = new File(USER_FILE); // Create a File object for users.txt
        System.out.println("Loading users from file: " + userFile.getAbsolutePath()); // Print the absolute path

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]); // username -> password:role
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void saveUsersToFile() {
        File userFile = new File(USER_FILE); // Create a File object for users.txt
        System.out.println("Saving users to file: " + userFile.getAbsolutePath()); // Print the absolute path

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

}
