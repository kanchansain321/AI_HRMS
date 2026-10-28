import { useState } from 'react';
import { api, saveSession, unwrap } from '../services/api';

export default function LoginPage({ onLoggedIn }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function onSubmit(e) {
    e.preventDefault();
    setBusy(true);
    setError('');
    try {
      const res = await api.login(email.trim(), password);
      const data = unwrap(res);
      if (!data?.token) {
        throw new Error(res?.message || 'Login failed');
      }
      saveSession(data);
      onLoggedIn(data);
    } catch (err) {
      setError(err.message || 'Login failed');
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="login-shell">
      <section className="login-hero">
        <p className="brand">AI HRMS</p>
        <h1>Manage people, payroll &amp; hiring in one place</h1>
        <p>
          A clean HR workspace with attendance, leave, payroll, and AI recruitment —
          inspired by modern admin dashboards.
        </p>
        <ul className="login-points">
          <li>Live attendance &amp; leave approvals</li>
          <li>Payroll overview at a glance</li>
          <li>AI-assisted resume screening</li>
        </ul>
      </section>

      <section className="login-card">
        <div className="logo-mark" style={{ marginBottom: '1rem' }}>
          AH
        </div>
        <h2>Sign in</h2>
        <p className="muted">
          Use your employee email and password. If the database is empty, use the bootstrap
          admin from server <code>auth.bootstrap</code> settings.
        </p>
        {error && <div className="banner err">{error}</div>}
        <form className="login-form" onSubmit={onSubmit}>
          <label>
            Email address
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="hr@company.com"
              required
              autoComplete="username"
            />
          </label>
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              autoComplete="current-password"
            />
          </label>
          <button type="submit" disabled={busy}>
            {busy ? (
              <span className="btn-busy">
                <span className="loader-spinner sm" aria-hidden />
                Signing in…
              </span>
            ) : (
              'Sign in'
            )}
          </button>
        </form>
      </section>
    </div>
  );
}
