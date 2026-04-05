package com.fcmbp.geigerobservatory.analysis;

import com.fcmbp.geigerobservatory.model.AnalysisSnapshot;
import com.fcmbp.geigerobservatory.model.DetectedAnomaly;
import com.fcmbp.geigerobservatory.model.Reading;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReadingAnalyzer {

    public boolean isAnomalous(int cpm, List<Reading> recent) {
        if (recent.size() < 5) {
            return false;
        }

        double baseline = recent.stream()
                .mapToInt(Reading::cpm)
                .average()
                .orElse(0.0);

        return cpm > baseline * 1.8 && cpm > baseline + 20;
    }

    public AnalysisSnapshot snapshot(
            List<Reading> recent,
            int windowMinutes,
            int thresholdCpm,
            int minDurationSeconds
    ) {
        if (recent.isEmpty()) {
            return new AnalysisSnapshot(
                    windowMinutes,
                    thresholdCpm,
                    minDurationSeconds,
                    0,
                    0.0,
                    0.0,
                    0,
                    false,
                    false,
                    0,
                    "No readings available yet.",
                    List.of()
            );
        }

        double movingAverage = recent.stream()
                .mapToInt(Reading::cpm)
                .average()
                .orElse(0.0);

        double variance = recent.stream()
                .mapToDouble(reading -> Math.pow(reading.cpm() - movingAverage, 2))
                .average()
                .orElse(0.0);
        double standardDeviation = Math.sqrt(variance);

        int recentMax = recent.stream()
                .mapToInt(Reading::cpm)
                .max()
                .orElse(0);

        boolean elevated = recentMax > thresholdCpm;
        List<DetectedAnomaly> anomalies = detectAnomalies(recent, thresholdCpm, minDurationSeconds);
        boolean anomalyDetected = !anomalies.isEmpty();

        return new AnalysisSnapshot(
                windowMinutes,
                thresholdCpm,
                minDurationSeconds,
                recent.size(),
                movingAverage,
                standardDeviation,
                recentMax,
                elevated,
                anomalyDetected,
                anomalies.size(),
                buildNarrative(thresholdCpm, minDurationSeconds, anomalies, recentMax),
                anomalies
        );
    }

    public List<DetectedAnomaly> detectAnomalies(List<Reading> readings, int thresholdCpm, int minDurationSeconds) {
        List<DetectedAnomaly> anomalies = new ArrayList<>();
        long minimumDurationSeconds = Math.max(1, minDurationSeconds);

        int runStart = -1;
        for (int index = 0; index < readings.size(); index++) {
            boolean aboveThreshold = readings.get(index).cpm() > thresholdCpm;

            if (aboveThreshold && runStart < 0) {
                runStart = index;
            }

            boolean closingRun = runStart >= 0 && (!aboveThreshold || index == readings.size() - 1);
            if (!closingRun) {
                continue;
            }

            int runEnd = aboveThreshold && index == readings.size() - 1 ? index : index - 1;
            if (runEnd < runStart) {
                runStart = -1;
                continue;
            }

            Reading start = readings.get(runStart);
            Reading end = readings.get(runEnd);
            long durationSeconds = Math.max(0, Duration.between(start.timestamp(), end.timestamp()).getSeconds());
            int sampleCount = runEnd - runStart + 1;
            double averageCpm = readings.subList(runStart, runEnd + 1).stream()
                    .mapToInt(Reading::cpm)
                    .average()
                    .orElse(0.0);

            if (durationSeconds >= minimumDurationSeconds) {
                anomalies.add(new DetectedAnomaly(
                        start.timestamp(),
                        end.timestamp(),
                        averageCpm,
                        durationSeconds,
                        sampleCount
                ));
            }

            runStart = -1;
        }

        return anomalies;
    }

    private String buildNarrative(
            int thresholdCpm,
            int minDurationSeconds,
            List<DetectedAnomaly> anomalies,
            int recentMax
    ) {
        if (!anomalies.isEmpty()) {
            DetectedAnomaly latest = anomalies.getLast();
            return String.format(
                    "Detected %d sustained run(s) above %d CPM. Latest run lasted %.1f minutes with an average of %.1f CPM.",
                    anomalies.size(),
                    thresholdCpm,
                    latest.durationSeconds() / 60.0,
                    latest.averageCpm()
            );
        }

        if (recentMax > thresholdCpm) {
            return String.format(
                    "Readings exceeded %d CPM briefly, but not for the required %d second run.",
                    thresholdCpm,
                    minDurationSeconds
            );
        }

        return String.format(
                "No sustained anomaly detected. The current window does not contain a run above %d CPM lasting at least %d second(s).",
                thresholdCpm,
                minDurationSeconds
        );
    }
}
