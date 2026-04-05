import { useState } from "react";
import type { MouseEvent } from "react";
import type { Reading } from "../lib/types";

type ReadingChartProps = {
  readings: Reading[];
};

const chartTimeFormatter = new Intl.DateTimeFormat("en-CA", {
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
  hour12: false
});

const chartTooltipFormatter = new Intl.DateTimeFormat("en-CA", {
  year: "numeric",
  month: "short",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
  hour12: false
});

export function ReadingChart({ readings }: ReadingChartProps) {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  if (readings.length === 0) {
    return <div className="panel">Waiting for readings...</div>;
  }

  const width = 100;
  const height = 100;
  const margin = {
    top: 8,
    right: 4,
    bottom: 12,
    left: 14
  };
  const plotWidth = width - margin.left - margin.right;
  const plotHeight = height - margin.top - margin.bottom;

  const readingValues = readings.map((reading) => reading.cpm);
  const rawMinCpm = Math.min(...readingValues);
  const rawMaxCpm = Math.max(...readingValues, 1);
  const rangePadding = Math.max(4, Math.ceil((rawMaxCpm - rawMinCpm) * 0.15));
  const minCpm = Math.max(0, Math.floor((rawMinCpm - rangePadding) / 5) * 5);
  const maxCpm = Math.max(minCpm + 10, Math.ceil((rawMaxCpm + rangePadding) / 5) * 5);
  const cpmRange = Math.max(1, maxCpm - minCpm);
  const yTickCount = 5;
  const yTicks = Array.from({ length: yTickCount }, (_, index) => {
    const value = minCpm + (index / (yTickCount - 1)) * cpmRange;
    return Math.round(value);
  });

  const firstTimestamp = new Date(readings[0].timestamp);
  const lastTimestamp = new Date(readings[readings.length - 1].timestamp);

  const chartPoints = readings
    .map((reading, index) => {
      const x = margin.left + (index / Math.max(readings.length - 1, 1)) * plotWidth;
      const normalizedCpm = (reading.cpm - minCpm) / cpmRange;
      const y = margin.top + (1 - normalizedCpm) * plotHeight;
      return {
        reading,
        index,
        x,
        y
      };
    });
  const points = chartPoints.map((point) => `${point.x},${point.y}`).join(" ");
  const hoveredPoint = hoveredIndex === null ? null : chartPoints[hoveredIndex];

  function handlePointerMove(event: MouseEvent<SVGSVGElement>) {
    const bounds = event.currentTarget.getBoundingClientRect();
    const pointerX = ((event.clientX - bounds.left) / bounds.width) * width;
    const clampedX = Math.min(width - margin.right, Math.max(margin.left, pointerX));
    const relativeX = (clampedX - margin.left) / Math.max(plotWidth, 1);
    const nextIndex = Math.round(relativeX * Math.max(readings.length - 1, 0));
    setHoveredIndex(Math.min(readings.length - 1, Math.max(0, nextIndex)));
  }

  return (
    <section className="panel panel--chart">
      <div className="panel__header">
        <h2>Live CPM Trend</h2>
        <span>{readings.length} samples</span>
      </div>
      <div className="chart-wrap">
        <svg
          viewBox="0 0 100 100"
          className="chart"
          preserveAspectRatio="none"
          aria-label="Live CPM chart"
          onMouseMove={handlePointerMove}
          onMouseLeave={() => setHoveredIndex(null)}
        >
          {yTicks.map((tickValue) => {
            const normalizedTick = (tickValue - minCpm) / cpmRange;
            const y = margin.top + (1 - normalizedTick) * plotHeight;

            return (
              <g key={tickValue}>
                <line
                  className="chart__grid-line"
                  x1={margin.left}
                  y1={y}
                  x2={width - margin.right}
                  y2={y}
                />
                <text className="chart__axis-label chart__axis-label--y" x={margin.left - 2} y={y}>
                  {tickValue}
                </text>
              </g>
            );
          })}

          <line className="chart__axis" x1={margin.left} y1={margin.top} x2={margin.left} y2={height - margin.bottom} />
          <line
            className="chart__axis"
            x1={margin.left}
            y1={height - margin.bottom}
            x2={width - margin.right}
            y2={height - margin.bottom}
          />
          <polyline className="chart__line" fill="none" points={points} />
          {hoveredPoint ? (
            <g>
              <line
                className="chart__crosshair"
                x1={margin.left}
                y1={hoveredPoint.y}
                x2={width - margin.right}
                y2={hoveredPoint.y}
              />
              <line
                className="chart__crosshair"
                x1={hoveredPoint.x}
                y1={margin.top}
                x2={hoveredPoint.x}
                y2={height - margin.bottom}
              />
              <circle className="chart__point" cx={hoveredPoint.x} cy={hoveredPoint.y} r={1.5} />
            </g>
          ) : null}
          <text className="chart__axis-label chart__axis-label--x" x={margin.left} y={height - 2}>
            {chartTimeFormatter.format(firstTimestamp)}
          </text>
          <text className="chart__axis-label chart__axis-label--x chart__axis-label--x-end" x={width - margin.right} y={height - 2}>
            {chartTimeFormatter.format(lastTimestamp)}
          </text>
        </svg>
        {hoveredPoint ? (
          <div
            className={`chart-tooltip ${hoveredPoint.x > width * 0.7 ? "chart-tooltip--left" : ""}`}
            style={{
              left: `${hoveredPoint.x}%`,
              top: `${hoveredPoint.y}%`
            }}
          >
            <strong>{hoveredPoint.reading.cpm} CPM</strong>
            <span>{chartTooltipFormatter.format(new Date(hoveredPoint.reading.timestamp))}</span>
          </div>
        ) : null}
      </div>
    </section>
  );
}
