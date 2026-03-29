package com.fcmbp.geigerobservatory.device;

public record DeviceStatus(
        String mode,
        boolean connected,
        String source,
        String detail
) {
}
