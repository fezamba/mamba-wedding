import Header from '../components/Header'

function GiftsPage() {
  return (
    <div className="min-h-screen bg-slate-100">
      <Header />
      <main className="max-w-5xl mx-auto p-6">
        <div className="bg-white rounded-xl shadow p-6 border border-slate-200">
          <h2 className="text-2xl font-bold text-slate-900 mb-2">Presentes</h2>
          <p className="text-slate-600">
            Próxima etapa: listar presentes disponíveis, reservados e comprados.
          </p>
        </div>
      </main>
    </div>
  )
}

export default GiftsPage