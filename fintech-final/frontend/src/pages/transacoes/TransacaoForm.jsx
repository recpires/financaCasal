import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import Alert from '../../components/Alert.jsx'
import FormField from '../../components/FormField.jsx'
import Icon from '../../components/Icon.jsx'
import Loading from '../../components/Loading.jsx'
import PageHeader from '../../components/PageHeader.jsx'
import { contaApi, transacaoApi } from '../../services/api.js'
import { TIPOS_TRANSACAO, descreverConta, formatarMoeda } from '../../utils/format.js'

/** Cadastro e edicao de transacao. Ao editar, o backend desfaz o valor antigo e aplica o novo. */
export default function TransacaoForm() {
  const { id } = useParams()
  const [params] = useSearchParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [contas, setContas] = useState([])
  const [form, setForm] = useState({
    contaId: params.get('contaId') || '',
    tipo: 'DEPOSITO',
    valor: '',
    descricao: '',
  })
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    const carregarTransacao = editando ? transacaoApi.buscar(id) : Promise.resolve(null)
    Promise.all([contaApi.listar(), carregarTransacao])
      .then(([listaContas, transacao]) => {
        setContas(listaContas)
        if (transacao) {
          setForm({
            contaId: String(transacao.conta.id),
            tipo: transacao.tipo,
            valor: String(transacao.valor),
            descricao: transacao.descricao,
          })
        }
      })
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }, [id, editando])

  function alterar(evento) {
    const { name, value } = evento.target
    setForm((atual) => ({ ...atual, [name]: value }))
  }

  async function salvar(evento) {
    evento.preventDefault()
    setErro('')
    setErros({})
    setSalvando(true)
    const dados = {
      contaId: form.contaId ? Number(form.contaId) : null,
      tipo: form.tipo,
      valor: form.valor === '' ? null : Number(String(form.valor).replace(',', '.')),
      descricao: form.descricao,
    }
    try {
      if (editando) await transacaoApi.atualizar(id, dados)
      else await transacaoApi.criar(dados)
      navigate(`/transacoes?contaId=${dados.contaId}`, {
        state: { mensagem: `Transação ${editando ? 'atualizada' : 'registrada'} e saldo atualizado.` },
      })
    } catch (e) {
      setErro(e.message)
      setErros(e.campos || {})
    } finally {
      setSalvando(false)
    }
  }

  if (carregando) return <Loading />

  // Conta nova so pode receber transacao se estiver ativa
  const opcoesConta = editando ? contas : contas.filter((c) => c.ativa)
  const contaSelecionada = contas.find((c) => String(c.id) === String(form.contaId))

  return (
    <>
      <PageHeader titulo={editando ? 'Editar transação' : 'Nova transação'}
        subtitulo={contaSelecionada
          ? `Saldo disponível na conta: ${formatarMoeda(contaSelecionada.saldoDisponivel)}`
          : 'Escolha a conta que vai receber a movimentação.'}>
        <Link to="/transacoes" className="botao"><Icon nome="voltar" /> Voltar</Link>
      </PageHeader>

      <form className="card formulario" onSubmit={salvar} noValidate>
        <Alert mensagem={erro} />

        <div className="grade-campos">
          <FormField rotulo="Conta" erro={erros.contaId}>
            <select name="contaId" value={form.contaId} onChange={alterar} disabled={editando} required>
              <option value="">Selecione a conta</option>
              {opcoesConta.map((c) => <option key={c.id} value={c.id}>{descreverConta(c)}</option>)}
            </select>
          </FormField>
          <FormField rotulo="Tipo" erro={erros.tipo}>
            <select name="tipo" value={form.tipo} onChange={alterar}>
              {Object.entries(TIPOS_TRANSACAO).map(([valor, { rotulo, credito }]) => (
                <option key={valor} value={valor}>{rotulo} ({credito ? 'entrada' : 'saída'})</option>
              ))}
            </select>
          </FormField>
          <FormField rotulo="Valor (R$)" erro={erros.valor}>
            <input name="valor" type="number" min="0.01" step="0.01" value={form.valor} onChange={alterar} required />
          </FormField>
          <FormField rotulo="Descrição" erro={erros.descricao}>
            <input name="descricao" value={form.descricao} onChange={alterar} maxLength={150} required />
          </FormField>
        </div>

        <div className="formulario-acoes">
          <Link to="/transacoes" className="botao">Cancelar</Link>
          <button type="submit" className="botao botao-primario" disabled={salvando}>
            {salvando ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </>
  )
}
