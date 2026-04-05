import type { StoredAnomaly } from "../lib/types";

type AnomalyTableProps = {
  anomalies: StoredAnomaly[];
};

const torontoDateFormatter = new Intl.DateTimeFormat("en-CA", {
  year: "numeric",
  month: "short",
  day: "2-digit",
  timeZone: "America/Toronto"
});

const torontoTimeFormatter = new Intl.DateTimeFormat("en-CA", {
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
  hour12: false,
  timeZone: "America/Toronto"
});

const utcTimeFormatter = new Intl.DateTimeFormat("en-CA", {
  year: "numeric",
  month: "short",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit",
  second: "2-digit",
  hour12: false,
  timeZone: "UTC"
});

function formatDuration(durationSeconds: number): string {
  if (durationSeconds < 60) {
    return `${durationSeconds} sec`;
  }

  return `${(durationSeconds / 60).toFixed(1)} min`;
}

export function AnomalyTable({ anomalies }: AnomalyTableProps) {
  return (
    <section className="panel">
      <div className="panel__header">
        <h2>Detected Anomalies</h2>
        <span>{anomalies.length} archived</span>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Start Date</th>
              <th>Toronto Time</th>
              <th>UTC Time</th>
              <th>Duration</th>
              <th>Average CPM</th>
              <th>AI Comment</th>
            </tr>
          </thead>
          <tbody>
            {anomalies.length === 0 ? (
              <tr>
                <td colSpan={6} className="table-empty">No archived anomalies yet.</td>
              </tr>
            ) : (
              anomalies.map((anomaly) => {
                const start = new Date(anomaly.startTime);

                return (
                  <tr key={anomaly.id}>
                    <td>{torontoDateFormatter.format(start)}</td>
                    <td>{torontoTimeFormatter.format(start)}</td>
                    <td>{utcTimeFormatter.format(start)} UTC</td>
                    <td>{formatDuration(anomaly.durationSeconds)}</td>
                    <td>{anomaly.averageCpm.toFixed(1)}</td>
                    <td>{anomaly.aiComment || ""}</td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
