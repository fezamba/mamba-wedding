import {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { login as loginRequest } from '../services/authService'
import { getToken, removeToken, setToken } from '../services/api'

type AuthContextType = {
  token: string | null
  isAuthenticated: boolean
  login: (codigoConvite: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

type AuthProviderProps = {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setTokenState] = useState<string | null>(null)

  useEffect(() => {
    const storedToken = getToken()
    if (storedToken) {
      setTokenState(storedToken)
    }
  }, [])

  async function login(codigoConvite: string) {
    const response = await loginRequest(codigoConvite)
    setToken(response.token)
    setTokenState(response.token)
  }

  function logout() {
    removeToken()
    setTokenState(null)
  }

  const value = useMemo(
    () => ({
      token,
      isAuthenticated: !!token,
      login,
      logout,
    }),
    [token]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider')
  }

  return context
}