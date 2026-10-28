import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import { statusLabel } from '../utils/text';
import Loader, { BusyButton } from '../components/Loader';

const EMPTY_FORM = {
  id: null,
  name: '',
  email: '',
  password: '',
  phoneNumber: '',
  department: '',
  designation: '',
  role: '',
  status: 'ACTIVE',
};

export default function EmployeesPage({ flash }) {
  const [rows, setRows] = useState([]);
  const [deps, setDeps] = useState([]);
  const [desigs, setDesigs] = useState([]);
  const [roles, setRoles] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [open, setOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);

  const editing = form.id != null;

  function defaultRoleId(roleList = roles) {
    const employeeRole = roleList.find((x) => String(x.name).toUpperCase() === 'EMPLOYEE');
    return employeeRole ? String(employeeRole.id) : roleList[0] ? String(roleList[0].id) : '';
  }

  function closeModal() {
    setOpen(false);
    setForm({ ...EMPTY_FORM, role: defaultRoleId() });
  }

  function openAdd() {
    setForm({ ...EMPTY_FORM, role: defaultRoleId() });
    setOpen(true);
  }

  function openEdit(row) {
    setForm({
      id: row.id,
      name: row.name || row.firstName || '',
      email: row.email || '',
      password: '',
      phoneNumber: row.phoneNumber || '',
      department: row.departmentId != null ? String(row.departmentId) : '',
      designation: row.designationId != null ? String(row.designationId) : '',
      role: row.roleId != null ? String(row.roleId) : defaultRoleId(),
      status: row.status === 'INACTIVE' || row.status === 0 ? 'INACTIVE' : 'ACTIVE',
    });
    setOpen(true);
  }

  async function load() {
    setLoading(true);
    try {
      const [emps, d, g, r] = await Promise.all([
        api.listEmployees(),
        api.listDepartments(),
        api.listDesignations(),
        api.listRoles().catch(() => []),
      ]);
      setRows(Array.isArray(emps) ? emps : unwrap(emps) || []);
      setDeps(unwrap(d) || []);
      setDesigs(unwrap(g) || []);
      const roleList = Array.isArray(r) ? r : unwrap(r) || [];
      setRoles(roleList);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    if (!open) return undefined;
    function onKey(e) {
      if (e.key === 'Escape') closeModal();
    }
    document.addEventListener('keydown', onKey);
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', onKey);
      document.body.style.overflow = '';
    };
  }, [open]);

  async function onSubmit(e) {
    e.preventDefault();
    setSaving(true);
    try {
      const body = {
        name: form.name,
        email: form.email,
        phoneNumber: form.phoneNumber,
        role: Number(form.role),
        department: form.department ? Number(form.department) : null,
        designation: form.designation ? Number(form.designation) : null,
        status: form.status,
      };
      if (form.password.trim()) {
        body.password = form.password;
      }

      if (editing) {
        body.id = Number(form.id);
        await api.updateEmployee(body);
        flash(true, 'Employee updated');
      } else {
        if (!form.password.trim()) {
          flash(false, 'Password is required for new employees');
          setSaving(false);
          return;
        }
        body.password = form.password;
        body.joiningDate = new Date().toISOString();
        await api.createEmployee(body);
        flash(true, 'Employee created');
      }
      closeModal();
      await load();
    } catch (err) {
      flash(false, err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="panel">
      <div className="page-head">
        <div>
          <h2>Employees</h2>
          <p className="lead">Manage employee records in one place.</p>
        </div>
        <button type="button" onClick={openAdd}>
          Add employee
        </button>
      </div>

      {loading ? (
        <Loader label="Loading employees…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Dept</th>
                <th>Designation</th>
                <th>Status</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={9} className="muted">
                    No employees yet. Click Add employee to create one.
                  </td>
                </tr>
              ) : (
                rows.map((r) => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td>{r.name || `${r.firstName || ''} ${r.lastName || ''}`.trim()}</td>
                    <td>{r.email}</td>
                    <td>{r.phoneNumber || '—'}</td>
                    <td>{statusLabel(r.roleName || r.role)}</td>
                    <td>{r.departmentName || r.department || '—'}</td>
                    <td>{r.designation || r.designationName || '—'}</td>
                    <td>
                      <span className="status-pill">{statusLabel(r.status)}</span>
                    </td>
                    <td className="row-actions">
                      <button type="button" className="ghost" onClick={() => openEdit(r)}>
                        Edit
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {open && (
        <div
          className="modal-backdrop"
          role="presentation"
          onClick={(e) => {
            if (e.target === e.currentTarget) closeModal();
          }}
        >
          <div
            className="modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="employee-modal-title"
          >
            <div className="modal-head">
              <h3 id="employee-modal-title">{editing ? 'Edit employee' : 'Add employee'}</h3>
              <button type="button" className="icon-btn" onClick={closeModal} aria-label="Close">
                ×
              </button>
            </div>
            {editing && (
              <p className="muted modal-hint">
                Editing #{form.id}. Leave password blank to keep the current one.
              </p>
            )}
            <form className="form-grid modal-form" onSubmit={onSubmit}>
              <label>
                Name
                <input
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  required
                  autoFocus
                />
              </label>
              <label>
                Email
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  required
                />
              </label>
              <label>
                Password{editing ? ' (optional)' : ''}
                <input
                  type="password"
                  value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })}
                  required={!editing}
                  placeholder={editing ? 'Leave blank to keep current' : ''}
                  autoComplete="new-password"
                />
              </label>
              <label>
                Phone
                <input
                  value={form.phoneNumber}
                  onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
                />
              </label>
              <label>
                Role
                <select
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value })}
                  required
                >
                  <option value="">Select</option>
                  {roles.map((r) => (
                    <option key={r.id} value={r.id}>
                      {r.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Status
                <select
                  value={form.status}
                  onChange={(e) => setForm({ ...form, status: e.target.value })}
                  required
                >
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                </select>
              </label>
              <label>
                Department
                <select
                  value={form.department}
                  onChange={(e) => setForm({ ...form, department: e.target.value })}
                >
                  <option value="">Select</option>
                  {deps.map((d) => (
                    <option key={d.id || d.departmentId} value={d.id || d.departmentId}>
                      {d.name || d.departmentName}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Designation
                <select
                  value={form.designation}
                  onChange={(e) => setForm({ ...form, designation: e.target.value })}
                >
                  <option value="">Select</option>
                  {desigs.map((d) => (
                    <option key={d.id || d.designationId} value={d.id || d.designationId}>
                      {d.name || d.designationName}
                    </option>
                  ))}
                </select>
              </label>
              <div className="modal-actions">
                <button type="button" className="ghost" onClick={closeModal} disabled={saving}>
                  Cancel
                </button>
                <BusyButton
                  type="submit"
                  busy={saving}
                  busyText="Saving…"
                >
                  {editing ? 'Save changes' : 'Add employee'}
                </BusyButton>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}
