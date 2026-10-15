import { Link } from 'react-router-dom'

/** Pagina de erro personalizada para qualquer rota inexistente. */
export default function NotFound() {
  return (
    <div className="erro-pagina">
      <div className="card erro-card">
        <span className="erro-codigo">404</span>
        <h1>Página não encontrada</h1>
        <p>O endereço que você tentou abrir não existe ou foi removido.</p>
        <Link to="/" className="botao botao-primario">Voltar para o início</Link>
      </div>
    </div>
  )
}
