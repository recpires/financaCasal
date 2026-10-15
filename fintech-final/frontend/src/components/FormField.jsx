/** Rotulo + campo + mensagem de erro vinda da validacao do backend. */
export default function FormField({ rotulo, erro, dica, children }) {
  return (
    <label className={erro ? 'campo campo-erro' : 'campo'}>
      <span className="campo-rotulo">{rotulo}</span>
      {children}
      {erro ? <small className="campo-msg">{erro}</small> : dica && <small className="campo-dica">{dica}</small>}
    </label>
  )
}
