type MetricCardProps = {
  label: string;
  value: string;
  tone?: "default" | "alert";
};

export function MetricCard({ label, value, tone = "default" }: MetricCardProps) {
  return (
    <article className={`metric-card metric-card--${tone}`}>
      <p className="metric-card__label">{label}</p>
      <strong className="metric-card__value">{value}</strong>
    </article>
  );
}
