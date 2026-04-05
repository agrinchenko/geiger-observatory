package com.fcmbp.geigerobservatory.analysis;

import com.fcmbp.geigerobservatory.model.AnalysisSnapshot;
import com.fcmbp.geigerobservatory.model.Reading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadingAnalyzerTests {

    private final ReadingAnalyzer analyzer = new ReadingAnalyzer();

    @Test
    void detectsSustainedRunAboveThreshold() {
        List<Reading> readings = new ArrayList<>();
        Instant start = Instant.parse("2026-04-04T00:00:00Z");

        for (int index = 0; index < 120; index++) {
            int cpm = index >= 20 && index <= 89 ? 47 : 24;
            readings.add(new Reading(start.plusSeconds(index * 2L), cpm, "simulator", false));
        }

        AnalysisSnapshot snapshot = analyzer.snapshot(readings, 4, 40, 15);

        assertTrue(snapshot.anomalyDetected());
        assertEquals(1, snapshot.anomalyCount());
        assertEquals(47.0, snapshot.anomalies().getFirst().averageCpm());
    }

    @Test
    void ignoresBriefThresholdCrossing() {
        List<Reading> readings = new ArrayList<>();
        Instant start = Instant.parse("2026-04-04T00:00:00Z");

        for (int index = 0; index < 120; index++) {
            int cpm = index >= 20 && index <= 35 ? 42 : 24;
            readings.add(new Reading(start.plusSeconds(index * 2L), cpm, "simulator", false));
        }

        AnalysisSnapshot snapshot = analyzer.snapshot(readings, 4, 40, 40);

        assertFalse(snapshot.anomalyDetected());
        assertEquals(0, snapshot.anomalyCount());
    }
}
