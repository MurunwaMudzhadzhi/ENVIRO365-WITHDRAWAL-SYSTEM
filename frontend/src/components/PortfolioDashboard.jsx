import React from 'react'

export default function PortfolioDashboard({ investors, selectedInvestorId, onSelectInvestor, portfolio, loading }) {
  return (
    <section className="card">
      <div className="card-header">
        <h2>Portfolio Dashboard</h2>
        <select
          value={selectedInvestorId || ''}
          onChange={(e) => onSelectInvestor(e.target.value)}
        >
          <option value="" disabled>Select an investor</option>
          {investors.map((inv) => (
            <option key={inv.id} value={inv.id}>{inv.fullName}</option>
          ))}
        </select>
      </div>

      {loading && <p className="muted">Loading portfolio...</p>}

      {!loading && portfolio && (
        <>
          <div className="investor-details">
            <p><strong>{portfolio.fullName}</strong> · {portfolio.email}</p>
            <p className="muted">Age: {portfolio.age}</p>
          </div>

          <div className="product-grid">
            {portfolio.products.map((p) => (
              <div className="product-card" key={p.id}>
                <span className="product-type">{p.productType.replaceAll('_', ' ')}</span>
                <h3>{p.productName}</h3>
                <p className="balance">R {Number(p.balance).toLocaleString('en-ZA', { minimumFractionDigits: 2 })}</p>
              </div>
            ))}
          </div>
        </>
      )}

      {!loading && !portfolio && (
        <p className="muted">Select an investor above to view their portfolio.</p>
      )}
    </section>
  )
}
