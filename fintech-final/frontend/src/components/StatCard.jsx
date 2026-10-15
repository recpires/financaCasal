/** Card de indicador da pagina inicial. "tom" muda a cor do valor (positivo, negativo ou neutro). */
export default function StatCard({ titulo, valor, detalhe, tom = 'neutro' }) {
  return (
    <div className={`card stat stat-${tom}`}>
      <h3>{titulo}</h3>
      <p className="stat-valor">{valor}</p>
      {detalhe && <small>{detalhe}</small>}
    </div>
  )
}
