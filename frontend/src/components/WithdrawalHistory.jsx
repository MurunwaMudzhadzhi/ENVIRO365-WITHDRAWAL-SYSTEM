import React, { useState } from 'react'
import { downloadCsv } from '../api/api'

export default function WithdrawalHistory({ history, investorId, loading }) {
  const [downloading, setDownloading] = useState(false)
  const [downloadError, setDownloadError] = useState(null)

  const handleDownload = async () => {
    setDownloadError(null)
    setDownloading(true)
    try {
      await downloadCsv(investorId)
    } catch (err) {
      setDownloadError('Could not download CSV. Please try again.')
    } finally {
      setDownloading(false)
    }
  }

  return (
    <section className="card">
      <div className="card-header">
        <h2>Withdrawal History</h2>
        <button onClick={handleDownload} disabled={!investorId || downloading || history.length === 0}>
          {downloading ? 'Preparing CSV...' : 'Download CSV'}
        </button>
      </div>

      {downloadError && <p className="error">{downloadError}</p>}
      {loading && <p className="muted">Loading history...</p>}

      {!loading && history.length === 0 && (
        <p className="muted">No withdrawal notices yet.</p>
      )}

      {!loading && history.length > 0 && (
        <table className="history-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Product</th>
              <th>Amount</th>
              <th>Balance After</th>
              <th>Status</th>
              <th>Notes</th>
            </tr>
          </thead>
          <tbody>
            {history.map((h) => (
              <tr key={h.id}>
                <td>{new Date(h.requestDate).toLocaleString('en-ZA')}</td>
                <td>{h.productName}</td>
                <td>R {Number(h.amount).toLocaleString('en-ZA', { minimumFractionDigits: 2 })}</td>
                <td>R {Number(h.balanceAfter).toLocaleString('en-ZA', { minimumFractionDigits: 2 })}</td>
                <td><span className={`status status-${h.status.toLowerCase()}`}>{h.status}</span></td>
                <td>{h.notes || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
