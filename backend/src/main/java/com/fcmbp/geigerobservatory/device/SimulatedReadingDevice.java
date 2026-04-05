package com.fcmbp.geigerobservatory.device;

import com.fcmbp.geigerobservatory.config.DeviceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Component
@ConditionalOnProperty(prefix = "app.device", name = "mode", havingValue = "simulator", matchIfMissing = true)
public class SimulatedReadingDevice implements ReadingDevice {

    private final DeviceProperties properties;
    private final AtomicLong readingCounter = new AtomicLong();

    public SimulatedReadingDevice(DeviceProperties properties) {
        this.properties = properties;
    }

    @Override
    public OptionalInt nextCpm() {
        long readingIndex = readingCounter.getAndIncrement();
        if (properties.isTestAnomalyEnabled() && isWithinForcedAnomalyWindow(readingIndex)) {
            return OptionalInt.of(properties.getTestAnomalyCpm());
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int noise = random.nextInt(-properties.getNoiseBand(), properties.getNoiseBand() + 1);
        int spike = random.nextDouble() < properties.getSpikeProbability()
                ? random.nextInt(properties.getSpikeMagnitude() / 2, properties.getSpikeMagnitude() + 1)
                : 0;
        return OptionalInt.of(Math.max(0, properties.getBaselineCpm() + noise + spike));
    }

    @Override
    public String sourceName() {
        return "simulator";
    }

    @Override
    public DeviceStatus status() {
        return new DeviceStatus(
                properties.getMode(),
                true,
                sourceName(),
                properties.isTestAnomalyEnabled()
                        ? String.format(
                        "Generating synthetic CPM readings with a forced anomaly after %d samples for %d samples at %d CPM.",
                        properties.getTestAnomalyStartAfterReadings(),
                        properties.getTestAnomalyDurationReadings(),
                        properties.getTestAnomalyCpm()
                )
                        : "Generating synthetic CPM readings."
        );
    }

    private boolean isWithinForcedAnomalyWindow(long readingIndex) {
        long start = Math.max(0, properties.getTestAnomalyStartAfterReadings());
        long duration = Math.max(0, properties.getTestAnomalyDurationReadings());
        long endExclusive = start + duration;
        return duration > 0 && readingIndex >= start && readingIndex < endExclusive;
    }
}
