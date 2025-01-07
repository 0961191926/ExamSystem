package org.example.Controller;

import org.example.Communicator.ClientServerCommunicator;
import org.json.JSONObject;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class AccountController {
    private final ClientServerCommunicator communicator;

    public AccountController(ClientServerCommunicator communicator) {
        this.communicator = communicator; // 使用 ClientServerCommunicator 與伺服器通信
    }

    /**
     * 用戶註冊
     */
    public boolean handleRegisterRequest(String username, String password, String role) {
        if (isInputInvalid(username, password, role)) {
            JOptionPane.showMessageDialog(null, "帳號、密碼或角色不能為空");
            return false;
        }

        try {
            // 構建 JSON 請求
            String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}", username, password, role);
            // 發送 POST 請求到伺服器的 /account/create 節點
            String response = communicator.sendPostRequest("/account/create", payload);

            // 檢查是否是 JSON 格式響應
            JSONObject responseObject = new JSONObject(response); // 使用 org.json 解析 JSON
            String status = responseObject.getString("status");   // 提取 "status" 欄位
            String message = responseObject.getString("message"); // 提取 "message" 欄位

            if ("success".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null, "註冊成功: " + message);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "註冊失敗: " + message);
                return false;
            }
        } catch (Exception e) {
            // 捕獲異常並向用戶反饋
            JOptionPane.showMessageDialog(null, "註冊失敗: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 用戶登入
     */
    public boolean handleLoginRequest(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "帳號和密碼不能為空");
            return false;
        }
        System.out.println("handleLoginRequest has been called with username: " + username + " and password: " + password);

        try {
            // 構建 JSON 請求
            String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            // 發送 POST 請求到伺服器的 /account/login 節點
            String response = communicator.sendPostRequest("/account/login", payload);

            // 檢查是否是 JSON 格式響應
            JSONObject responseObject = new JSONObject(response); // 使用 org.json 解析 JSON
            String status = responseObject.getString("status");   // 解析 "status" 字段
            String message = responseObject.getString("message"); // 解析 "message" 字段
            System.out.println("Response received: " + response);
            if ("success".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(null, "登入成功: " + message);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "登入失敗: " + message);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "登入失敗: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 取得用戶角色
     */
    public String handleGetUserRoleRequest(String username) {
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "帳號不能為空");
            return null;
        }

        try {
            // 發送 GET 請求到伺服器的 /account/info 節點
            String response = communicator.sendGetRequest("/account/info?username=" + username);

            if (!response.isEmpty()) {
                return response; // 假設伺服器直接返回角色名稱
            } else {
                JOptionPane.showMessageDialog(null, "用戶不存在");
                return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "無法獲取用戶角色: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * 私有方法 - 驗證輸入是否有效
     */
    private boolean isInputInvalid(String username, String password, String role) {
        return username == null || username.isEmpty() || password == null || password.isEmpty() || role == null || role.isEmpty();
    }
}