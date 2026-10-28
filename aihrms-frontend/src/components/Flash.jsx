export default function Flash({ error, info }) {
  if (!error && !info) return null;
  return <div className={error ? 'banner err' : 'banner ok'}>{error || info}</div>;
}
