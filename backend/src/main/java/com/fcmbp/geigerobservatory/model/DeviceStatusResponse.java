package com.fcmbp.geigerobservatory.model;

public record DeviceStatusResponse(
        String mode,
        boolean connected,
        String source,
        String detail
) {
}
