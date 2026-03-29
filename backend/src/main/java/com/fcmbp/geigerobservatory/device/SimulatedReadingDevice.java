package com.fcmbp.geigerobservatory.device;

import com.fcmbp.geigerobservatory.config.DeviceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(prefix = "app.device", name = "mode", havingValue = "simulator", matchIfMissing = true)
public class SimulatedReadingDevice implements ReadingDevice {

    private final DeviceProperties properties;

    public SimulatedReadingDevice(DeviceProperties properties) {
        this.properties = properties;
    }

    @Override
    public OptionalInt nextCpm() {
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
                "Generating synthetic CPM readings."
        );
    }
}
