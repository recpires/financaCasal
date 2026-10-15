import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

/** Se nao houver usuario logado, manda para o login guardando a pagina que ele tentou abrir. */
export default function PrivateRoute() {
  const { usuario } = useAuth()
  const location = useLocation()

  if (!usuario) {
    return <Navigate to="/login" replace state={{ origem: location.pathname + location.search }} />
  }
  return <Outlet />
}
