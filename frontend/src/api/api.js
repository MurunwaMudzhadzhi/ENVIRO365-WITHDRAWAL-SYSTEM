import axios from 'axios'

const client = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export const getInvestors = () => client.get('/investors').then(r => r.data)

export const getPortfolio = (investorId) =>
  client.get(`/investors/${investorId}/portfolio`).then(r => r.data)

export const getWithdrawalHistory = (investorId) =>
  client.get('/withdrawals', { params: { investorId } }).then(r => r.data)

export const createWithdrawal = (payload) =>
  client.post('/withdrawals', payload).then(r => r.data)

export const downloadCsv = async (investorId, productId) => {
  const response = await client.get('/withdrawals/export', {
    params: { investorId, productId: productId || undefined },
    responseType: 'blob',
  })
  const url = window.URL.createObjectURL(new Blob([response.data]))
  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', `withdrawal-statement-${investorId}.csv`)
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}

/** Normalises Axios/backend errors into a single user-facing message string. */
export const extractErrorMessage = (error) => {
  const data = error?.response?.data
  if (!data) return 'Something went wrong. Please check your connection and try again.'
  if (data.fieldErrors) {
    return Object.values(data.fieldErrors).join(' ')
  }
  return data.message || 'An unexpected error occurred.'
}
