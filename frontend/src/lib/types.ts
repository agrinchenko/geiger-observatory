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

export type AnalysisSnapshot = {
  movingAverage: number;
  recentMax: number;
  elevated: boolean;
  narrative: string;
};

export type DeviceStatus = {
  mode: string;
  connected: boolean;
  source: string;
  detail: string;
};
