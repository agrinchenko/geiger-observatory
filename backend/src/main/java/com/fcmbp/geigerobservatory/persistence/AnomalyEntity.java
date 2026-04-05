package com.fcmbp.geigerobservatory.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
        name = "anomalies",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_anomaly_start_threshold_duration",
                columnNames = {"start_time", "threshold_cpm", "min_duration_seconds"}
        )
)
public class AnomalyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "average_cpm", nullable = false)
    private double averageCpm;

    @Column(name = "duration_seconds", nullable = false)
    private long durationSeconds;

    @Column(name = "sample_count", nullable = false)
    private int sampleCount;

    @Column(name = "threshold_cpm", nullable = false)
    private int thresholdCpm;

    @Column(name = "min_duration_seconds", nullable = false)
    private int minDurationSeconds;

    @Column(name = "ai_comment", nullable = false)
    private String aiComment = "";

    public Long getId() {
        return id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public double getAverageCpm() {
        return averageCpm;
    }

    public void setAverageCpm(double averageCpm) {
        this.averageCpm = averageCpm;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
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

    public String getAiComment() {
        return aiComment;
    }

    public void setAiComment(String aiComment) {
        this.aiComment = aiComment;
    }
}
