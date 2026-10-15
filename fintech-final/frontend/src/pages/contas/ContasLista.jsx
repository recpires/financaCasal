import { useState } from 'react'
import { Link, useLocation, useSearchParams } from 'react-router-dom'
import Alert from '../../components/Alert.jsx'
import Icon from '../../components/Icon.jsx'
import Loading from '../../components/Loading.jsx'
import PageHeader from '../../components/PageHeader.jsx'
import useLista from '../../hooks/useLista.js'
import { clienteApi, contaApi } from '../../services/api.js'
import { TIPOS_CONTA, formatarData, formatarMoeda } from '../../utils/format.js'

export default function ContasLista() {
  const location = useLocation()
  const [params, setParams] = useSearchParams()
  const clienteId = params.get('clienteId') || ''

  const { itens: contas, carregando, erro, recarregar } = useLista(() => contaApi.listar({ clienteId }), [clienteId])
  const { itens: clientes } = useLista(() => clienteApi.listar())
  const [mensagem, setMensagem] = useState(location.state?.mensagem || '')
  const [erroAcao, setErroAcao] = useState('')

  function filtrar(evento) {
    setParams(evento.target.value ? { clienteId: evento.target.value } : {})
  }

  async function excluir(conta) {
    if (!window.confirm(`Encerrar a conta ${conta.numero}? O extrato dela também será removido.`)) return
    setMensagem('')
    setErroAcao('')
    try {
      await contaApi.excluir(conta.id)
      setMensagem(`Conta ${conta.numero} encerrada.`)
      recarregar()
    } catch (e) {
      setErroAcao(e.message)
    }
  }

  return (
    <>
      <PageHeader titulo="Contas" subtitulo="Contas correntes e poupanças dos clientes.">
        <Link to={clienteId ? `/contas/nova?clienteId=${clienteId}` : '/contas/nova'} className="botao botao-primario">
          <Icon nome="mais" /> Nova conta
        </Link>
      </PageHeader>

      <Alert tipo="sucesso" mensagem={mensagem} onFechar={() => setMensagem('')} />
      <Alert mensagem={erro || erroAcao} onFechar={() => setErroAcao('')} />

      <section className="card">
        <div className="filtros">
          <label className="campo campo-inline">
            <span className="campo-rotulo">Cliente</span>
            <select value={clienteId} onChange={filtrar}>
              <option value="">Todos os clientes</option>
              {clientes.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
            </select>
          </label>
        </div>

        {carregando ? <Loading /> : contas.length === 0 ? (
          <p className="vazio">Nenhuma conta encontrada.</p>
        ) : (
          <div className="tabela-wrapper">
            <table className="tabela">
              <thead>
                <tr>
                  <th>Conta</th>
                  <th>Tipo</th>
                  <th>Titular</th>
                  <th>Abertura</th>
                  <th className="direita">Saldo</th>
                  <th>Condições</th>
                  <th>Status</th>
                  <th aria-label="Ações" />
                </tr>
              </thead>
              <tbody>
                {contas.map((conta) => (
                  <tr key={conta.id}>
                    <td className="mono">Ag {conta.agencia} · {conta.numero}</td>
                    <td>{TIPOS_CONTA[conta.tipo]}</td>
                    <td>{conta.cliente.nome}</td>
                    <td>{formatarData(conta.dataAbertura)}</td>
                    <td className={`direita ${conta.saldo < 0 ? 'valor-saida' : ''}`}>{formatarMoeda(conta.saldo)}</td>
                    <td>
                      {conta.tipo === 'CORRENTE'
                        ? `Limite ${formatarMoeda(conta.limiteChequeEspecial)}`
                        : `Rende ${(conta.taxaRendimentoMensal * 100).toFixed(2).replace('.', ',')}% a.m.`}
                    </td>
                    <td>
                      <span className={conta.ativa ? 'etiqueta etiqueta-ok' : 'etiqueta'}>
                        {conta.ativa ? 'Ativa' : 'Inativa'}
                      </span>
                    </td>
                    <td className="tabela-acoes">
                      <Link to={`/transacoes?contaId=${conta.id}`} className="link">Extrato</Link>
                      <Link to={`/contas/${conta.id}/editar`} className="botao-icone" title="Editar">
                        <Icon nome="editar" />
                      </Link>
                      <button type="button" className="botao-icone perigo" title="Encerrar"
                        onClick={() => excluir(conta)}>
                        <Icon nome="lixeira" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  )
}
