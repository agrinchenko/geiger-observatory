package com.fcmbp.geigerobservatory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ingestion")
public class IngestionProperties {

    private long intervalMs = 2_000;
    private int historyLimit = 500;

    public long getIntervalMs() {
        return intervalMs;
    }

    public void setIntervalMs(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public int getHistoryLimit() {
        return historyLimit;
    }

    public void setHistoryLimit(int historyLimit) {
        this.historyLimit = historyLimit;
    }
}
