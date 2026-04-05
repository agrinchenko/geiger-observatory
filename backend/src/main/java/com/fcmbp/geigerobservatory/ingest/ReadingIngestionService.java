package com.fcmbp.geigerobservatory.ingest;

import com.fcmbp.geigerobservatory.analysis.ReadingAnalyzer;
import com.fcmbp.geigerobservatory.analysis.AnomalyArchiveService;
import com.fcmbp.geigerobservatory.config.AnalysisProperties;
import com.fcmbp.geigerobservatory.config.IngestionProperties;
import com.fcmbp.geigerobservatory.device.DeviceStatus;
import com.fcmbp.geigerobservatory.device.ReadingDevice;
import com.fcmbp.geigerobservatory.model.DeviceStatusResponse;
import com.fcmbp.geigerobservatory.model.AnalysisSnapshot;
import com.fcmbp.geigerobservatory.model.Reading;
import com.fcmbp.geigerobservatory.model.ReadingSummary;
import com.fcmbp.geigerobservatory.model.StoredAnomalyResponse;
import com.fcmbp.geigerobservatory.repository.ReadingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReadingIngestionService {

    private final ReadingDevice readingDevice;
    private final ReadingRepository readingRepository;
    private final ReadingAnalyzer readingAnalyzer;
    private final AnomalyArchiveService anomalyArchiveService;
    private final AnalysisProperties analysisProperties;
    private final IngestionProperties ingestionProperties;
    private final LiveReadingPublisher liveReadingPublisher;

    public ReadingIngestionService(
            ReadingDevice readingDevice,
            ReadingRepository readingRepository,
            ReadingAnalyzer readingAnalyzer,
            AnomalyArchiveService anomalyArchiveService,
            AnalysisProperties analysisProperties,
            IngestionProperties ingestionProperties,
            LiveReadingPublisher liveReadingPublisher
    ) {
        this.readingDevice = readingDevice;
        this.readingRepository = readingRepository;
        this.readingAnalyzer = readingAnalyzer;
        this.anomalyArchiveService = anomalyArchiveService;
        this.analysisProperties = analysisProperties;
        this.ingestionProperties = ingestionProperties;
        this.liveReadingPublisher = liveReadingPublisher;
    }

    @PostConstruct
    void warmup() {
        pollDevice();
    }

    @Scheduled(fixedDelayString = "${app.ingestion.interval-ms}")
    public void pollDevice() {
        List<Reading> recent = readingRepository.findRecent(30);
        var cpm = readingDevice.nextCpm();
        if (cpm.isEmpty()) {
            return;
        }

        int cpmValue = cpm.getAsInt();
        boolean anomalous = readingAnalyzer.isAnomalous(cpmValue, recent);
        Reading reading = new Reading(Instant.now(), cpmValue, readingDevice.sourceName(), anomalous);
        readingRepository.save(reading, ingestionProperties.getHistoryLimit());
        archiveAnomalies();
        liveReadingPublisher.publish(reading);
    }

    public List<Reading> recentReadings(int limit) {
        return readingRepository.findRecent(limit);
    }

    public ReadingSummary summary() {
        List<Reading> recent = readingRepository.findRecent(ingestionProperties.getHistoryLimit());
        Reading latest = readingRepository.findLatest().orElse(null);
        double average = recent.stream().mapToInt(Reading::cpm).average().orElse(0.0);
        int max = recent.stream().mapToInt(Reading::cpm).max().orElse(0);
        return new ReadingSummary(
                latest,
                average,
                max,
                readingRepository.count(),
                readingRepository.countAnomalies()
        );
    }

    public AnalysisSnapshot analysis(int windowMinutes, int thresholdCpm, int minDurationSeconds) {
        Instant cutoff = Instant.now().minus(windowMinutes, ChronoUnit.MINUTES);
        return readingAnalyzer.snapshot(
                readingRepository.findSince(cutoff),
                windowMinutes,
                thresholdCpm,
                minDurationSeconds
        );
    }

    public DeviceStatusResponse deviceStatus() {
        DeviceStatus status = readingDevice.status();
        return new DeviceStatusResponse(status.mode(), status.connected(), status.source(), status.detail());
    }

    public List<StoredAnomalyResponse> historicalAnomalies() {
        return anomalyArchiveService.listHistorical();
    }

    private void archiveAnomalies() {
        Instant cutoff = Instant.now().minus(analysisProperties.getArchiveWindowMinutes(), ChronoUnit.MINUTES);
        List<Reading> recentWindow = readingRepository.findSince(cutoff);
        anomalyArchiveService.syncDetectedAnomalies(
                readingAnalyzer.detectAnomalies(
                        recentWindow,
                        analysisProperties.getThresholdCpm(),
                        analysisProperties.getMinDurationSeconds()
                ),
                analysisProperties.getThresholdCpm(),
                analysisProperties.getMinDurationSeconds()
        );
    }
}
