package org.example.VIew;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.example.Communicator.ClientServerCommunicator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultStatisticsView extends JPanel implements StatisticView {
    private StatisticsData paperStatisticsData;
    private final String currentUsername; // 名稱：用於篩選資料

    public ResultStatisticsView(String currentUsername) {
        this.currentUsername = currentUsername;
        this.setLayout(new BorderLayout());
        fetchAndShowStatistics(); // 初始化時自動從伺服器獲取數據
    }

    private void fetchAndShowStatistics() {
        try {
            ClientServerCommunicator communicator = new ClientServerCommunicator("http://localhost:8080");
            String response = communicator.fetchStatistics();

            // 修改：匯入 Arrays 並解析 JSON 陣列
            ObjectMapper mapper = new ObjectMapper();
            List<StatisticsData> dataList = Arrays.asList(mapper.readValue(response, StatisticsData[].class));

            // 過濾與 currentUsername 匹配的數據
            List<StatisticsData> filteredData = new ArrayList<>();
            for (StatisticsData data : dataList) {
                if (data.getName().equals(currentUsername)) {
                    filteredData.add(data);
                }
            }
            // 顯示所有匹配數據
            if (!filteredData.isEmpty()) {
                showAllStatistics(filteredData); // 修改為顯示多筆數據的方法
            } else {
                JOptionPane.showMessageDialog(this, "無法找到與使用者匹配的統計數據。", "信息", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "無法獲取統計數據: " + e.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAllStatistics(List<StatisticsData> statisticsDataList) {
        this.removeAll(); // 清空當前視圖

        // 左側文字框
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14)); // 確保使用支援中文的字體
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setPreferredSize(new Dimension(300, getHeight()));

        // 填充文字框內容
        StringBuilder textBuilder = new StringBuilder();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset(); // 用於柱狀圖

        for (StatisticsData data : statisticsDataList) {
            textBuilder.append(data.toString()).append("\n\n");
            dataset.addValue(data.getScore(), "分數", data.getTestName());
            DefaultCategoryDataset lineDataset = new DefaultCategoryDataset(); // 折線圖
            lineDataset.addValue(data.getScore(), "分數", data.getTestName()); 
        }

        textArea.setText(textBuilder.toString());

        // 右側柱狀圖
        JFreeChart barChart = createBarChart(dataset);
        ChartPanel chartPanel = new ChartPanel(barChart);

        // 佈局：左文字框 + 右柱狀圖
        this.add(textScrollPane, BorderLayout.WEST);
        this.add(chartPanel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    @Override
    public void showStatistics(StatisticsData data) {
        this.paperStatisticsData = data;

        // 顯示柱狀圖（取代 PieChart）
        if (data.getScoreDistribution() != null) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            data.getScoreDistribution().forEach((key, value) -> dataset.addValue(value, "次數", key));
            JFreeChart chart = createBarChart(dataset);
            ChartPanel chartPanel = new ChartPanel(chart);

            this.removeAll();
            this.add(chartPanel, BorderLayout.CENTER);
        }

        // 顯示總覽文字
        JTextArea textArea = new JTextArea(data.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.EAST);

        this.revalidate();
        this.repaint();
    }

    private JFreeChart createBarChart(DefaultCategoryDataset dataset) {
        // 創建柱狀圖
        JFreeChart chart = ChartFactory.createBarChart(
                "分數分布柱狀圖", // 圖表標題
                "範圍",         // X 軸標籤
                "次數",         // Y 軸標籤
                dataset         // 數據集
        );

        // 配置標題字體
        chart.setTitle(new TextTitle("分數分布柱狀圖", new Font("Microsoft JhengHei", Font.BOLD, 18)));

        // 配置 X 軸和 Y 軸字體
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getDomainAxis().setLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 14)); // X 軸字體
        plot.getDomainAxis().setTickLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12)); // X 軸刻度字體
        plot.getRangeAxis().setLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 14)); // Y 軸字體
        plot.getRangeAxis().setTickLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12)); // Y 軸刻度字體

        // 隱藏圖例
        chart.removeLegend();
        
        return chart;
    }

    @Override
    public void updateStatistics(StatisticsData data) {
        showStatistics(data);
    }


    public StatisticsData getStatistics() {
        return this.paperStatisticsData;
    }

    @Override
    public void showStatistics(org.example.StatisticsData data) {

    }

    @Override
    public void updateStatistics(org.example.StatisticsData data) {

    }

    public void hideStatistics() {
        this.removeAll();
        this.revalidate();
        this.repaint();
    }
}
