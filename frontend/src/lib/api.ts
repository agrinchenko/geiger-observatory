import type { AnalysisSnapshot, DeviceStatus, Reading, ReadingSummary, StoredAnomaly } from "./types";

const API_BASE = "http://localhost:8080/api";

export async function fetchSummary(): Promise<ReadingSummary> {
  const response = await fetch(`${API_BASE}/readings/summary`);
  if (!response.ok) {
    throw new Error("Failed to load summary");
  }
  return response.json();
}

export async function fetchRecentReadings(limit = 50): Promise<Reading[]> {
  const response = await fetch(`${API_BASE}/readings?limit=${limit}`);
  if (!response.ok) {
    throw new Error("Failed to load readings");
  }
  return response.json();
}

export async function fetchAnalysis(
  windowMinutes = 10,
  thresholdCpm = 40,
  minDurationSeconds = 15
): Promise<AnalysisSnapshot> {
  const response = await fetch(
    `${API_BASE}/readings/analysis?windowMinutes=${windowMinutes}&thresholdCpm=${thresholdCpm}&minDurationSeconds=${minDurationSeconds}`
  );
  if (!response.ok) {
    throw new Error("Failed to load analysis");
  }
  return response.json();
}

export async function fetchDeviceStatus(): Promise<DeviceStatus> {
  const response = await fetch(`${API_BASE}/readings/device`);
  if (!response.ok) {
    throw new Error("Failed to load device status");
  }
  return response.json();
}

export async function fetchHistoricalAnomalies(): Promise<StoredAnomaly[]> {
  const response = await fetch(`${API_BASE}/readings/anomalies`);
  if (!response.ok) {
    throw new Error("Failed to load anomaly history");
  }
  return response.json();
}

export function subscribeToReadings(onReading: (reading: Reading) => void): () => void {
  const eventSource = new EventSource(`${API_BASE}/readings/stream`);
  eventSource.addEventListener("reading", (event) => {
    onReading(JSON.parse((event as MessageEvent).data) as Reading);
  });
  return () => eventSource.close();
}
