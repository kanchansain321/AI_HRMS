const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8081';
const TOKEN_KEY = 'aihrms_token';
const USER_KEY = 'aihrms_user';

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null');
  } catch {
    return null;
  }
}

export function saveSession(loginData) {
  localStorage.setItem(TOKEN_KEY, loginData.token);
  localStorage.setItem(
    USER_KEY,
    JSON.stringify({
      employeeId: loginData.employeeId,
      name: loginData.name,
      email: loginData.email,
      role: loginData.role,
    })
  );
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

async function request(path, options = {}) {
  const headers = { ...(options.headers || {}) };
  const token = getToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  const data = await res.json().catch(() => null);
  if (res.status === 401 && !path.startsWith('/auth/login')) {
    clearSession();
  }
  if (!res.ok) {
    const message = data?.message || (typeof data === 'string' ? data : null) || res.statusText || 'Request failed';
    throw new Error(message);
  }
  return data;
}

function json(method, body) {
  return {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  };
}

export function unwrap(res) {
  if (res && typeof res === 'object' && 'data' in res) return res.data;
  return res;
}

export const api = {
  login: (email, password) => request('/auth/login', json('POST', { email, password })),
  me: () => request('/auth/me'),

  listEmployees: () => request('/employee/all'),
  getEmployee: (empId) => request(`/employee?empId=${empId}`),
  createEmployee: (body) => request('/employee', json('POST', body)),
  updateEmployee: (body) => request('/employee', json('PUT', body)),
  listRoles: () => request('/roles'),

  listDepartments: () => request('/department'),
  createDepartment: (departmentName) =>
    request(`/department?departmentName=${encodeURIComponent(departmentName)}`, { method: 'POST' }),
  listDesignations: () => request('/designation'),
  createDesignation: (designationName) =>
    request(`/designation?designationName=${encodeURIComponent(designationName)}`, { method: 'POST' }),

  checkIn: (body) => request('/attendance/check-in', json('POST', body)),
  checkOut: (id) => request(`/attendance/check-out/${id}`, { method: 'PUT' }),
  markAbsent: (body) => request('/attendance/mark-absent', json('POST', body)),
  listAttendance: () => request('/attendance/all'),
  todayAttendance: () => request('/attendance/today'),
  attendanceByEmployee: (empId) => request(`/attendance/employee/${empId}`),
  deleteAttendance: (id) => request(`/attendance/delete/${id}`, { method: 'DELETE' }),

  applyLeave: (body) => request('/leave/apply', json('POST', body)),
  approveLeave: (id) => request(`/leave/approve/${id}`, { method: 'PUT' }),
  rejectLeave: (id) => request(`/leave/reject/${id}`, { method: 'PUT' }),
  cancelLeave: (id) => request(`/leave/cancel/${id}`, { method: 'PUT' }),
  listLeaves: () => request('/leave/all'),
  pendingLeaves: () => request('/leave/pending'),
  leavesByEmployee: (empId) => request(`/leave/employee/${empId}`),

  addPayroll: (body) => request('/api/employee-payroll/add', json('POST', body)),
  listPayrolls: () => request('/api/employee-payroll/all'),
  payrollByEmployee: (employeeId) => request(`/api/employee-payroll/employee/${employeeId}`),
  deletePayroll: (payrollId) => request(`/api/employee-payroll/${payrollId}`, { method: 'DELETE' }),

  listResumes: () => request('/recruitment/resumes'),
  uploadResume: (formData) => request('/recruitment/resumes/upload', { method: 'POST', body: formData }),
  screenResume: (id) => request(`/recruitment/resumes/${id}/screen`, { method: 'POST' }),
  rankCandidates: (requiredSkills) =>
    request('/recruitment/resumes/rank', json('POST', { requiredSkills })),
  getRankings: () => request('/recruitment/resumes/rankings'),
  generateQuestions: (resumeId, count) =>
    request('/recruitment/interview-questions/generate', json('POST', { resumeId, count })),
  listQuestions: (resumeId) => request(`/recruitment/interview-questions?resumeId=${resumeId}`),
  submitAnswer: (questionId, answerText) =>
    request('/recruitment/answers', json('POST', { questionId, answerText })),
  chat: (employeeId, message) =>
    request('/recruitment/chat', json('POST', { employeeId: employeeId || null, message })),
  chatHistory: (employeeId) => request(`/recruitment/chat/history/${employeeId}`),
  createPerformance: (body) => request('/recruitment/performance', json('POST', body)),
  listPerformance: () => request('/recruitment/performance'),
};
