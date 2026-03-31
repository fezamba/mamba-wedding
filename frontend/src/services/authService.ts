import type { LoginResponse } from '../types/auth'
import { apiFetch } from './api'

export async function login(codigoConvite: string): Promise<LoginResponse> {
  const response = await apiFetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ codigoConvite }),
  })

  if (!response.ok) {
    throw new Error('Código de convite inválido')
  }

  const data: LoginResponse = await response.json()
  return data
}