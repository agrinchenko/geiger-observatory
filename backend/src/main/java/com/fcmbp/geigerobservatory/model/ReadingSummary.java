package com.fcmbp.geigerobservatory.model;

public record ReadingSummary(
        Reading latest,
        double averageCpm,
        int maxCpm,
        long totalReadings,
        long anomalies
) {
}
