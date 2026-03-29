package com.fcmbp.geigerobservatory.device;

import com.fazecast.jSerialComm.SerialPort;
import com.fcmbp.geigerobservatory.config.DeviceProperties;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;

@Component
@ConditionalOnProperty(prefix = "app.device", name = "mode", havingValue = "serial")
public class SerialReadingDevice implements ReadingDevice {

    private final DeviceProperties properties;
    private volatile SerialPort serialPort;
    private volatile String lastDetail = "Not connected";

    public SerialReadingDevice(DeviceProperties properties) {
        this.properties = properties;
    }

    @Override
    public OptionalInt nextCpm() {
        if (!ensureConnected()) {
            return OptionalInt.empty();
        }

        byte[] command = properties.getCommand().getBytes(StandardCharsets.UTF_8);
        int written = serialPort.writeBytes(command, command.length);
        if (written != command.length) {
            lastDetail = "Failed to write CPM command to serial device.";
            closePort();
            return OptionalInt.empty();
        }

        byte[] response = new byte[2];
        int bytesRead = serialPort.readBytes(response, response.length);
        if (bytesRead != response.length) {
            lastDetail = "Received " + bytesRead + " bytes instead of 2 from the serial device.";
            closePort();
            return OptionalInt.empty();
        }

        lastDetail = "Connected to " + properties.getCommPort() + " at " + properties.getBaudRate() + " baud.";
        return OptionalInt.of(getUnsignedShort(response));
    }

    @Override
    public String sourceName() {
        return "serial";
    }

    @Override
    public DeviceStatus status() {
        boolean connected = serialPort != null && serialPort.isOpen();
        return new DeviceStatus(properties.getMode(), connected, sourceName(), lastDetail);
    }

    private synchronized boolean ensureConnected() {
        if (serialPort != null && serialPort.isOpen()) {
            return true;
        }

        try {
            SerialPort candidate = SerialPort.getCommPort(properties.getCommPort());
            candidate.setBaudRate(properties.getBaudRate());
            candidate.setComPortTimeouts(
                    SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                    properties.getReadTimeoutMs(),
                    properties.getWriteTimeoutMs()
            );

            if (!candidate.openPort()) {
                lastDetail = "Unable to open serial port " + properties.getCommPort() + ".";
                return false;
            }

            serialPort = candidate;
            lastDetail = "Connected to " + properties.getCommPort() + ".";
            return true;
        } catch (RuntimeException ex) {
            lastDetail = "Serial initialization failed: " + ex.getMessage();
            closePort();
            return false;
        }
    }

    private int getUnsignedShort(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
    }

    private synchronized void closePort() {
        if (serialPort != null) {
            serialPort.closePort();
            serialPort = null;
        }
    }

    @PreDestroy
    void shutdown() {
        closePort();
    }
}
