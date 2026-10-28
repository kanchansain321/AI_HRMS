import { useEffect, useState } from 'react';
import { api, unwrap } from '../services/api';
import { statusLabel } from '../utils/text';
import Loader, { BusyButton } from '../components/Loader';

const SUB = [
  { id: 'resumes', label: 'Resumes' },
  { id: 'rankings', label: 'Rankings' },
  { id: 'questions', label: 'Questions' },
  { id: 'chat', label: 'Chat' },
  { id: 'performance', label: 'Performance' },
];

export default function RecruitmentPage({ flash }) {
  const [sub, setSub] = useState('resumes');

  return (
    <section className="panel">
      <h2>Recruitment &amp; AI</h2>
      <div className="subtabs">
        {SUB.map((s) => (
          <button
            key={s.id}
            type="button"
            className={sub === s.id ? 'tab active' : 'tab'}
            onClick={() => setSub(s.id)}
          >
            {s.label}
          </button>
        ))}
      </div>
      {sub === 'resumes' && <Resumes flash={flash} />}
      {sub === 'rankings' && <Rankings flash={flash} />}
      {sub === 'questions' && <Questions flash={flash} />}
      {sub === 'chat' && <Chat flash={flash} />}
      {sub === 'performance' && <Performance flash={flash} />}
    </section>
  );
}

