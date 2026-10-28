export default function Loader({ label = 'Loading…', overlay = false, compact = false }) {
  const body = (
    <div className={`loader${compact ? ' compact' : ''}`} role="status" aria-live="polite">
      <span className="loader-spinner" aria-hidden />
      {label ? <span className="loader-label">{label}</span> : null}
    </div>
  );
  if (overlay) {
    return <div className="loader-overlay">{body}</div>;
  }
  return body;
}

export function BusyButton({
  busy,
  busyText,
  children,
  className = '',
  type = 'button',
  disabled,
  ...rest
}) {
  return (
    <button type={type} className={className} disabled={busy || disabled} {...rest}>
      {busy ? (
        <span className="btn-busy">
          <span className="loader-spinner sm" aria-hidden />
          {busyText || children}
        </span>
      ) : (
        children
      )}
    </button>
  );
}
