import React, { useState } from 'react'
import { createWithdrawal, extractErrorMessage } from '../api/api'

export default function WithdrawalForm({ products, onWithdrawalCreated }) {
  const [productId, setProductId] = useState('')
  const [amount, setAmount] = useState('')
  const [notes, setNotes] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [validationError, setValidationError] = useState(null)

  const selectedProduct = products.find((p) => String(p.id) === String(productId))

  const validate = () => {
    if (!productId) return 'Please select a product.'
    const numericAmount = Number(amount)
    if (!amount || Number.isNaN(numericAmount) || numericAmount <= 0) {
      return 'Please enter an amount greater than 0.'
    }
    if (selectedProduct && numericAmount > Number(selectedProduct.balance)) {
      return `Amount exceeds available balance of R ${Number(selectedProduct.balance).toLocaleString('en-ZA')}.`
    }
    return null
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(null)

    const clientError = validate()
    if (clientError) {
      setValidationError(clientError)
      return
    }
    setValidationError(null)
    setSubmitting(true)

    try {
      const result = await createWithdrawal({
        productId: Number(productId),
        amount: Number(amount),
        notes: notes || null,
      })
      setSuccess(`Withdrawal of R ${Number(result.amount).toLocaleString('en-ZA')} processed. New balance: R ${Number(result.balanceAfter).toLocaleString('en-ZA')}.`)
      setAmount('')
      setNotes('')
      onWithdrawalCreated()
    } catch (err) {
      setError(extractErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="card">
      <h2>Submit Withdrawal Notice</h2>
      <form onSubmit={handleSubmit} className="withdrawal-form">
        <label>
          Product
          <select value={productId} onChange={(e) => setProductId(e.target.value)}>
            <option value="" disabled>Select a product</option>
            {products.map((p) => (
              <option key={p.id} value={p.id}>
                {p.productName} (R {Number(p.balance).toLocaleString('en-ZA')})
              </option>
            ))}
          </select>
        </label>

        <label>
          Amount (ZAR)
          <input
            type="number"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="0.00"
          />
        </label>

        <label>
          Notes (optional)
          <input
            type="text"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Reason for withdrawal"
          />
        </label>

        {validationError && <p className="error">{validationError}</p>}
        {error && <p className="error">{error}</p>}
        {success && <p className="success">{success}</p>}

        <button type="submit" disabled={submitting}>
          {submitting ? 'Submitting...' : 'Submit Withdrawal'}
        </button>
      </form>
    </section>
  )
}
