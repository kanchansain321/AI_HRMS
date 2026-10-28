import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import { isAdmin } from '../utils/roles';
import { statusLabel } from '../utils/text';
import Loader from '../components/Loader';

export default function AttendancePage({ flash, user }) {
  const admin = isAdmin(user?.role);
  const selfId = user?.employeeId;
  const [rows, setRows] = useState([]);
  const [empId, setEmpId] = useState(admin ? '' : String(selfId || ''));
  const [view, setView] = useState(admin ? 'today' : 'mine');
  const [loading, setLoading] = useState(true);

  async function load(kind = view) {
    setLoading(true);
    try {
      let res;
      if (admin) {
        if (kind === 'all') res = await api.listAttendance();
        else if (kind === 'employee' && empId) res = await api.attendanceByEmployee(Number(empId));
        else res = await api.todayAttendance();
      } else {
        if (!selfId) {
          setRows([]);
          return;
        }
        res = await api.attendanceByEmployee(selfId);
      }
      setRows(unwrap(res) || []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load(admin ? 'today' : 'mine');
  }, [admin, selfId]);

  async function checkIn(e) {
    e.preventDefault();
    try {
      const now = new Date().toISOString();
      const targetId = admin ? Number(empId) : selfId;
      await api.checkIn({
        employeeId: targetId,
        date: now,
        checkIn: now,
        status: 'PRESENT',
      });
      flash(true, 'Checked in');
      await load(view);
    } catch (err) {
      flash(false, err.message);
    }
  }

  async function markAbsent(e) {
    e.preventDefault();
    if (!admin) return;
    try {
      await api.markAbsent({
        employeeId: Number(empId),
        date: new Date().toISOString(),
        status: 'ABSENT',
      });
      flash(true, 'Marked absent');
      await load(view);
    } catch (err) {
      flash(false, err.message);
    }
  }

  async function checkOut(id) {
    try {
      await api.checkOut(id);
      flash(true, 'Checked out');
      await load(view);
    } catch (err) {
      flash(false, err.message);
    }
  }

  async function remove(id) {
    if (!admin) return;
    try {
      await api.deleteAttendance(id);
      flash(true, 'Deleted');
      await load(view);
    } catch (err) {
      flash(false, err.message);
    }
  }

  return (
    <section className="panel">
      <h2>{admin ? 'Attendance' : 'My attendance'}</h2>
      <form className="inline" onSubmit={checkIn}>
        {admin ? (
          <input
            placeholder="Employee ID"
            value={empId}
            onChange={(e) => setEmpId(e.target.value)}
            required
          />
        ) : (
          <span className="muted">Employee #{selfId || '—'}</span>
        )}
        <button type="submit" disabled={!admin && !selfId}>
          Check in
        </button>
        {admin && (
          <button type="button" className="ghost" onClick={markAbsent}>
            Mark absent
          </button>
        )}
      </form>
      {admin && (
        <div className="inline">
          <button
            type="button"
            onClick={() => {
              setView('today');
              load('today');
            }}
          >
            Today
          </button>
          <button
            type="button"
            onClick={() => {
              setView('all');
              load('all');
            }}
          >
            All
          </button>
          <button
            type="button"
            onClick={() => {
              setView('employee');
              load('employee');
            }}
          >
            By employee
          </button>
        </div>
      )}
      {loading ? (
        <Loader label="Loading attendance…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee</th>
                <th>Date</th>
                <th>Check in</th>
                <th>Check out</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {(rows || []).map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.employeeId || r.employeeName || '—'}</td>
                  <td>{r.date ? String(r.date).slice(0, 10) : '—'}</td>
                  <td>{r.checkIn ? String(r.checkIn) : '—'}</td>
                  <td>{r.checkOut ? String(r.checkOut) : '—'}</td>
                  <td><span className="status-pill">{statusLabel(r.status)}</span></td>
                  <td className="row-actions">
                    {!r.checkOut && r.status !== 'ABSENT' && (
                      <button type="button" onClick={() => checkOut(r.id)}>
                        Check out
                      </button>
                    )}
                    {admin && (
                      <button type="button" className="ghost" onClick={() => remove(r.id)}>
                        Delete
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
