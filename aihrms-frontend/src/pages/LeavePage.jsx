import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import { isAdmin } from '../utils/roles';
import { statusLabel } from '../utils/text';
import Loader from '../components/Loader';

export default function LeavePage({ flash, user }) {
  const admin = isAdmin(user?.role);
  const selfId = user?.employeeId;
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({
    employeeId: admin ? '' : String(selfId || ''),
    leaveType: 'CASUAL',
    startDate: '',
    endDate: '',
    reason: '',
  });

  async function load(kind = admin ? 'all' : 'mine') {
    setLoading(true);
    try {
      let res;
      if (admin) {
        res = kind === 'pending' ? await api.pendingLeaves() : await api.listLeaves();
      } else {
        if (!selfId) {
          setRows([]);
          return;
        }
        res = await api.leavesByEmployee(selfId);
      }
      setRows(unwrap(res) || []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load(admin ? 'all' : 'mine');
  }, [admin, selfId]);

  async function apply(e) {
    e.preventDefault();
    try {
      await api.applyLeave({
        employeeId: admin ? Number(form.employeeId) : selfId,
        leaveType: form.leaveType,
        startDate: form.startDate,
        endDate: form.endDate,
        reason: form.reason,
      });
      flash(true, 'Leave applied');
      setForm({
        employeeId: admin ? '' : String(selfId || ''),
        leaveType: 'CASUAL',
        startDate: '',
        endDate: '',
        reason: '',
      });
      await load(admin ? 'all' : 'mine');
    } catch (err) {
      flash(false, err.message);
    }
  }

  async function act(kind, id) {
    try {
      if (kind === 'approve') await api.approveLeave(id);
      if (kind === 'reject') await api.rejectLeave(id);
      if (kind === 'cancel') await api.cancelLeave(id);
      const messages = {
        approve: 'Leave approved',
        reject: 'Leave rejected',
        cancel: 'Leave cancelled',
      };
      flash(true, messages[kind] || 'Leave updated');
      await load(admin ? 'all' : 'mine');
    } catch (err) {
      flash(false, err.message);
    }
  }

  return (
    <section className="panel">
      <h2>{admin ? 'Leave' : 'My leave'}</h2>
      <form className="form-grid" onSubmit={apply}>
        {admin ? (
          <label>
            Employee ID
            <input
              value={form.employeeId}
              onChange={(e) => setForm({ ...form, employeeId: e.target.value })}
              required
            />
          </label>
        ) : (
          <label>
            Employee
            <input value={`#${selfId || '—'}`} disabled />
          </label>
        )}
        <label>
          Type
          <select
            value={form.leaveType}
            onChange={(e) => setForm({ ...form, leaveType: e.target.value })}
          >
            <option value="CASUAL">Casual</option>
            <option value="SICK">Sick</option>
            <option value="EARNED">Earned</option>
          </select>
        </label>
        <label>
          Start
          <input
            type="date"
            value={form.startDate}
            onChange={(e) => setForm({ ...form, startDate: e.target.value })}
            required
          />
        </label>
        <label>
          End
          <input
            type="date"
            value={form.endDate}
            onChange={(e) => setForm({ ...form, endDate: e.target.value })}
            required
          />
        </label>
        <label>
          Reason
          <input
            value={form.reason}
            onChange={(e) => setForm({ ...form, reason: e.target.value })}
          />
        </label>
        <button type="submit" disabled={!admin && !selfId}>
          Apply
        </button>
      </form>
      {admin && (
        <div className="inline">
          <button type="button" onClick={() => load('all')}>
            All
          </button>
          <button type="button" onClick={() => load('pending')}>
            Pending
          </button>
        </div>
      )}
      {loading ? (
        <Loader label="Loading leave…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee</th>
                <th>Type</th>
                <th>From</th>
                <th>To</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {(rows || []).map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.employeeId || r.employeeName || '—'}</td>
                  <td>{statusLabel(r.leaveType)}</td>
                  <td>{r.startDate ? String(r.startDate).slice(0, 10) : '—'}</td>
                  <td>{r.endDate ? String(r.endDate).slice(0, 10) : '—'}</td>
                  <td><span className="status-pill">{statusLabel(r.status)}</span></td>
                  <td className="row-actions">
                    {r.status === 'PENDING' && (
                      <>
                        {admin && (
                          <>
                            <button type="button" onClick={() => act('approve', r.id)}>
                              Approve
                            </button>
                            <button type="button" className="ghost" onClick={() => act('reject', r.id)}>
                              Reject
                            </button>
                          </>
                        )}
                        <button type="button" className="ghost" onClick={() => act('cancel', r.id)}>
                          Cancel
                        </button>
                      </>
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
