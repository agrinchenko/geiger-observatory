package com.fcmbp.geigerobservatory.analysis;

import com.fcmbp.geigerobservatory.model.DetectedAnomaly;
import com.fcmbp.geigerobservatory.model.StoredAnomalyResponse;
import com.fcmbp.geigerobservatory.persistence.AnomalyEntity;
import com.fcmbp.geigerobservatory.persistence.AnomalyJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnomalyArchiveService {

    private final AnomalyJpaRepository anomalyJpaRepository;

    public AnomalyArchiveService(AnomalyJpaRepository anomalyJpaRepository) {
        this.anomalyJpaRepository = anomalyJpaRepository;
    }

    public void syncDetectedAnomalies(List<DetectedAnomaly> anomalies, int thresholdCpm, int minDurationSeconds) {
        for (DetectedAnomaly anomaly : anomalies) {
            AnomalyEntity entity = anomalyJpaRepository
                    .findByStartTimeAndThresholdCpmAndMinDurationSeconds(
                            anomaly.startTime(),
                            thresholdCpm,
                            minDurationSeconds
                    )
                    .orElseGet(AnomalyEntity::new);

            entity.setStartTime(anomaly.startTime());
            entity.setEndTime(anomaly.endTime());
            entity.setAverageCpm(anomaly.averageCpm());
            entity.setDurationSeconds(anomaly.durationSeconds());
            entity.setSampleCount(anomaly.sampleCount());
            entity.setThresholdCpm(thresholdCpm);
            entity.setMinDurationSeconds(minDurationSeconds);
            if (entity.getAiComment() == null) {
                entity.setAiComment("");
            }

            anomalyJpaRepository.save(entity);
        }
    }

    public List<StoredAnomalyResponse> listHistorical() {
        return anomalyJpaRepository.findAll().stream()
                .sorted((left, right) -> right.getStartTime().compareTo(left.getStartTime()))
                .map(entity -> new StoredAnomalyResponse(
                        entity.getId(),
                        entity.getStartTime(),
                        entity.getEndTime(),
                        entity.getAverageCpm(),
                        entity.getDurationSeconds(),
                        entity.getSampleCount(),
                        entity.getThresholdCpm(),
                        entity.getMinDurationSeconds(),
                        entity.getAiComment()
                ))
                .toList();
    }
}
