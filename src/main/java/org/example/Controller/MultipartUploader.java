package org.example.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultipartUploader {

    private final String BOUNDARY = "----Boundary" + UUID.randomUUID().toString(); // 动态生成唯一边界字符串
    private final List<byte[]> multipartData = new ArrayList<>();
    private String dirName; // 用于传递目录名称

    /**
     * 设置表头中的 dirName（目录名称）。
     *
     * @param dirName 要设置的 dirName 值
     * @return MultipartUploader
     */
    public MultipartUploader setDirName(String dirName) {
        this.dirName = dirName;
        return this;
    }

    /**
     * 添加文件部分到 HTTP 请求。
     *
     * @param fieldName 文件字段的名称
     * @param filePath 文件路径
     * @throws IOException 如果文件读取失败
     */
    public MultipartUploader addFile(String fieldName, Path filePath) throws IOException {
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) {
            // 根据扩展名判断 MIME 类型
            String extension = getFileExtension(filePath.getFileName().toString());
            switch (extension) {
                case "pdf":
                    mimeType = "application/pdf";
                    break;
                case "docx":
                    mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    break;
                case "doc":
                    mimeType = "application/msword";
                    break;
                default:
                    mimeType = "application/octet-stream"; // 默认 MIME 类型
            }
        }

        System.out.println("Adding file: " + filePath + " with MIME type: " + mimeType);

        multipartData.add(("--" + BOUNDARY + "\r\nContent-Disposition: form-data; name=\"" + fieldName +
                "\"; filename=\"" + filePath.getFileName() + "\"\r\n" +
                "Content-Type: " + mimeType + "\r\n\r\n").getBytes());
        multipartData.add(Files.readAllBytes(filePath)); // 添加文件的实际数据
        multipartData.add("\r\n".getBytes());
        return this;
    }

    /**
     * 构建 HTTP 请求的 BodyPublisher。
     *
     * @return HttpRequest.BodyPublisher
     */
    public HttpRequest.BodyPublisher build() {
        // 添加结束分隔符
        multipartData.add(("--" + BOUNDARY + "--\r\n").getBytes());
        return HttpRequest.BodyPublishers.ofByteArrays(multipartData);
    }

    /**
     * 发送文件上传 HTTP 请求。
     *
     * @param targetUrl 目标 URL
     * @return 返回服务器响应内容
     * @throws IOException 如果发送请求失败
     * @throws InterruptedException 请求被打断
     */
    public String upload(String targetUrl) throws IOException, InterruptedException {
        // 验证 dirName 是否已设置
        if (dirName == null || dirName.isEmpty()) {
            throw new IllegalArgumentException("dirName 未设置，请先设置 dirName。");
        }
        System.out.println("dirName being sent: " + dirName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
                .header("dirName", dirName) // 添加自定义 Header
                .POST(build())
                .build();

        // 发送请求
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 检查服务器响应
        if (response.statusCode() == 200) {
            return "上传成功: " + response.body();
        } else {
            throw new IOException("上传失败，状态码: " + response.statusCode() + "，响应: " + response.body());
        }
    }

    /**
     * 获取文件的扩展名。
     *
     * @param fileName 文件名称
     * @return 文件扩展名
     */
    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return "";
    }
}