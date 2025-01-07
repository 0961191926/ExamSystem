package org.example.Communicator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientServerCommunicator {
    private final HttpClient client;
    private final String serverUrl;

    public ClientServerCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    public String fetchStatistics() throws Exception {
        return sendGetRequest("/statistics/getAll");
    }

    /**
     * 發送 GET 請求
     *
     * @param endpoint 伺服器的 API 端點
     * @return 伺服器回應的內容
     * @throws Exception 處理請求時的例外
     */
    public String sendGetRequest(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .GET()
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("GET request failed with response code: " + response.statusCode());
        }
    }

    /**
     * 發送 POST 請求
     *
     * @param endpoint 伺服器的 API 端點
     * @param payload  要發送的 JSON 格式內容
     * @return 伺服器回應的內容
     * @throws Exception 處理請求時的例外
     */
    public String sendPostRequest(String endpoint, String payload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(payload))// 設定為 POST 並傳送字串形式的資料
                .header("Content-Type", "application/json")// 設定 HTTP Header 表明傳送的是 JSON 格式
                .header("Accept", "application/json") // 設定希望接收 JSON 格式的回應
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("POST request failed with response code: " + response.statusCode());
        }
    }

    /**
     * 發送 PUT 請求
     *
     * @param endpoint 伺服器的 API 端點
     * @param payload  要發送的 JSON 格式內容
     * @return 伺服器回應的內容
     * @throws Exception 處理請求時的例外
     */
    public String sendPutRequest(String endpoint, String payload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("PUT request failed with response code: " + response.statusCode());
        }
    }

    /**
     * 發送 DELETE 請求
     *
     * @param endpoint 伺服器的 API 端點
     * @param payload  要發送的 JSON 格式內容
     * @return 伺服器回應的內容
     * @throws Exception 處理請求時的例外
     */
    public String sendDeleteRequest(String endpoint, String payload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + endpoint))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(payload))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new Exception("DELETE request failed with response code: " + response.statusCode());
        }
    }

}

