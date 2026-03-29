package com.fcmbp.geigerobservatory.device;

import java.util.OptionalInt;

public interface ReadingDevice {

    OptionalInt nextCpm();

    String sourceName();

    DeviceStatus status();
}
