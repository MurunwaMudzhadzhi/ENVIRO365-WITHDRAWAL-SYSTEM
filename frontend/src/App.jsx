import React, { useEffect, useState, useCallback } from 'react'
import PortfolioDashboard from './components/PortfolioDashboard.jsx'
import WithdrawalForm from './components/WithdrawalForm.jsx'
import WithdrawalHistory from './components/WithdrawalHistory.jsx'
import { getInvestors, getPortfolio, getWithdrawalHistory, extractErrorMessage } from './api/api.js'

export default function App() {
  const [investors, setInvestors] = useState([])
  const [selectedInvestorId, setSelectedInvestorId] = useState(null)
  const [portfolio, setPortfolio] = useState(null)
  const [history, setHistory] = useState([])
  const [loadingPortfolio, setLoadingPortfolio] = useState(false)
  const [loadingHistory, setLoadingHistory] = useState(false)
  const [globalError, setGlobalError] = useState(null)

  useEffect(() => {
    getInvestors()
      .then((data) => {
        setInvestors(data)
        if (data.length > 0) setSelectedInvestorId(String(data[0].id))
      })
      .catch((err) => setGlobalError(extractErrorMessage(err)))
  }, [])

  const loadPortfolio = useCallback((investorId) => {
    if (!investorId) return
    setLoadingPortfolio(true)
    getPortfolio(investorId)
      .then(setPortfolio)
      .catch((err) => setGlobalError(extractErrorMessage(err)))
      .finally(() => setLoadingPortfolio(false))
  }, [])

  const loadHistory = useCallback((investorId) => {
    if (!investorId) return
    setLoadingHistory(true)
    getWithdrawalHistory(investorId)
      .then(setHistory)
      .catch((err) => setGlobalError(extractErrorMessage(err)))
      .finally(() => setLoadingHistory(false))
  }, [])

  useEffect(() => {
    if (selectedInvestorId) {
      loadPortfolio(selectedInvestorId)
      loadHistory(selectedInvestorId)
    }
  }, [selectedInvestorId, loadPortfolio, loadHistory])

  const handleSelectInvestor = (id) => {
    setGlobalError(null)
    setSelectedInvestorId(id)
  }

  const handleWithdrawalCreated = () => {
    // refresh balance and history after a successful withdrawal
    loadPortfolio(selectedInvestorId)
    loadHistory(selectedInvestorId)
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Enviro365 Investments</h1>
        <p className="muted">Withdrawal Notice System</p>
      </header>

      {globalError && <p className="error global-error">{globalError}</p>}

      <PortfolioDashboard
        investors={investors}
        selectedInvestorId={selectedInvestorId}
        onSelectInvestor={handleSelectInvestor}
        portfolio={portfolio}
        loading={loadingPortfolio}
      />

      {portfolio && (
        <WithdrawalForm
          key={selectedInvestorId}
          products={portfolio.products}
          onWithdrawalCreated={handleWithdrawalCreated}
        />
      )}

      <WithdrawalHistory
        history={history}
        investorId={selectedInvestorId}
        loading={loadingHistory}
      />
    </div>
  )
}
