/** Mensagem de sucesso ou erro. Nao renderiza nada se nao houver mensagem. */
export default function Alert({ tipo = 'erro', mensagem, onFechar }) {
  if (!mensagem) return null

  return (
    <div className={`alerta alerta-${tipo}`} role={tipo === 'erro' ? 'alert' : 'status'}>
      <span>{mensagem}</span>
      {onFechar && (
        <button type="button" className="alerta-fechar" onClick={onFechar} aria-label="Fechar">×</button>
      )}
    </div>
  )
}
