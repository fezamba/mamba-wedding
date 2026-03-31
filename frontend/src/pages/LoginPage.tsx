import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function LoginPage() {
  const [codigoConvite, setCodigoConvite] = useState('')
  const [erro, setErro] = useState('')
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()

    const codigoTratado = codigoConvite.trim()

    if (!codigoTratado) {
      setErro('Informe o código do convite.')
      return
    }

    try {
      setLoading(true)
      setErro('')
      await login(codigoTratado)
      navigate('/rsvp')
    } catch (error) {
      setErro('Não foi possível entrar. Verifique o código do convite.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center px-6">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg border border-slate-200 p-8">
        <h2 className="text-3xl font-bold text-slate-900 mb-2">
          Bem-vindo
        </h2>
        <p className="text-slate-600 mb-6">
          Informe seu código de convite para acessar a área do convidado.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label
              htmlFor="codigoConvite"
              className="block text-sm font-medium text-slate-700 mb-1"
            >
              Código do convite
            </label>
            <input
              id="codigoConvite"
              type="text"
              value={codigoConvite}
              onChange={(e) => setCodigoConvite(e.target.value)}
              placeholder="Ex: ABC123"
              className="w-full border border-slate-300 rounded-lg px-4 py-2 outline-none focus:ring-2 focus:ring-slate-400"
            />
          </div>

          {erro && (
            <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 text-sm">
              {erro}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-slate-800 text-white py-2.5 rounded-lg hover:bg-slate-700 transition disabled:opacity-60"
          >
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default LoginPage