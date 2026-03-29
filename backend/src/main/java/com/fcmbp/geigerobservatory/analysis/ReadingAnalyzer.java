package com.fcmbp.geigerobservatory.analysis;

import com.fcmbp.geigerobservatory.model.AnalysisSnapshot;
import com.fcmbp.geigerobservatory.model.Reading;
import org.springframework.stereotype.Component;

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

    public AnalysisSnapshot snapshot(List<Reading> recent) {
        if (recent.isEmpty()) {
            return new AnalysisSnapshot(0.0, 0, false, "No readings available yet.");
        }

        double movingAverage = recent.stream()
                .mapToInt(Reading::cpm)
                .average()
                .orElse(0.0);

        int recentMax = recent.stream()
                .mapToInt(Reading::cpm)
                .max()
                .orElse(0);

        boolean elevated = recentMax > movingAverage * 1.8 && recentMax > movingAverage + 20;
        String narrative = elevated
                ? "Recent readings show an elevated spike relative to the moving baseline."
                : "Recent readings remain within the expected operating band.";

        return new AnalysisSnapshot(movingAverage, recentMax, elevated, narrative);
    }
}
