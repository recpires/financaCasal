import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Alert from '../components/Alert.jsx'
import Icon from '../components/Icon.jsx'
import Loading from '../components/Loading.jsx'
import PageHeader from '../components/PageHeader.jsx'
import StatCard from '../components/StatCard.jsx'
import TransacaoItem from '../components/TransacaoItem.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { clienteApi, contaApi, transacaoApi } from '../services/api.js'
import { TIPOS_TRANSACAO, formatarMoeda } from '../utils/format.js'

export default function Home() {
  const { usuario } = useAuth()
  const [dados, setDados] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => {
    Promise.all([clienteApi.listar(), contaApi.listar(), transacaoApi.listar()])
      .then(([clientes, contas, transacoes]) => setDados({ clientes, contas, transacoes }))
      .catch((e) => setErro(e.message))
  }, [])

  if (erro) return <Alert mensagem={erro} />
  if (!dados) return <Loading />

  const { clientes, contas, transacoes } = dados
  const saldoTotal = contas.reduce((soma, conta) => soma + Number(conta.saldo), 0)
  const somar = (credito) => transacoes
    .filter((t) => TIPOS_TRANSACAO[t.tipo].credito === credito)
    .reduce((soma, t) => soma + Number(t.valor), 0)

  return (
    <>
      <PageHeader titulo={`Olá, ${usuario.nome.split(' ')[0]}`}
        subtitulo="Resumo financeiro de todas as contas cadastradas.">
        <Link to="/transacoes/nova" className="botao botao-primario">
          <Icon nome="mais" /> Nova transação
        </Link>
      </PageHeader>

      <div className="grade-stats">
        <StatCard titulo="Saldo total" valor={formatarMoeda(saldoTotal)}
          detalhe={`${contas.length} contas de ${clientes.length} clientes`} />
        <StatCard titulo="Entradas" valor={formatarMoeda(somar(true))} tom="positivo"
          detalhe="Depósitos e rendimentos" />
        <StatCard titulo="Saídas" valor={formatarMoeda(somar(false))} tom="negativo"
          detalhe="Saques, pagamentos e taxas" />
      </div>

      <section className="card">
        <div className="secao-topo">
          <h2>Transações recentes</h2>
          <Link to="/transacoes" className="link">Ver extrato completo</Link>
        </div>
        {transacoes.length === 0
          ? <p className="vazio">Nenhuma transação registrada ainda.</p>
          : (
            <div className="lista-transacoes">
              {transacoes.slice(0, 6).map((t) => <TransacaoItem key={t.id} transacao={t} />)}
            </div>
          )}
      </section>
    </>
  )
}
