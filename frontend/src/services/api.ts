const API_BASE_URL = 'http://localhost:8080'

export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token: string) {
  localStorage.setItem('token', token)
}

export function removeToken() {
  localStorage.removeItem('token')
}

export async function apiFetch(endpoint: string, options: RequestInit = {}) {
  const token = getToken()

  const headers = new Headers(options.headers)

  headers.set('Content-Type', 'application/json')

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  })

  return response
}