function Resumes({ flash }) {
  const [resumes, setResumes] = useState([]);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [file, setFile] = useState(null);
  const [skills, setSkills] = useState('');
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [screeningId, setScreeningId] = useState(null);

  async function load() {
    setLoading(true);
    try {
      setResumes(unwrap(await api.listResumes()) || []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function onUpload(e) {
    e.preventDefault();
    setUploading(true);
    try {
      const fd = new FormData();
      fd.append('candidateName', name);
      fd.append('email', email);
      fd.append('file', file);
      skills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean)
        .forEach((s) => fd.append('skills', s));
      await api.uploadResume(fd);
      flash(true, 'Resume uploaded');
      setName('');
      setEmail('');
      setFile(null);
      setSkills('');
      await load();
    } catch (err) {
      flash(false, err.message);
    } finally {
      setUploading(false);
    }
  }

  async function onScreen(id) {
    setScreeningId(id);
    try {
      const res = await api.screenResume(id);
      flash(true, res?.message || 'Resume screened');
      await load();
    } catch (err) {
      flash(false, err.message);
    } finally {
      setScreeningId(null);
    }
  }

  const busy = uploading || screeningId != null;

  return (
    <div className="panel-loading">
      {screeningId != null && <Loader overlay label="Screening resume with AI…" />}
      <form className="form-grid" onSubmit={onUpload}>
        <label>
          Candidate name
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            disabled={busy}
          />
        </label>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            disabled={busy}
          />
        </label>
        <label>
          File
          <input
            type="file"
            onChange={(e) => setFile(e.target.files?.[0] || null)}
            required
            disabled={busy}
          />
        </label>
        <label>
          Skills (optional)
          <input
            value={skills}
            onChange={(e) => setSkills(e.target.value)}
            disabled={busy}
          />
        </label>
        <BusyButton type="submit" busy={uploading} busyText="Uploading…" disabled={busy}>
          Upload resume
        </BusyButton>
      </form>
      {loading ? (
        <Loader label="Loading resumes…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Skills</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {resumes.length === 0 ? (
                <tr>
                  <td colSpan={5} className="muted">
                    No resumes yet.
                  </td>
                </tr>
              ) : (
                resumes.map((r) => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td>{r.candidateName}</td>
                    <td>{r.email}</td>
                    <td>{(r.skills || []).join(', ') || '—'}</td>
                    <td>
                      <BusyButton
                        busy={screeningId === r.id}
                        busyText="Screening…"
                        onClick={() => onScreen(r.id)}
                        disabled={busy}
                      >
                        Screen
                      </BusyButton>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function Rankings({ flash }) {
  const [skills, setSkills] = useState('Java, Spring Boot, MySQL');
  const [rankings, setRankings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [ranking, setRanking] = useState(false);

  async function load() {
    setLoading(true);
    try {
      setRankings(unwrap(await api.getRankings()) || []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function onRank(e) {
    e.preventDefault();
    setRanking(true);
    try {
      const requiredSkills = skills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
      const res = await api.rankCandidates(requiredSkills);
      setRankings(unwrap(res) || []);
      flash(true, res?.message || 'Candidates ranked');
    } catch (err) {
      flash(false, err.message);
    } finally {
      setRanking(false);
    }
  }

  return (
    <div className="panel-loading">
      {ranking && <Loader overlay label="Ranking candidates…" />}
      <form className="inline" onSubmit={onRank}>
        <input
          value={skills}
          onChange={(e) => setSkills(e.target.value)}
          placeholder="Required skills"
          aria-label="Required skills"
          disabled={ranking}
        />
        <BusyButton type="submit" busy={ranking} busyText="Ranking…">
          Rank candidates
        </BusyButton>
      </form>
      {loading ? (
        <Loader label="Loading rankings…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Candidate</th>
                <th>Score</th>
                <th>Feedback</th>
              </tr>
            </thead>
            <tbody>
              {rankings.map((r) => (
                <tr key={r.id}>
                  <td>{r.rankPosition}</td>
                  <td>
                    {r.candidateName}
                    <div className="muted">{r.email}</div>
                  </td>
                  <td>{r.score}</td>
                  <td>{r.feedback}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function Questions({ flash }) {
  const [resumeId, setResumeId] = useState('');
  const [questions, setQuestions] = useState([]);
  const [answers, setAnswers] = useState({});
  const [generating, setGenerating] = useState(false);
  const [loading, setLoading] = useState(false);
  const [savingId, setSavingId] = useState(null);

  async function generate(e) {
    e.preventDefault();
    setGenerating(true);
    try {
      const res = await api.generateQuestions(Number(resumeId));
      setQuestions(unwrap(res) || []);
      flash(true, res?.message || 'Questions generated');
    } catch (err) {
      flash(false, err.message);
    } finally {
      setGenerating(false);
    }
  }

  async function load() {
    setLoading(true);
    try {
      setQuestions(unwrap(await api.listQuestions(Number(resumeId))) || []);
    } catch (err) {
      flash(false, err.message);
    } finally {
      setLoading(false);
    }
  }

  async function submitAnswer(questionId) {
    setSavingId(questionId);
    try {
      await api.submitAnswer(questionId, answers[questionId] || '');
      flash(true, 'Answer saved');
    } catch (err) {
      flash(false, err.message);
    } finally {
      setSavingId(null);
    }
  }

  return (
    <div className="panel-loading">
      {(generating || loading) && (
        <Loader overlay label={generating ? 'Generating questions…' : 'Loading questions…'} />
      )}
      <form className="inline" onSubmit={generate}>
        <input
          placeholder="Resume ID"
          value={resumeId}
          onChange={(e) => setResumeId(e.target.value)}
          required
          disabled={generating || loading}
        />
        <BusyButton type="button" busy={loading} busyText="Loading…" onClick={load} disabled={generating}>
          Load
        </BusyButton>
        <BusyButton type="submit" busy={generating} busyText="Generating…" disabled={loading}>
          Generate questions
        </BusyButton>
      </form>
      <ul className="q-list">
        {questions.map((q) => (
          <li key={q.id}>
            <div className="skill-tag">{statusLabel(q.skillName || 'General')}</div>
            <p>{q.questionText}</p>
            <textarea
              value={answers[q.id] || ''}
              onChange={(e) => setAnswers({ ...answers, [q.id]: e.target.value })}
            />
            <BusyButton
              busy={savingId === q.id}
              busyText="Saving…"
              onClick={() => submitAnswer(q.id)}
            >
              Save answer
            </BusyButton>
          </li>
        ))}
      </ul>
    </div>
  );
}

function Chat({ flash }) {
  const [employeeId, setEmployeeId] = useState('');
  const [message, setMessage] = useState('');
  const [thread, setThread] = useState([]);
  const [sending, setSending] = useState(false);
  const [loadingHistory, setLoadingHistory] = useState(false);

  async function send(e) {
    e.preventDefault();
    setSending(true);
    try {
      const res = await api.chat(employeeId ? Number(employeeId) : null, message);
      setThread((t) => [unwrap(res) || res?.data, ...t]);
      setMessage('');
      flash(true, 'Reply received');
    } catch (err) {
      flash(false, err.message);
    } finally {
      setSending(false);
    }
  }

  async function loadHistory() {
    if (!employeeId) return flash(false, 'Enter an employee ID');
    setLoadingHistory(true);
    try {
      setThread(unwrap(await api.chatHistory(Number(employeeId))) || []);
    } catch (err) {
      flash(false, err.message);
    } finally {
      setLoadingHistory(false);
    }
  }

  return (
    <div className="panel-loading">
      {sending && <Loader overlay label="Waiting for AI reply…" />}
      <div className="inline">
        <input
          placeholder="Employee ID"
          value={employeeId}
          onChange={(e) => setEmployeeId(e.target.value)}
          disabled={sending}
        />
        <BusyButton
          type="button"
          busy={loadingHistory}
          busyText="Loading…"
          onClick={loadHistory}
          disabled={sending}
        >
          Load history
        </BusyButton>
      </div>
      <form className="chat-form" onSubmit={send}>
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          required
          disabled={sending}
        />
        <BusyButton type="submit" busy={sending} busyText="Sending…">
          Send message
        </BusyButton>
      </form>
      <ul className="chat-thread">
        {thread.map((h) => (
          <li key={h?.id || `${h?.message}-${h?.createdAt}`}>
            <strong>You:</strong> {h?.message}
            <div>
              <strong>AI:</strong> {h?.response}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

function Performance({ flash }) {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({ employeeId: '', reviewPeriod: '', rating: '', notes: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  async function load() {
    setLoading(true);
    try {
      setRows(unwrap(await api.listPerformance()) || []);
    } catch (e) {
      flash(false, e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function onCreate(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await api.createPerformance({
        employeeId: Number(form.employeeId),
        reviewPeriod: form.reviewPeriod,
        rating: form.rating ? Number(form.rating) : null,
        notes: form.notes,
      });
      flash(true, 'Performance saved');
      setForm({ employeeId: '', reviewPeriod: '', rating: '', notes: '' });
      await load();
    } catch (err) {
      flash(false, err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="panel-loading">
      {saving && <Loader overlay label="Saving performance review…" />}
      <form className="form-grid" onSubmit={onCreate}>
        <label>
          Employee ID
          <input
            value={form.employeeId}
            onChange={(e) => setForm({ ...form, employeeId: e.target.value })}
            required
            disabled={saving}
          />
        </label>
        <label>
          Period
          <input
            value={form.reviewPeriod}
            onChange={(e) => setForm({ ...form, reviewPeriod: e.target.value })}
            required
            disabled={saving}
          />
        </label>
        <label>
          Rating
          <input
            value={form.rating}
            onChange={(e) => setForm({ ...form, rating: e.target.value })}
            disabled={saving}
          />
        </label>
        <label>
          Notes
          <textarea
            value={form.notes}
            onChange={(e) => setForm({ ...form, notes: e.target.value })}
            disabled={saving}
          />
        </label>
        <BusyButton type="submit" busy={saving} busyText="Saving…">
          Create review
        </BusyButton>
      </form>
      {loading ? (
        <Loader label="Loading performance…" />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Employee</th>
                <th>Period</th>
                <th>Rating</th>
                <th>AI summary</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>{r.employeeId}</td>
                  <td>{r.reviewPeriod}</td>
                  <td>{r.rating}</td>
                  <td>{r.aiSummary}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
