import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import { isAdmin } from '../utils/roles';
import Loader from '../components/Loader';

export default function DashboardPage({ flash, goTo, user }) {
  const admin = isAdmin(user?.role);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    employees: 0,
    departments: 0,
    today: 0,
    pendingLeaves: 0,
    resumes: 0,
    myAttendance: 0,
    myLeaves: 0,
    myPayroll: 0,
  });

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        if (admin) {
          const [emps, deps, today, pending, resumes] = await Promise.all([
            api.listEmployees().catch(() => []),
            api.listDepartments().catch(() => ({})),
            api.todayAttendance().catch(() => ({})),
            api.pendingLeaves().catch(() => ({})),
            api.listResumes().catch(() => ({})),
          ]);
          setStats((s) => ({
            ...s,
            employees: Array.isArray(emps) ? emps.length : (unwrap(emps)?.length || 0),
            departments: (unwrap(deps) || []).length || 0,
            today: (unwrap(today) || []).length || 0,
            pendingLeaves: (unwrap(pending) || []).length || 0,
            resumes: (unwrap(resumes) || []).length || 0,
          }));
          return;
        }

        const empId = user?.employeeId;
        if (!empId) return;
        const [att, leaves, pay] = await Promise.all([
          api.attendanceByEmployee(empId).catch(() => ({})),
          api.leavesByEmployee(empId).catch(() => ({})),
          api.payrollByEmployee(empId).catch(() => ({})),
        ]);
        setStats((s) => ({
          ...s,
          myAttendance: (unwrap(att) || []).length || 0,
          myLeaves: (unwrap(leaves) || []).length || 0,
          myPayroll: (unwrap(pay) || []).length || 0,
        }));
      } catch (e) {
        flash(false, e.message);
      } finally {
        setLoading(false);
      }
    })();
  }, [admin, user?.employeeId]);

  const cards = admin
    ? [
        { label: 'Employees', value: stats.employees, tab: 'employees', icon: '☷', trend: 'Active headcount' },
        { label: 'Departments', value: stats.departments, tab: 'org', icon: '⌂', trend: 'Org structure' },
        { label: 'Present today', value: stats.today, tab: 'attendance', icon: '◷', trend: 'Checked in' },
        { label: 'Pending leaves', value: stats.pendingLeaves, tab: 'leave', icon: '▤', trend: 'Awaiting approval' },
        { label: 'Resumes', value: stats.resumes, tab: 'recruitment', icon: '✦', trend: 'In pipeline' },
      ]
    : [
        { label: 'My attendance', value: stats.myAttendance, tab: 'attendance', icon: '◷', trend: 'Your records' },
        { label: 'My leaves', value: stats.myLeaves, tab: 'leave', icon: '▤', trend: 'Your requests' },
        { label: 'My payroll', value: stats.myPayroll, tab: 'payroll', icon: '₽', trend: 'Your payslips' },
      ];

  return (
    <div className="split" style={{ gridTemplateColumns: '1fr' }}>
      <section>
        <h2 style={{ margin: '0 0 0.35rem', fontSize: '1.15rem' }}>
          Welcome{user?.name ? `, ${user.name}` : ''}
        </h2>
        <p className="lead" style={{ marginBottom: '1.1rem' }}>
          {admin
            ? 'Admin overview of AI HRMS modules.'
            : 'Your personal attendance, leave, and payroll.'}
        </p>
        {loading ? (
          <Loader label="Loading dashboard…" />
        ) : (
          <div className="stat-grid">
            {cards.map((c) => (
              <button key={c.label} type="button" className="stat-card" onClick={() => goTo(c.tab)}>
                <span className="stat-label">{c.label}</span>
                <span className="stat-value">{c.value}</span>
                <span className="stat-trend">{c.trend}</span>
                <span className="stat-icon" aria-hidden>
                  {c.icon}
                </span>
              </button>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
