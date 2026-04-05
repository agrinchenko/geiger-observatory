import { useEffect, useState } from "react";
import { MetricCard } from "./components/MetricCard";
import { ReadingChart } from "./components/ReadingChart";
import { AnomalyTable } from "./components/AnomalyTable";
import { fetchAnalysis, fetchDeviceStatus, fetchHistoricalAnomalies, fetchRecentReadings, fetchSummary, subscribeToReadings } from "./lib/api";
import type { AnalysisSnapshot, DeviceStatus, Reading, ReadingSummary, StoredAnomaly } from "./lib/types";

const emptySummary: ReadingSummary = {
  latest: null,
  averageCpm: 0,
  maxCpm: 0,
  totalReadings: 0,
  anomalies: 0
};

const emptyAnalysis: AnalysisSnapshot = {
  windowMinutes: 10,
  thresholdCpm: 40,
  minDurationSeconds: 15,
  sampleCount: 0,
  movingAverage: 0,
  standardDeviation: 0,
  recentMax: 0,
  elevated: false,
  anomalyDetected: false,
  anomalyCount: 0,
  narrative: "Loading analysis...",
  anomalies: []
};

const emptyDeviceStatus: DeviceStatus = {
  mode: "simulator",
  connected: false,
  source: "simulator",
  detail: "Loading device status..."
};

function formatDuration(durationSeconds: number): string {
  if (durationSeconds < 60) {
    return `${durationSeconds} sec`;
  }

  return `${(durationSeconds / 60).toFixed(1)} min`;
}

