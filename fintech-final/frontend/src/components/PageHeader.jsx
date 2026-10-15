/** Titulo da pagina com subtitulo opcional e uma area de acoes (botoes) a direita. */
export default function PageHeader({ titulo, subtitulo, children }) {
  return (
    <header className="page-header">
      <div>
        <h1>{titulo}</h1>
        {subtitulo && <p>{subtitulo}</p>}
      </div>
      {children && <div className="acoes">{children}</div>}
    </header>
  )
}
