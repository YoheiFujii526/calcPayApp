const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

export const memberApi = {
  list: () => request('/members'),
  create: (payload) => request('/members', { method: 'POST', body: JSON.stringify(payload) }),
  update: (id, payload) => request(`/members/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  remove: (id) => request(`/members/${id}`, { method: 'DELETE' })
}

export const eventApi = {
  list: () => request('/events'),
  create: (payload) => request('/events', { method: 'POST', body: JSON.stringify(payload) }),
  update: (id, payload) => request(`/events/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  detail: (id) => request(`/events/${id}`),
  remove: (id) => request(`/events/${id}`, { method: 'DELETE' })
}

export const expenseApi = {
  list: (eventId) => request(`/events/${eventId}/expenses`),
  create: (eventId, payload) => request(`/events/${eventId}/expenses`, { method: 'POST', body: JSON.stringify(payload) }),
  update: (id, payload) => request(`/expenses/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  remove: (id) => request(`/expenses/${id}`, { method: 'DELETE' })
}

export const repaymentApi = {
  list: (eventId) => request(`/events/${eventId}/repayments`),
  create: (eventId, payload) => request(`/events/${eventId}/repayments`, { method: 'POST', body: JSON.stringify(payload) }),
  update: (id, payload) => request(`/repayments/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  remove: (id) => request(`/repayments/${id}`, { method: 'DELETE' })
}

export const settlementApi = {
  get: (eventId) => request(`/events/${eventId}/settlement`)
}

export const participantApi = {
  list: (eventId) => request(`/events/${eventId}/participants`),
  add: (eventId, memberIds) => request(`/events/${eventId}/participants`, {
    method: 'POST',
    body: JSON.stringify({ memberIds })
  }),
  remove: (eventId, memberId) => request(`/events/${eventId}/participants/${memberId}`, { method: 'DELETE' })
}