export default function App() {
  const [summary, setSummary] = useState<ReadingSummary>(emptySummary);
  const [analysis, setAnalysis] = useState<AnalysisSnapshot>(emptyAnalysis);
  const [deviceStatus, setDeviceStatus] = useState<DeviceStatus>(emptyDeviceStatus);
  const [historicalAnomalies, setHistoricalAnomalies] = useState<StoredAnomaly[]>([]);
  const [readings, setReadings] = useState<Reading[]>([]);
  const [windowMinutes, setWindowMinutes] = useState<number>(10);
  const [thresholdCpm, setThresholdCpm] = useState<number>(40);
  const [minDurationSeconds, setMinDurationSeconds] = useState<number>(15);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadInitialData() {
      try {
        const [summaryData, readingsData, analysisData] = await Promise.all([
          fetchSummary(),
          fetchRecentReadings(),
          fetchAnalysis(windowMinutes, thresholdCpm, minDurationSeconds)
        ]);
        const [deviceData, anomaliesData] = await Promise.all([
          fetchDeviceStatus(),
          fetchHistoricalAnomalies()
        ]);

        if (cancelled) {
          return;
        }

        setSummary(summaryData);
        setReadings(readingsData);
        setAnalysis(analysisData);
        setDeviceStatus(deviceData);
        setHistoricalAnomalies(anomaliesData);
      } catch (loadError) {
        if (!cancelled) {
          setError(loadError instanceof Error ? loadError.message : "Unknown error");
        }
      }
    }

    loadInitialData();

    const unsubscribe = subscribeToReadings((reading) => {
      setReadings((current) => [...current, reading].slice(-50));
      setSummary((current) => {
        const totalReadings = current.totalReadings + 1;
        const nextAverage =
          totalReadings === 0
            ? reading.cpm
            : ((current.averageCpm * current.totalReadings) + reading.cpm) / totalReadings;

        return {
          latest: reading,
          averageCpm: nextAverage,
          maxCpm: Math.max(current.maxCpm, reading.cpm),
          totalReadings,
          anomalies: current.anomalies + (reading.anomalous ? 1 : 0)
        };
      });
    });

    const refreshInterval = window.setInterval(async () => {
      try {
        const [analysisData, deviceData] = await Promise.all([
          fetchAnalysis(windowMinutes, thresholdCpm, minDurationSeconds),
          fetchDeviceStatus()
        ]);
        const anomaliesData = await fetchHistoricalAnomalies();
        if (!cancelled) {
          setAnalysis(analysisData);
          setDeviceStatus(deviceData);
          setHistoricalAnomalies(anomaliesData);
        }
      } catch {
        if (!cancelled) {
          setError("Lost connection to backend endpoints");
        }
      }
    }, 5000);

    return () => {
      cancelled = true;
      unsubscribe();
      window.clearInterval(refreshInterval);
    };
  }, [windowMinutes, thresholdCpm, minDurationSeconds]);

  return (
    <main className="app-shell">
      <section className="hero">
        <div>
          <p className="eyebrow">React + Spring Boot Portfolio Project</p>
          <h1>Geiger Observatory</h1>
          <p className="hero__copy">
            Live CPM monitoring with a simulator-first backend, simple anomaly analysis, and a dashboard
            designed to be easy to demo and extend.
          </p>
        </div>
        <div className={`status-badge ${deviceStatus.connected ? "" : "status-badge--warning"}`}>
          <span className="status-badge__dot" />
          {error ? `Backend issue: ${error}` : `${deviceStatus.source} device: ${deviceStatus.detail}`}
        </div>
      </section>

      <section className="metrics-grid">
        <MetricCard
          label="Current CPM"
          value={summary.latest ? `${summary.latest.cpm}` : "--"}
          tone={summary.latest?.anomalous ? "alert" : "default"}
        />
        <MetricCard label="Device Mode" value={deviceStatus.mode} tone={deviceStatus.connected ? "default" : "alert"} />
        <MetricCard label="Average CPM" value={summary.averageCpm.toFixed(1)} />
        <MetricCard label="Peak CPM" value={`${summary.maxCpm}`} />
        <MetricCard label="Sustained Runs" value={`${analysis.anomalyCount}`} tone={analysis.anomalyDetected ? "alert" : "default"} />
      </section>

      <section className="analysis-grid">
        <article className="panel panel--feature">
          <div className="panel__header">
            <h2>Analysis Snapshot</h2>
            <span>{analysis.anomalyDetected ? "Anomaly detected" : analysis.elevated ? "Above threshold" : "Stable"}</span>
          </div>
          <div className="analysis-controls">
            <label className="window-control">
              <span>Window</span>
              <input
                type="number"
                min={1}
                max={60}
                value={windowMinutes}
                onChange={(event) => setWindowMinutes(Number(event.target.value))}
              />
              <span>minutes</span>
            </label>
            <label className="window-control">
              <span>Threshold</span>
              <input
                type="number"
                min={1}
                max={1000}
                value={thresholdCpm}
                onChange={(event) => setThresholdCpm(Number(event.target.value))}
              />
              <span>CPM</span>
            </label>
            <label className="window-control">
              <span>Minimum run</span>
              <input
                type="number"
                min={1}
                max={3600}
                value={minDurationSeconds}
                onChange={(event) => setMinDurationSeconds(Number(event.target.value))}
              />
              <span>seconds</span>
            </label>
          </div>
          <p className="analysis-copy">{analysis.narrative}</p>
          <div className="analysis-stats">
            <div>
              <span>Moving Average</span>
              <strong>{analysis.movingAverage.toFixed(1)} CPM</strong>
            </div>
            <div>
              <span>Recent Max</span>
              <strong>{analysis.recentMax} CPM</strong>
            </div>
            <div>
              <span>Std. Deviation</span>
              <strong>{analysis.standardDeviation.toFixed(2)}</strong>
            </div>
            <div>
              <span>Samples</span>
              <strong>{analysis.sampleCount}</strong>
            </div>
            <div>
              <span>Threshold</span>
              <strong>{analysis.thresholdCpm} CPM</strong>
            </div>
            <div>
              <span>Minimum Run</span>
              <strong>{analysis.minDurationSeconds} sec</strong>
            </div>
          </div>
          <div className="anomaly-list">
            {analysis.anomalies.length === 0 ? (
              <p className="analysis-copy">No sustained anomalies in the current window.</p>
            ) : (
              analysis.anomalies.map((anomaly) => (
                <article className="anomaly-item" key={anomaly.startTime}>
                  <strong>{new Date(anomaly.startTime).toLocaleString()}</strong>
                  <span>Average {anomaly.averageCpm.toFixed(1)} CPM</span>
                  <span>Duration {formatDuration(anomaly.durationSeconds)}</span>
                  <span>{anomaly.sampleCount} samples</span>
                </article>
              ))
            )}
          </div>
        </article>
        <ReadingChart readings={readings} />
      </section>

      <AnomalyTable anomalies={historicalAnomalies} />
    </main>
  );
}
