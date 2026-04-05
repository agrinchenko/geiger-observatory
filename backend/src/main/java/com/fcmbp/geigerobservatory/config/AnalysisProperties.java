package com.fcmbp.geigerobservatory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.analysis")
public class AnalysisProperties {

    private int archiveWindowMinutes = 60;
    private int thresholdCpm = 40;
    private int minDurationSeconds = 15;

    public int getArchiveWindowMinutes() {
        return archiveWindowMinutes;
    }

    public void setArchiveWindowMinutes(int archiveWindowMinutes) {
        this.archiveWindowMinutes = archiveWindowMinutes;
    }

    public int getThresholdCpm() {
        return thresholdCpm;
    }

    public void setThresholdCpm(int thresholdCpm) {
        this.thresholdCpm = thresholdCpm;
    }

    public int getMinDurationSeconds() {
        return minDurationSeconds;
    }

    public void setMinDurationSeconds(int minDurationSeconds) {
        this.minDurationSeconds = minDurationSeconds;
    }
}
