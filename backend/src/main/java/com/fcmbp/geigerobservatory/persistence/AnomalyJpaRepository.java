package com.fcmbp.geigerobservatory.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AnomalyJpaRepository extends JpaRepository<AnomalyEntity, Long> {

    Optional<AnomalyEntity> findByStartTimeAndThresholdCpmAndMinDurationSeconds(
            Instant startTime,
            int thresholdCpm,
            int minDurationSeconds
    );
}
