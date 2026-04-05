package com.fcmbp.geigerobservatory.model;

import java.time.Instant;

public record DetectedAnomaly(
        Instant startTime,
        Instant endTime,
        double averageCpm,
        long durationSeconds,
        int sampleCount
) {
}
