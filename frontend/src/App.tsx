import { useEffect, useState } from "react";
import { MetricCard } from "./components/MetricCard";
import { ReadingChart } from "./components/ReadingChart";
import { ReadingTable } from "./components/ReadingTable";
import { fetchAnalysis, fetchDeviceStatus, fetchRecentReadings, fetchSummary, subscribeToReadings } from "./lib/api";
import type { AnalysisSnapshot, DeviceStatus, Reading, ReadingSummary } from "./lib/types";

const emptySummary: ReadingSummary = {
  latest: null,
  averageCpm: 0,
  maxCpm: 0,
  totalReadings: 0,
  anomalies: 0
};

const emptyAnalysis: AnalysisSnapshot = {
  movingAverage: 0,
  recentMax: 0,
  elevated: false,
  narrative: "Loading analysis..."
};

const emptyDeviceStatus: DeviceStatus = {
  mode: "simulator",
  connected: false,
  source: "simulator",
  detail: "Loading device status..."
};

export default function App() {
  const [summary, setSummary] = useState<ReadingSummary>(emptySummary);
  const [analysis, setAnalysis] = useState<AnalysisSnapshot>(emptyAnalysis);
  const [deviceStatus, setDeviceStatus] = useState<DeviceStatus>(emptyDeviceStatus);
  const [readings, setReadings] = useState<Reading[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadInitialData() {
      try {
        const [summaryData, readingsData, analysisData] = await Promise.all([
          fetchSummary(),
          fetchRecentReadings(),
          fetchAnalysis()
        ]);
        const deviceData = await fetchDeviceStatus();

        if (cancelled) {
          return;
        }

        setSummary(summaryData);
        setReadings(readingsData);
        setAnalysis(analysisData);
        setDeviceStatus(deviceData);
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
          fetchAnalysis(),
          fetchDeviceStatus()
        ]);
        if (!cancelled) {
          setAnalysis(analysisData);
          setDeviceStatus(deviceData);
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
  }, []);

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
        <MetricCard label="Anomalies" value={`${summary.anomalies}`} tone={analysis.elevated ? "alert" : "default"} />
      </section>

      <section className="analysis-grid">
        <article className="panel panel--feature">
          <div className="panel__header">
            <h2>Analysis Snapshot</h2>
            <span>{analysis.elevated ? "Elevated" : "Stable"}</span>
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
          </div>
        </article>
        <ReadingChart readings={readings} />
      </section>

      <ReadingTable readings={readings} />
    </main>
  );
}
