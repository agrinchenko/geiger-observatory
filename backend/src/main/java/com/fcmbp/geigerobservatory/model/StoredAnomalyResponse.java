package com.fcmbp.geigerobservatory.model;

import java.time.Instant;

public record StoredAnomalyResponse(
        long id,
        Instant startTime,
        Instant endTime,
        double averageCpm,
        long durationSeconds,
        int sampleCount,
        int thresholdCpm,
        int minDurationSeconds,
        String aiComment
) {
}
