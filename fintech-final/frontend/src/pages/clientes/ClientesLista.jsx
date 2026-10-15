import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import Alert from '../../components/Alert.jsx'
import Icon from '../../components/Icon.jsx'
import Loading from '../../components/Loading.jsx'
import PageHeader from '../../components/PageHeader.jsx'
import useLista from '../../hooks/useLista.js'
import { clienteApi } from '../../services/api.js'
import { formatarCpf } from '../../utils/format.js'

export default function ClientesLista() {
  const location = useLocation()
  const { itens: clientes, carregando, erro, recarregar } = useLista(() => clienteApi.listar())
  const [mensagem, setMensagem] = useState(location.state?.mensagem || '')
  const [erroAcao, setErroAcao] = useState('')

  async function excluir(cliente) {
    if (!window.confirm(`Excluir o cliente ${cliente.nome}?`)) return
    setMensagem('')
    setErroAcao('')
    try {
      await clienteApi.excluir(cliente.id)
      setMensagem(`Cliente ${cliente.nome} excluído.`)
      recarregar()
    } catch (e) {
      setErroAcao(e.message)
    }
  }

  return (
    <>
      <PageHeader titulo="Clientes" subtitulo="Titulares das contas do Fintech.">
        <Link to="/clientes/novo" className="botao botao-primario"><Icon nome="mais" /> Novo cliente</Link>
      </PageHeader>

      <Alert tipo="sucesso" mensagem={mensagem} onFechar={() => setMensagem('')} />
      <Alert mensagem={erro || erroAcao} onFechar={() => setErroAcao('')} />

      <section className="card">
        {carregando ? <Loading /> : clientes.length === 0 ? (
          <p className="vazio">Nenhum cliente cadastrado.</p>
        ) : (
          <div className="tabela-wrapper">
            <table className="tabela">
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>CPF</th>
                  <th>E-mail</th>
                  <th>Telefone</th>
                  <th>Idade</th>
                  <th>Crédito</th>
                  <th aria-label="Ações" />
                </tr>
              </thead>
              <tbody>
                {clientes.map((cliente) => (
                  <tr key={cliente.id}>
                    <td>{cliente.nome}</td>
                    <td className="mono">{formatarCpf(cliente.cpf)}</td>
                    <td>{cliente.email}</td>
                    <td>{cliente.telefone || '-'}</td>
                    <td>{cliente.idade}</td>
                    <td>
                      <span className={cliente.elegivelParaCredito ? 'etiqueta etiqueta-ok' : 'etiqueta'}>
                        {cliente.elegivelParaCredito ? 'Elegível' : 'Não elegível'}
                      </span>
                    </td>
                    <td className="tabela-acoes">
                      <Link to={`/contas?clienteId=${cliente.id}`} className="link">Contas</Link>
                      <Link to={`/clientes/${cliente.id}/editar`} className="botao-icone" title="Editar">
                        <Icon nome="editar" />
                      </Link>
                      <button type="button" className="botao-icone perigo" title="Excluir"
                        onClick={() => excluir(cliente)}>
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
