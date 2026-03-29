package com.fcmbp.geigerobservatory.model;

import java.time.Instant;

public record Reading(
        Instant timestamp,
        int cpm,
        String source,
        boolean anomalous
) {
}
