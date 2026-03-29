import type { Reading } from "../lib/types";

type ReadingChartProps = {
  readings: Reading[];
};

export function ReadingChart({ readings }: ReadingChartProps) {
  if (readings.length === 0) {
    return <div className="panel">Waiting for readings...</div>;
  }

  const maxCpm = Math.max(...readings.map((reading) => reading.cpm), 1);
  const points = readings
    .map((reading, index) => {
      const x = (index / Math.max(readings.length - 1, 1)) * 100;
      const y = 100 - (reading.cpm / maxCpm) * 100;
      return `${x},${y}`;
    })
    .join(" ");

  return (
    <section className="panel">
      <div className="panel__header">
        <h2>Live CPM Trend</h2>
        <span>{readings.length} samples</span>
      </div>
      <svg viewBox="0 0 100 100" className="chart" preserveAspectRatio="none" aria-label="Live CPM chart">
        <polyline fill="none" stroke="currentColor" strokeWidth="2" points={points} />
      </svg>
    </section>
  );
}
