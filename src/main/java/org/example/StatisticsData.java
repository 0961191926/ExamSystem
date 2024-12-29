package org.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class StatisticsData {
    private int totalStudents;
    private int passedStudents;
    private double averageScore;
    private int highestScore;
    private int lowestScore;
    private Map<String, Integer> scoreDistribution;

    // Getters and Setters

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("總學生人數: ").append(totalStudents).append("\n");
        sb.append("及格學生人數: ").append(passedStudents).append("\n");
        sb.append("平均分數: ").append(averageScore).append("\n");
        sb.append("最高分: ").append(highestScore).append("\n");
        sb.append("最低分: ").append(lowestScore).append("\n");
        sb.append("分數分布:\n");
        for (Map.Entry<String, Integer> entry : scoreDistribution.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}

