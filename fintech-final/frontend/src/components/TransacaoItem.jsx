import { TIPOS_TRANSACAO, formatarDataHora, formatarMoeda } from '../utils/format.js'

/** Linha de transacao no estilo de extrato: entradas em verde, saidas em vermelho. */
export default function TransacaoItem({ transacao }) {
  const { rotulo, credito } = TIPOS_TRANSACAO[transacao.tipo]

  return (
    <div className="transacao-item">
      <div className={credito ? 'bolinha bolinha-entrada' : 'bolinha bolinha-saida'}>
        {credito ? '+' : '−'}
      </div>
      <div className="transacao-info">
        <p>{transacao.descricao}</p>
        <small>
          {rotulo} · {transacao.conta.cliente.nome} · {formatarDataHora(transacao.dataHora)}
        </small>
      </div>
      <span className={credito ? 'valor-entrada' : 'valor-saida'}>
        {credito ? '+ ' : '− '}{formatarMoeda(transacao.valor)}
      </span>
    </div>
  )
}
