package com.fcmbp.geigerobservatory.model;

public record AnalysisSnapshot(
        double movingAverage,
        int recentMax,
        boolean elevated,
        String narrative
) {
}
