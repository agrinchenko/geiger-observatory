package com.fcmbp.geigerobservatory.device;

import com.fcmbp.geigerobservatory.config.DeviceProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatedReadingDeviceTests {

    @Test
    void emitsForcedAnomalyWindowWhenEnabled() {
        DeviceProperties properties = new DeviceProperties();
        properties.setBaselineCpm(18);
        properties.setNoiseBand(0);
        properties.setSpikeProbability(0.0);
        properties.setSpikeMagnitude(0);
        properties.setTestAnomalyEnabled(true);
        properties.setTestAnomalyStartAfterReadings(2);
        properties.setTestAnomalyDurationReadings(3);
        properties.setTestAnomalyCpm(48);

        SimulatedReadingDevice device = new SimulatedReadingDevice(properties);

        assertEquals(18, device.nextCpm().orElseThrow());
        assertEquals(18, device.nextCpm().orElseThrow());
        assertEquals(48, device.nextCpm().orElseThrow());
        assertEquals(48, device.nextCpm().orElseThrow());
        assertEquals(48, device.nextCpm().orElseThrow());
        assertEquals(18, device.nextCpm().orElseThrow());
    }
}
