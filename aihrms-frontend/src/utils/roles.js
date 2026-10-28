export function canonicalRole(role) {
  if (!role) return 'EMPLOYEE';
  const u = String(role).trim().toUpperCase().replace(/[\s-]+/g, '_');
  if (u === 'SUPER_ADMIN' || u === 'SUPERADMIN') return 'SUPER_ADMIN';
  if (u === 'HR' || u === 'HR_ADMIN') return 'HR';
  if (u.includes('ADMIN')) return 'ADMIN';
  return 'EMPLOYEE';
}

export function isAdmin(role) {
  const c = canonicalRole(role);
  return c === 'SUPER_ADMIN' || c === 'ADMIN' || c === 'HR';
}

export function roleLabel(role) {
  const c = canonicalRole(role);
  if (c === 'SUPER_ADMIN') return 'Super Admin';
  if (c === 'ADMIN') return 'Admin';
  if (c === 'HR') return 'HR';
  return 'Employee';
}
