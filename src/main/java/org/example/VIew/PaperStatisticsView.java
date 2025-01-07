package org.example.VIew;

import org.example.StatisticsData;

public class PaperStatisticsView implements StatisticView {
    private StatisticsData paperStatisticsData;

    public void showStatistics(StatisticsData data) {
        // 顯示試卷統計數據
    }

    @Override
    public void updateStatistics(StatisticsData data) {
        // 更新試卷統計數據
        this.paperStatisticsData = data;
    }

    public void hideStatistics() {
        // 隱藏試卷統計視圖
    }

    @Override
    public void showStatistics(org.example.VIew.StatisticsData data) {

    }

    @Override
    public void updateStatistics(org.example.VIew.StatisticsData data) {

    }


}

// 其他統計視圖類的實現類似