export default function Loading({ texto = 'Carregando...' }) {
  return (
    <div className="loading" role="status">
      <span className="spinner" aria-hidden="true" />
      {texto}
    </div>
  )
}
