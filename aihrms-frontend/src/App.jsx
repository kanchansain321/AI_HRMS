import { useEffect, useState } from 'react';
import DashboardPage from './pages/DashboardPage';
import EmployeesPage from './pages/EmployeesPage';
import OrgPage from './pages/OrgPage';
import AttendancePage from './pages/AttendancePage';
import LeavePage from './pages/LeavePage';
import PayrollPage from './pages/PayrollPage';
import RecruitmentPage from './pages/RecruitmentPage';
import LoginPage from './pages/LoginPage';
import { clearSession, getStoredUser, getToken } from './services/api';
import { isAdmin, roleLabel } from './utils/roles';
import './App.css';

const NAV_SECTIONS = [
  {
    title: 'Main menu',
    items: [
      { id: 'dashboard', label: 'Dashboard', icon: '▣', roles: 'all' },
      { id: 'employees', label: 'Employees', icon: '☷', roles: 'admin' },
      { id: 'org', label: 'Organization', icon: '⌂', roles: 'admin' },
    ],
  },
  {
    title: 'My work',
    items: [
      { id: 'attendance', label: 'Attendance', icon: '◷', roles: 'all' },
      { id: 'leave', label: 'Leave', icon: '▤', roles: 'all' },
      { id: 'payroll', label: 'Payroll', icon: '₽', roles: 'all' },
    ],
  },
  {
    title: 'Recruitment',
    items: [{ id: 'recruitment', label: 'Recruitment', icon: '✦', roles: 'admin' }],
  },
];

function navFor(user) {
  const admin = isAdmin(user?.role);
  return NAV_SECTIONS.map((section) => ({
    ...section,
    items: section.items.filter((item) => item.roles === 'all' || admin),
  })).filter((section) => section.items.length > 0);
}

export default function App() {
  const [user, setUser] = useState(() => (getToken() ? getStoredUser() : null));
  const [tab, setTab] = useState('dashboard');
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');

  useEffect(() => {
    if (!getToken()) setUser(null);
  }, []);

  useEffect(() => {
    if (!user) return;
    const allowed = navFor(user).flatMap((s) => s.items).map((i) => i.id);
    if (!allowed.includes(tab)) setTab('dashboard');
  }, [user, tab]);

  function flash(ok, msg) {
    setError(ok ? '' : msg);
    setInfo(ok ? msg : '');
  }

  function logout() {
    clearSession();
    setUser(null);
    setTab('dashboard');
  }

  if (!user) {
    return <LoginPage onLoggedIn={setUser} />;
  }

  const admin = isAdmin(user.role);
  const sections = navFor(user);
  const allNav = sections.flatMap((s) => s.items);
  const title = allNav.find((n) => n.id === tab)?.label || 'Dashboard';

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="logo-mark">AH</div>
          <div>
            <p className="brand">AI HRMS</p>
            <h1>SmartHR</h1>
          </div>
        </div>
        <nav className="side-nav">
          {sections.map((section) => (
            <div key={section.title}>
              <div className="nav-section">{section.title}</div>
              {section.items.map((n) => (
                <button
                  key={n.id}
                  type="button"
                  className={tab === n.id ? 'nav-item active' : 'nav-item'}
                  onClick={() => setTab(n.id)}
                >
                  <span className="nav-icon">{n.icon}</span>
                  {n.label}
                </button>
              ))}
            </div>
          ))}
        </nav>
        <div className="sidebar-foot">
          <div className="user-chip">
            <div className="avatar">{(user.name || 'U').slice(0, 1).toUpperCase()}</div>
            <div>
              <strong>{user.name || 'User'}</strong>
              <div className="muted">{roleLabel(user.role)}</div>
            </div>
          </div>
          <button type="button" className="ghost logout" onClick={logout}>
            Sign out
          </button>
        </div>
      </aside>

      <div className="main">
        <header className="topbar">
          <div>
            <p className="eyebrow">
              {admin ? 'Admin' : 'Employee'} / {title}
            </p>
            <h2>{title}</h2>
          </div>
          <div className="topbar-right">
            <div className="search-pill">Search…</div>
            <button type="button" className="icon-btn" aria-label="Notifications">
              ◈
            </button>
            <div className="avatar">{(user.name || 'U').slice(0, 1).toUpperCase()}</div>
          </div>
        </header>

        <div className="content">
          {(error || info) && (
            <div className={error ? 'banner err' : 'banner ok'}>{error || info}</div>
          )}

          {tab === 'dashboard' && <DashboardPage flash={flash} goTo={setTab} user={user} />}
          {tab === 'employees' && admin && <EmployeesPage flash={flash} />}
          {tab === 'org' && admin && <OrgPage flash={flash} />}
          {tab === 'attendance' && <AttendancePage flash={flash} user={user} />}
          {tab === 'leave' && <LeavePage flash={flash} user={user} />}
          {tab === 'payroll' && <PayrollPage flash={flash} user={user} />}
          {tab === 'recruitment' && admin && <RecruitmentPage flash={flash} />}
        </div>
      </div>
    </div>
  );
}
