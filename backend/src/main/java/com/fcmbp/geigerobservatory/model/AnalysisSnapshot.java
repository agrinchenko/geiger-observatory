package com.fcmbp.geigerobservatory.model;

import java.util.List;

public record AnalysisSnapshot(
        int windowMinutes,
        int thresholdCpm,
        int minDurationSeconds,
        int sampleCount,
        double movingAverage,
        double standardDeviation,
        int recentMax,
        boolean elevated,
        boolean anomalyDetected,
        int anomalyCount,
        String narrative,
        List<DetectedAnomaly> anomalies
) {
}
