package org.example;
import org.apache.poi.xwpf.usermodel.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ExamController {

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
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            contentBuilder.append(text);
        }
    }
}