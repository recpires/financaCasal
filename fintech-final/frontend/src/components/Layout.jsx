import { Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import Sidebar from './Sidebar.jsx'

/** Estrutura das paginas internas: menu lateral + conteudo da rota atual. */
export default function Layout() {
  const { usuario, logout } = useAuth()
  const navigate = useNavigate()

  function sair() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="app">
      <Sidebar usuario={usuario} onSair={sair} />
      <main className="conteudo">
        <Outlet />
      </main>
    </div>
  )
}
