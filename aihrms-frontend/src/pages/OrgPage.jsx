import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import Loader from '../components/Loader';

export default function OrgPage({ flash }) {
  const [deps, setDeps] = useState([]);
  const [desigs, setDesigs] = useState([]);
  const [deptName, setDeptName] = useState('');
  const [desigName, setDesigName] = useState('');
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    try {
      const [d, g] = await Promise.all([api.listDepartments(), api.listDesignations()]);
      setDeps(unwrap(d) || []);
      setDesigs(unwrap(g) || []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  async function addDept(e) {
    e.preventDefault();
    try {
      await api.createDepartment(deptName);
      flash(true, 'Department added');
      setDeptName('');
      await load();
    } catch (err) {
      flash(false, err.message);
    }
  }

  async function addDesig(e) {
    e.preventDefault();
    try {
      await api.createDesignation(desigName);
      flash(true, 'Designation added');
      setDesigName('');
      await load();
    } catch (err) {
      flash(false, err.message);
    }
  }

  return (
    <section className="panel stack">
      <div>
        <h2>Organization</h2>
        <p className="lead">Departments and designations.</p>
      </div>

      {loading ? (
        <Loader label="Loading organization…" />
      ) : (
        <div className="split">
          <div>
            <h3>Departments</h3>
            <form className="inline" onSubmit={addDept}>
              <input value={deptName} onChange={(e) => setDeptName(e.target.value)} placeholder="Department name" required />
              <button type="submit">Add</button>
            </form>
            <ul className="simple-list">
              {deps.map((d) => (
                <li key={d.id || d.departmentId}>{d.name || d.departmentName} <span className="muted">#{d.id || d.departmentId}</span></li>
              ))}
            </ul>
          </div>
          <div>
            <h3>Designations</h3>
            <form className="inline" onSubmit={addDesig}>
              <input value={desigName} onChange={(e) => setDesigName(e.target.value)} placeholder="Designation name" required />
              <button type="submit">Add</button>
            </form>
            <ul className="simple-list">
              {desigs.map((d) => (
                <li key={d.id || d.designationId}>{d.name || d.designationName} <span className="muted">#{d.id || d.designationId}</span></li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </section>
  );
}
