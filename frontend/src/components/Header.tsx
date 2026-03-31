import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

function Header() {
  const { isAuthenticated, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <header className="bg-white border-b border-slate-200 shadow-sm">
      <div className="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-bold text-slate-800">Nosso Evento</h1>
          <p className="text-sm text-slate-500">Área do convidado</p>
        </div>

        {isAuthenticated && (
          <nav className="flex items-center gap-4">
            <Link to="/rsvp" className="text-slate-700 hover:text-slate-900">
              RSVP
            </Link>
            <Link to="/gifts" className="text-slate-700 hover:text-slate-900">
              Presentes
            </Link>
            <Link
              to="/messages"
              className="text-slate-700 hover:text-slate-900"
            >
              Mensagens
            </Link>
            <button
              onClick={handleLogout}
              className="bg-slate-800 text-white px-4 py-2 rounded-lg hover:bg-slate-700 transition"
            >
              Sair
            </button>
          </nav>
        )}
      </div>
    </header>
  )
}

export default Header