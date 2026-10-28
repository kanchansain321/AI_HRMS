/** Title Case for headings, nav, and table headers. */
export function titleCase(value) {
  if (value == null || value === '') return '';
  return String(value)
    .trim()
    .replace(/[_-]+/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (c) => c.toUpperCase());
}

/** Human label for API enums (PENDING → Pending, CASUAL → Casual). */
export function statusLabel(value) {
  if (value == null || value === '') return '—';
  return titleCase(String(value));
}
