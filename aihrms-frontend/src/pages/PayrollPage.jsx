import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import { isAdmin } from '../utils/roles';
import Loader from '../components/Loader';

export default function PayrollPage({ flash, user }) {
  const admin = isAdmin(user?.role);
  const selfId = user?.employeeId;
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({
    employeeId: '',
    basicSalary: '',
    deductions: '0',
    month: '',
    year: new Date().getFullYear(),
  });

  async function load() {
    setLoading(true);
    try {
      const res = admin
        ? await api.listPayrolls()
        : selfId
          ? await api.payrollByEmployee(selfId)
          : null;
      setRows(res ? unwrap(res) || [] : []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [admin, selfId]);

  async function onAdd(e) {
    e.preventDefault();
    if (!admin) return;
    try {
      await api.addPayroll({
        employeeId: Number(form.employeeId),
        basicSalary: Number(form.basicSalary),
        deductions: Number(form.deductions || 0),
        month: form.month,
        year: Number(form.year),
      });
      flash(true, 'Payroll added');
      setForm({
        employeeId: '',
        basicSalary: '',
        deductions: '0',
        month: '',
        year: new Date().getFullYear(),
      });
      await load();
    } catch (err) {
      flash(false, err.message);
    }
  }

  async function remove(id) {
    if (!admin) return;
    try {
      await api.deletePayroll(id);
      flash(true, 'Payroll deleted');
      await load();
    } catch (err) {
      flash(false, err.message);
    }
  }

  return (
    <section className="panel">
      <h2>{admin ? 'Payroll' : 'My payroll'}</h2>
      {admin && (
        <form className="form-grid" onSubmit={onAdd}>
          <label>
            Employee ID
            <input
              value={form.employeeId}
              onChange={(e) => setForm({ ...form, employeeId: e.target.value })}
              required
            />
          </label>
          <label>
            Basic salary
            <input
              type="number"
              step="0.01"
              value={form.basicSalary}
              onChange={(e) => setForm({ ...form, basicSalary: e.target.value })}
              required
            />
          </label>
          <label>
            Deductions
            <input
              type="number"
              step="0.01"
              value={form.deductions}
              onChange={(e) => setForm({ ...form, deductions: e.target.value })}
            />
          </label>
          <label>
            Month
            <input
              value={form.month}
              onChange={(e) => setForm({ ...form, month: e.target.value })}
              placeholder="January"
              required
            />
          </label>
          <label>
            Year
            <input
              type="number"
              value={form.year}
              onChange={(e) => setForm({ ...form, year: e.target.value })}
              required
            />
          </label>
          <button type="submit">Add payroll</button>
        </form>
      )}
      {!admin && <p className="lead">Payslips for employee #{selfId || '—'}.</p>}
      {loading ? (
        <Loader label="Loading payroll…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee</th>
                <th>Month</th>
                <th>Year</th>
                <th>Basic</th>
                <th>Deductions</th>
                <th>Net</th>
                {admin && <th />}
              </tr>
            </thead>
            <tbody>
              {(rows || []).map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.employeeId || r.employeeName || '—'}</td>
                  <td>{r.month}</td>
                  <td>{r.year}</td>
                  <td>{r.basicSalary}</td>
                  <td>{r.deductions}</td>
                  <td>{r.netSalary || r.netPay || '—'}</td>
                  {admin && (
                    <td>
                      <button type="button" className="ghost" onClick={() => remove(r.id)}>
                        Delete
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
