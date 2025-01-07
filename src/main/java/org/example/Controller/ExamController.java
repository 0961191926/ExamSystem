package org.example.Controller;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.Communicator.ClientServerCommunicator;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExamController {
    private final ClientServerCommunicator communicator;
   // 存放考卷數據，鍵為 title
    private static final String BASE_API_URL = "http://localhost:8080"; // 服务端基础地址
    private static final String DOWNLOAD_DIR = "D://down"; // 本地下载文件存储目录
    // 構造方法，注入 ClientServerCommunicator
    public ExamController(ClientServerCommunicator communicator) {
        this.communicator = communicator;

    }
    public String postExam(String fileName) throws Exception {
        try {
            // 檢查文件名是否為空
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("文件名稱不能為空！");
            }

            // 構建 JSON 格式請求
            String payload = "{\"fileName\":\"" + fileName.trim() + "\"}";
            //String response = communicator.sendPostRequest("/exams", payload);

            // 打印伺服器返回的結果
            //System.out.println("伺服器回應: " + response);

            // 發送 POST 請求
            return communicator.sendPostRequest("/exams", payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "發送失敗: " + e.getMessage();
        }
    }
    public List<String> loadExamNames() {
        List<String> examNames = new ArrayList<>(); // 用於存儲考卷名稱

        try {
            // 調用服務器 API `/paper/getAll` 獲取考卷數據
            String response = communicator.sendGetRequest("/paper/getAll");

            // 假設響應是 JSON 陣列形式
            JSONArray papersArray = new JSONArray(response);

            // 遍歷每個考卷，提取 `title` 並添加到列表
            for (int i = 0; i < papersArray.length(); i++) {
                examNames.add(papersArray.getString(i));
            }
        } catch (Exception e) {
            // 捕捉異常並打印錯誤信息
            System.err.println("無法加載考卷名稱: " + e.getMessage());
        }

        return examNames; // 返回包含考卷名稱的列表
    }

    public String fetchExamFile(String examName) throws IOException {
        // 檢查參數
        if (examName == null || examName.trim().isEmpty()) {
            throw new IllegalArgumentException("考試名稱（examName）不能為空。");
        }

        // 創建 OkHttp 客戶端
        OkHttpClient client = new OkHttpClient();

        // 構建下載 URL
        String downloadUrl = BASE_API_URL + "/upload?fileName=" + examName.trim();

        // 構建請求
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();

        // 發送請求並處理響應
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    throw new IOException("服務器上未找到該文件：" + examName);
                } else {
                    throw new IOException("下載文件失敗，服務器返回錯誤碼：" + response.code());
                }
            }

            // 檢測 Content-Type 獲取 MIME 類型，並生成副檔名
            String contentType = response.header("Content-Type", "application/octet-stream");
            String fileExtension = getFileExtensionFromContentType(contentType);

            // 確保下載目錄存在
            File downloadDir = new File(DOWNLOAD_DIR);
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            // 構建本地文件路徑
            String localFilePath = DOWNLOAD_DIR + File.separator + examName + fileExtension;

            // 保存文件到本地
            try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
                fos.write(response.body().bytes());
            }

            return localFilePath; // 返回保存的文件路徑
        }
    }

    /**
     * 根據 MIME 類型獲取文件擴展名
     */
    private String getFileExtensionFromContentType(String contentType) {
        switch (contentType) {
            case "application/pdf":
                return ".pdf";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "text/plain":
                return ".txt";
            default:
                return ""; // 如果未識別類型，不加副檔名
        }
    }



    public String getSettingsInfo( String examName) {
        try {
            // 調用 ClientServerCommunicator 發送 GET 請求
            String response = communicator.sendGetRequest("/paper/getSettings?examName=" + examName);

            // 提取需要的信息
            return filterSettings(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }

    /**
     * 從返回的 JSON 中提取 `Public/Private` 和 `Number of questions` 信息
     *
     * @param responseJson 伺服器返回的 JSON 字符串
     * @return 過濾後的信息
     */
    private String filterSettings(String responseJson) {
        // 提取返回中 "settings" 的內容
        String settings = responseJson.replaceAll(".*\"settings\":\"(.*?)\".*", "$1");

        // 提取 "Public/Private" 信息
        String publicPrivate = settings.replaceAll(".*Public/Private\\?: (Public|Private).*", "Public/Private?: $1");

        // 提取 "Number of questions" 信息
        String numberOfQuestions = settings.replaceAll(".*Number of questions: (\\d+).*", "Number of questions: $1");

        // 組合結果
        return String.format("%s, %s", publicPrivate, numberOfQuestions);
    }





    public String extractContentFromFile(File file) throws Exception {
        StringBuilder contentBuilder = new StringBuilder();

        if (file.getName().endsWith(".txt")) {
            // 處理 TXT 文件
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }
            }
        } else if (file.getName().endsWith(".docx")) {
            // 處理 Word 文件
            extractWordContent(file, contentBuilder);
        } else if (file.getName().endsWith(".pdf")) {
            // 處理 PDF 文件
            extractPdfContent(file, contentBuilder);
        } else {
            throw new IllegalArgumentException("不支持的文件類型: " + file.getName());
        }

        return contentBuilder.toString();
    }

    private void extractWordContent(File file, StringBuilder contentBuilder) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            // 提取段落文字內容
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText().trim();
                if (!text.isEmpty()) {
                    contentBuilder.append(text).append("\n");
                }
            }

            // 提取表格內容
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        contentBuilder.append(cell.getText().trim()).append("\t"); // 每個單元格用 \t 分隔
                    }
                    contentBuilder.append("\n"); // 每行換行
                }
            }
        }
    }

        public List<String> extractAnswersFromFile(File answerFile) throws Exception {
            List<String> answers = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(answerFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 假设每行格式为 "1. A" 或 "A"，可以根据实际需求调整解析逻辑
                    line = line.trim();
                    if (!line.isEmpty()) {
                        // 检查格式：排除题号部分，仅提取答案
                        String[] parts = line.split("\\.");
                        if (parts.length > 1) {
                            answers.add(parts[1].trim()); // 提取答案部分
                        } else {
                            answers.add(line); // 如果没有"."，直接保存整行
                        }
                    }
                }
            } catch (Exception e) {
                throw new Exception("解析答案文件失敗: " + e.getMessage(), e);
            }

            return answers;
        }




    private void extractPdfContent(File file, StringBuilder contentBuilder) throws Exception {
        // 檢查輸入參數
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("提供的文件無效或文件不存在: " + (file != null ? file.getAbsolutePath() : "null"));
        }
        if (contentBuilder == null) {
            throw new IllegalArgumentException("StringBuilder 不能為 null！");
        }

        // 嘗試提取 PDF 內容
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            contentBuilder.append(pdfStripper.getText(document)); // 直接追加文本
        } catch (IOException e) {
            throw new IOException("讀取 PDF 文件時發生錯誤: " + file.getAbsolutePath(), e);
        } catch (Exception e) {
            throw new Exception("提取 PDF 內容時出現未知錯誤: " + file.getAbsolutePath(), e);
        }
    }
}