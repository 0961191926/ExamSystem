package org.example;

public interface StatisticView {
    void showStatistics(StatisticsData data);
    void updateStatistics(StatisticsData data);
    void hideStatistics();
    StatisticsData getStatistics();
}