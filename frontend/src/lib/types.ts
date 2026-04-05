export type Reading = {
  timestamp: string;
  cpm: number;
  source: string;
  anomalous: boolean;
};

export type ReadingSummary = {
  latest: Reading | null;
  averageCpm: number;
  maxCpm: number;
  totalReadings: number;
  anomalies: number;
};

export type DetectedAnomaly = {
  startTime: string;
  endTime: string;
  averageCpm: number;
  durationSeconds: number;
  sampleCount: number;
};

export type StoredAnomaly = {
  id: number;
  startTime: string;
  endTime: string;
  averageCpm: number;
  durationSeconds: number;
  sampleCount: number;
  thresholdCpm: number;
  minDurationSeconds: number;
  aiComment: string;
};

export type AnalysisSnapshot = {
  windowMinutes: number;
  thresholdCpm: number;
  minDurationSeconds: number;
  sampleCount: number;
  movingAverage: number;
  standardDeviation: number;
  recentMax: number;
  elevated: boolean;
  anomalyDetected: boolean;
  anomalyCount: number;
  narrative: string;
  anomalies: DetectedAnomaly[];
};

export type DeviceStatus = {
  mode: string;
  connected: boolean;
  source: string;
  detail: string;
};
