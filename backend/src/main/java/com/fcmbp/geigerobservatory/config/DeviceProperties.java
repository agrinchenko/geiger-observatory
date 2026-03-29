package com.fcmbp.geigerobservatory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.device")
public class DeviceProperties {

    private String mode = "simulator";
    private String commPort = "tty.usbserial-1420";
    private int baudRate = 115200;
    private int readTimeoutMs = 5_000;
    private int writeTimeoutMs = 5_000;
    private String command = "<GETCPM>>";
    private int baselineCpm = 18;
    private int noiseBand = 6;
    private double spikeProbability = 0.07;
    private int spikeMagnitude = 60;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCommPort() {
        return commPort;
    }

    public void setCommPort(String commPort) {
        this.commPort = commPort;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public int getWriteTimeoutMs() {
        return writeTimeoutMs;
    }

    public void setWriteTimeoutMs(int writeTimeoutMs) {
        this.writeTimeoutMs = writeTimeoutMs;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getBaselineCpm() {
        return baselineCpm;
    }

    public void setBaselineCpm(int baselineCpm) {
        this.baselineCpm = baselineCpm;
    }

    public int getNoiseBand() {
        return noiseBand;
    }

    public void setNoiseBand(int noiseBand) {
        this.noiseBand = noiseBand;
    }

    public double getSpikeProbability() {
        return spikeProbability;
    }

    public void setSpikeProbability(double spikeProbability) {
        this.spikeProbability = spikeProbability;
    }

    public int getSpikeMagnitude() {
        return spikeMagnitude;
    }

    public void setSpikeMagnitude(int spikeMagnitude) {
        this.spikeMagnitude = spikeMagnitude;
    }
}
