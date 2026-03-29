import type { Reading } from "../lib/types";

type ReadingTableProps = {
  readings: Reading[];
};

export function ReadingTable({ readings }: ReadingTableProps) {
  return (
    <section className="panel">
      <div className="panel__header">
        <h2>Recent Readings</h2>
        <span>Latest {readings.length}</span>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Time</th>
              <th>CPM</th>
              <th>Source</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {[...readings].reverse().map((reading) => (
              <tr key={reading.timestamp}>
                <td>{new Date(reading.timestamp).toLocaleTimeString()}</td>
                <td>{reading.cpm}</td>
                <td>{reading.source}</td>
                <td>{reading.anomalous ? "Spike" : "Normal"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
