package org.example.VIew;

import org.example.StatisticsData;

public interface StatisticView {
    void showStatistics(StatisticsData data);
    void updateStatistics(StatisticsData data);
    void hideStatistics();

    void showStatistics(org.example.VIew.StatisticsData data);

    void updateStatistics(org.example.VIew.StatisticsData data);


}