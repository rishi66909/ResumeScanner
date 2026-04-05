import { useEffect, useState } from 'react'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8082/api/resume'

function App() {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('resumeScannerUser'))
    } catch {
      return null
    }
  })
  const [view, setView] = useState('scan')
  const [file, setFile] = useState(null)
  const [jobDescription, setJobDescription] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [scanResult, setScanResult] = useState(null)
  const [scans, setScans] = useState([])
  const [loginEmail, setLoginEmail] = useState('')
  const [loginPassword, setLoginPassword] = useState('')

  useEffect(() => {
    if (user) {
      fetchAllScans()
    }
  }, [user])

  const handleLoginSubmit = (event) => {
    event.preventDefault()
    setError('')

    if (!loginEmail.trim() || !loginPassword.trim()) {
      setError('Please enter your email and password.')
      return
    }

    const profile = {
      name: loginEmail.split('@')[0] || loginEmail,
      email: loginEmail.trim(),
    }

    localStorage.setItem('resumeScannerUser', JSON.stringify(profile))
    setUser(profile)
    setLoginPassword('')
  }

  const handleLogout = () => {
    localStorage.removeItem('resumeScannerUser')
    setUser(null)
    setView('scan')
    setError('')
    setScanResult(null)
    setScans([])
  }

  const fetchAllScans = async () => {
    try {
      setError('')
      const response = await fetch(`${API_BASE}/resume/all`)
      if (!response.ok) throw new Error('Unable to load scan history.')
      const data = await response.json()
      setScans(Array.isArray(data) ? data : [])
    } catch (err) {
      setError(err.message || 'Failed to fetch scans.')
    }
  }

  const handleScanSubmit = async (event) => {
    event.preventDefault()
    setError('')

    if (!file) {
      setError('Please upload a resume file.')
      return
    }
    if (!jobDescription.trim()) {
      setError('Please enter a job description.')
      return
    }

    const formData = new FormData()
    formData.append('file', file)
    formData.append('jobDescription', jobDescription)

    try {
      setLoading(true)
      const response = await fetch(`${API_BASE}/resume/scan`, {
        method: 'POST',
        body: formData,
      })
      const body = await response.json()
      if (!response.ok) {
        throw new Error(body.message || 'Resume scan failed.')
      }

      setScanResult(body)
      setJobDescription('')
      setFile(null)
      fetchAllScans()
      setView('scan')
    } catch (err) {
      setError(err.message || 'Scan submission failed.')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this scan record?')) return

    try {
      setLoading(true)
      const response = await fetch(`${API_BASE}/resume/${id}`, { method: 'DELETE' })
      if (!response.ok) throw new Error('Failed to delete the scan.')
      if (scanResult?.id === id) setScanResult(null)
      await fetchAllScans()
    } catch (err) {
      setError(err.message || 'Delete operation failed.')
    } finally {
      setLoading(false)
    }
  }

  if (!user) {
    return (
      <main className="login-page">
        <section className="auth-card">
          <div className="auth-brand">
            <span className="logo">RS</span>
            <div>
              <h1>Resume Scanner</h1>
              <p>Secure login to access your dashboard.</p>
            </div>
          </div>

          <form className="auth-form" onSubmit={handleLoginSubmit}>
            <label>
              Email address
              <input
                type="email"
                value={loginEmail}
                onChange={(event) => setLoginEmail(event.target.value)}
                placeholder="you@example.com"
                required
              />
            </label>

            <label>
              Password
              <input
                type="password"
                value={loginPassword}
                onChange={(event) => setLoginPassword(event.target.value)}
                placeholder="Enter a secure password"
                required
              />
            </label>

            <button type="submit" className="primary-button">
              Sign in
            </button>

            <p className="auth-note">
              Use any email and password to continue. This is a frontend-only demo login.
            </p>

            {error && <div className="form-error">{error}</div>}
          </form>
        </section>
      </main>
    )
  }

  const totalScans = scans.length
  const lastScan = scans[0]
  const completedScore = scanResult?.matchScore ?? lastScan?.matchScore

  return (
    <main className="dashboard-page">
      <aside className="dashboard-sidebar">
        <div className="brand">
          <span className="logo">RS</span>
          <div>
            <h1>Resume Scanner</h1>
            <p>Dashboard</p>
          </div>
        </div>

        <nav className="dashboard-nav">
          <button className={view === 'scan' ? 'nav-button active' : 'nav-button'} onClick={() => setView('scan')}>
            Scan Resume
          </button>
          <button className={view === 'history' ? 'nav-button active' : 'nav-button'} onClick={() => setView('history')}>
            Scan History
          </button>
          <button className={view === 'stats' ? 'nav-button active' : 'nav-button'} onClick={() => setView('stats')}>
            Analytics
          </button>
        </nav>

        <div className="sidebar-footer">
          <div className="profile-summary">
            <span>Signed in as</span>
            <strong>{user.name}</strong>
          </div>
          <button className="secondary-button" onClick={handleLogout}>
            Sign out
          </button>
        </div>
      </aside>

      <section className="dashboard-main">
        <header className="dashboard-header">
          <div>
            <p className="eyebrow">Welcome back</p>
            <h2>{user.name}</h2>
            <p className="subtitle">
              Scan resumes against job postings and review performance at a glance.
            </p>
          </div>
          <div className="dashboard-top-actions">
            <button className="primary-button" onClick={() => setView('scan')}>
              New resume scan
            </button>
          </div>
        </header>

        <div className="overview-grid">
          <article className="stat-card">
            <h3>Total Scans</h3>
            <p>{totalScans}</p>
          </article>
          <article className="stat-card">
            <h3>Last Match</h3>
            <p>{completedScore ? `${completedScore.toFixed(2)}%` : 'N/A'}</p>
          </article>
          <article className="stat-card">
            <h3>Recent Scan</h3>
            <p>{lastScan ? lastScan.fileName : 'No scans yet'}</p>
          </article>
        </div>

        {error && <div className="global-error">{error}</div>}

        {view === 'scan' && (
          <section className="panel-card">
            <div className="panel-header">
              <div>
                <h3>Resume scanner</h3>
                <p>Upload a resume, paste the job description, and review matching keywords instantly.</p>
              </div>
              <span className="tag">Ready</span>
            </div>

            <form className="scan-form" onSubmit={handleScanSubmit}>
              <label>
                Resume file
                <input
                  type="file"
                  accept=".pdf,.docx,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                  onChange={(event) => setFile(event.target.files?.[0] || null)}
                />
              </label>

              <label>
                Job description
                <textarea
                  value={jobDescription}
                  onChange={(event) => setJobDescription(event.target.value)}
                  rows={6}
                  placeholder="Paste the job description here..."
                  required
                />
              </label>

              <div className="form-actions">
                <button type="submit" disabled={loading} className="primary-button">
                  {loading ? 'Scanning…' : 'Run scan'}
                </button>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => {
                    setFile(null)
                    setJobDescription('')
                    setError('')
                  }}
                >
                  Reset
                </button>
              </div>
            </form>
          </section>
        )}

        {view === 'history' && (
          <section className="panel-card">
            <div className="panel-header">
              <div>
                <h3>Scan history</h3>
                <p>Review all uploaded resumes, scores, and actions in one place.</p>
              </div>
              <span className="tag">{totalScans} records</span>
            </div>

            {scans.length === 0 ? (
              <div className="empty-state">
                <p>No scan records are available yet.</p>
              </div>
            ) : (
              <div className="history-list">
                {scans.map((item) => (
                  <article key={item.id} className="history-card">
                    <div>
                      <strong>{item.fileName}</strong>
                      <p>{item.scannedAt}</p>
                    </div>
                    <div className="history-meta">
                      <span>Score: {item.matchScore?.toFixed(2)}%</span>
                      <span>Matched: {item.matchedKeywords || 'None'}</span>
                    </div>
                    <div className="history-actions">
                      <button onClick={() => setScanResult(item)} className="secondary-button">
                        View
                      </button>
                      <button onClick={() => handleDelete(item.id)} className="delete-button">
                        Delete
                      </button>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>
        )}

        {view === 'stats' && (
          <section className="panel-card">
            <div className="panel-header">
              <div>
                <h3>Analytics</h3>
                <p>Quick performance summary from your scans.</p>
              </div>
              <span className="tag">Insights</span>
            </div>

            <div className="analytics-grid">
              <div className="analytics-card">
                <h4>Average Score</h4>
                <p>
                  {scans.length
                    ? `${(scans.reduce((sum, scan) => sum + (scan.matchScore || 0), 0) / scans.length).toFixed(2)}%`
                    : 'N/A'}
                </p>
              </div>
              <div className="analytics-card">
                <h4>Most Recent Keywords</h4>
                <p>{scanResult?.matchedKeywords || lastScan?.matchedKeywords || 'None'}</p>
              </div>
              <div className="analytics-card">
                <h4>Pending scans</h4>
                <p>{loading ? 'Scanning now...' : 'All caught up'}</p>
              </div>
            </div>
          </section>
        )}

        <section className="panel-card result-panel">
          <div className="panel-header">
            <div>
              <h3>Result preview</h3>
              <p>Detailed output for the last selected scan.</p>
            </div>
            <span className="tag">Result</span>
          </div>

          {scanResult ? (
            <div className="result-summary">
              <div className="result-grid">
                <div>
                  <p className="result-label">File</p>
                  <strong>{scanResult.fileName}</strong>
                </div>
                <div>
                  <p className="result-label">Match score</p>
                  <strong>{scanResult.matchScore?.toFixed(2)}%</strong>
                </div>
                <div>
                  <p className="result-label">Scanned at</p>
                  <strong>{scanResult.scannedAt || 'Unknown'}</strong>
                </div>
              </div>

              <div className="result-block">
                <p className="result-label">Matched keywords</p>
                <p>{scanResult.matchedKeywords || 'None'}</p>
              </div>

              <div className="result-block">
                <p className="result-label">Missing keywords</p>
                <p>{scanResult.missingKeywords || 'None'}</p>
              </div>

              <details className="result-preview">
                <summary>Resume text preview</summary>
                <pre>{scanResult.resumeText?.substring(0, 500) || 'No preview available.'}</pre>
              </details>
            </div>
          ) : (
            <div className="empty-state">
              <p>Select a scan from history or run a new scan to see detailed results.</p>
            </div>
          )}
        </section>
      </section>
    </main>
  )
}

export default App

