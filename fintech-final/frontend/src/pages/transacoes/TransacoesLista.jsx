import { useState } from 'react'
import { Link, useLocation, useSearchParams } from 'react-router-dom'
import Alert from '../../components/Alert.jsx'
import Icon from '../../components/Icon.jsx'
import Loading from '../../components/Loading.jsx'
import PageHeader from '../../components/PageHeader.jsx'
import useLista from '../../hooks/useLista.js'
import { contaApi, transacaoApi } from '../../services/api.js'
import { TIPOS_TRANSACAO, descreverConta, formatarDataHora, formatarMoeda } from '../../utils/format.js'

export default function TransacoesLista() {
  const location = useLocation()
  const [params, setParams] = useSearchParams()
  const contaId = params.get('contaId') || ''

  const { itens: transacoes, carregando, erro, recarregar } = useLista(
    () => transacaoApi.listar({ contaId }), [contaId])
  // As contas sao recarregadas junto para o saldo do resumo acompanhar os estornos
  const { itens: contas, recarregar: recarregarContas } = useLista(() => contaApi.listar())
  const [mensagem, setMensagem] = useState(location.state?.mensagem || '')
  const [erroAcao, setErroAcao] = useState('')

  const contaSelecionada = contas.find((c) => String(c.id) === contaId)

  function filtrar(evento) {
    setParams(evento.target.value ? { contaId: evento.target.value } : {})
  }

  async function excluir(transacao) {
    if (!window.confirm(`Excluir "${transacao.descricao}"? O valor será estornado no saldo da conta.`)) return
    setMensagem('')
    setErroAcao('')
    try {
      await transacaoApi.excluir(transacao.id)
      setMensagem('Transação excluída e valor estornado.')
      recarregar()
      recarregarContas()
    } catch (e) {
      setErroAcao(e.message)
    }
  }

  return (
    <>
      <PageHeader titulo="Transações" subtitulo="Extrato de movimentações. Cada transação altera o saldo da conta.">
        <Link to={contaId ? `/transacoes/nova?contaId=${contaId}` : '/transacoes/nova'} className="botao botao-primario">
          <Icon nome="mais" /> Nova transação
        </Link>
      </PageHeader>

      <Alert tipo="sucesso" mensagem={mensagem} onFechar={() => setMensagem('')} />
      <Alert mensagem={erro || erroAcao} onFechar={() => setErroAcao('')} />

      <section className="card">
        <div className="filtros">
          <label className="campo campo-inline">
            <span className="campo-rotulo">Conta</span>
            <select value={contaId} onChange={filtrar}>
              <option value="">Todas as contas</option>
              {contas.map((c) => <option key={c.id} value={c.id}>{descreverConta(c)}</option>)}
            </select>
          </label>
          {contaSelecionada && (
            <div className="resumo-conta">
              Saldo atual <strong>{formatarMoeda(contaSelecionada.saldo)}</strong>
              <small>Disponível {formatarMoeda(contaSelecionada.saldoDisponivel)}</small>
            </div>
          )}
        </div>

        {carregando ? <Loading /> : transacoes.length === 0 ? (
          <p className="vazio">Nenhuma transação encontrada.</p>
        ) : (
          <div className="tabela-wrapper">
            <table className="tabela">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Descrição</th>
                  <th>Tipo</th>
                  <th>Conta</th>
                  <th className="direita">Valor</th>
                  <th aria-label="Ações" />
                </tr>
              </thead>
              <tbody>
                {transacoes.map((t) => {
                  const { rotulo, credito } = TIPOS_TRANSACAO[t.tipo]
                  return (
                    <tr key={t.id}>
                      <td>{formatarDataHora(t.dataHora)}</td>
                      <td>{t.descricao}</td>
                      <td>{rotulo}</td>
                      <td>{t.conta.numero} · {t.conta.cliente.nome}</td>
                      <td className={`direita ${credito ? 'valor-entrada' : 'valor-saida'}`}>
                        {credito ? '+ ' : '− '}{formatarMoeda(t.valor)}
                      </td>
                      <td className="tabela-acoes">
                        <Link to={`/transacoes/${t.id}/editar`} className="botao-icone" title="Editar">
                          <Icon nome="editar" />
                        </Link>
                        <button type="button" className="botao-icone perigo" title="Excluir"
                          onClick={() => excluir(t)}>
                          <Icon nome="lixeira" />
                        </button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  )
}
