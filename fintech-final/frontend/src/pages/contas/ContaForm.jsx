import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import Alert from '../../components/Alert.jsx'
import FormField from '../../components/FormField.jsx'
import Icon from '../../components/Icon.jsx'
import Loading from '../../components/Loading.jsx'
import PageHeader from '../../components/PageHeader.jsx'
import { clienteApi, contaApi } from '../../services/api.js'
import { TIPOS_CONTA, formatarMoeda } from '../../utils/format.js'

/** Cadastro e edicao de conta. Na edicao, titular e tipo ficam bloqueados (regra do backend). */
export default function ContaForm() {
  const { id } = useParams()
  const [params] = useSearchParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [clientes, setClientes] = useState([])
  const [form, setForm] = useState({
    clienteId: params.get('clienteId') || '',
    tipo: 'CORRENTE',
    agencia: '0001',
    limiteChequeEspecial: '1000',
    taxaPercentual: '0.5',
    ativa: true,
  })
  const [saldo, setSaldo] = useState(null)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    const carregarConta = editando ? contaApi.buscar(id) : Promise.resolve(null)
    Promise.all([clienteApi.listar(), carregarConta])
      .then(([listaClientes, conta]) => {
        setClientes(listaClientes)
        if (conta) {
          setSaldo(conta.saldo)
          setForm({
            clienteId: String(conta.cliente.id),
            tipo: conta.tipo,
            agencia: conta.agencia,
            limiteChequeEspecial: conta.limiteChequeEspecial ?? '',
            taxaPercentual: conta.taxaRendimentoMensal != null ? String(conta.taxaRendimentoMensal * 100) : '',
            ativa: conta.ativa,
          })
        }
      })
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false))
  }, [id, editando])

  function alterar(evento) {
    const { name, value, type, checked } = evento.target
    setForm((atual) => ({ ...atual, [name]: type === 'checkbox' ? checked : value }))
  }

  async function salvar(evento) {
    evento.preventDefault()
    setErro('')
    setErros({})
    setSalvando(true)

    const corrente = form.tipo === 'CORRENTE'
    const dados = {
      clienteId: form.clienteId ? Number(form.clienteId) : null,
      tipo: form.tipo,
      agencia: form.agencia,
      limiteChequeEspecial: corrente && form.limiteChequeEspecial !== '' ? Number(form.limiteChequeEspecial) : null,
      // Na tela a taxa e digitada em % (0,5); a API recebe a fracao (0.005)
      taxaRendimentoMensal: !corrente && form.taxaPercentual !== ''
        ? Number((Number(String(form.taxaPercentual).replace(',', '.')) / 100).toFixed(4)) : null,
      ativa: form.ativa,
    }

    try {
      const conta = editando ? await contaApi.atualizar(id, dados) : await contaApi.criar(dados)
      navigate('/contas', { state: { mensagem: `Conta ${conta.numero} ${editando ? 'atualizada' : 'aberta'}.` } })
    } catch (e) {
      setErro(e.message)
      setErros(e.campos || {})
    } finally {
      setSalvando(false)
    }
  }

  if (carregando) return <Loading />

  const clienteSelecionado = clientes.find((c) => String(c.id) === String(form.clienteId))

  return (
    <>
      <PageHeader titulo={editando ? 'Editar conta' : 'Nova conta'}
        subtitulo={editando ? `Saldo atual: ${formatarMoeda(saldo)}` : 'A conta é aberta com saldo zero.'}>
        <Link to="/contas" className="botao"><Icon nome="voltar" /> Voltar</Link>
      </PageHeader>

      <form className="card formulario" onSubmit={salvar} noValidate>
        <Alert mensagem={erro} />

        <div className="grade-campos">
          <FormField rotulo="Titular" erro={erros.clienteId}>
            <select name="clienteId" value={form.clienteId} onChange={alterar} disabled={editando} required>
              <option value="">Selecione o cliente</option>
              {clientes.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
            </select>
          </FormField>
          <FormField rotulo="Tipo de conta" erro={erros.tipo}>
            <select name="tipo" value={form.tipo} onChange={alterar} disabled={editando}>
              {Object.entries(TIPOS_CONTA).map(([valor, rotulo]) => <option key={valor} value={valor}>{rotulo}</option>)}
            </select>
          </FormField>
          <FormField rotulo="Agência" erro={erros.agencia} dica="4 dígitos">
            <input name="agencia" value={form.agencia} onChange={alterar} inputMode="numeric" maxLength={4} />
          </FormField>

          {form.tipo === 'CORRENTE' ? (
            <FormField rotulo="Limite do cheque especial (R$)" erro={erros.limiteChequeEspecial}
              dica={clienteSelecionado && !clienteSelecionado.elegivelParaCredito
                ? 'Cliente menor de 21 anos: o limite será zerado.' : undefined}>
              <input name="limiteChequeEspecial" type="number" min="0" step="0.01"
                value={form.limiteChequeEspecial} onChange={alterar} />
            </FormField>
          ) : (
            <FormField rotulo="Rendimento mensal (%)" erro={erros.taxaRendimentoMensal} dica="Entre 0% e 5% ao mês">
              <input name="taxaPercentual" type="number" min="0" max="5" step="0.01"
                value={form.taxaPercentual} onChange={alterar} />
            </FormField>
          )}

          <label className="campo-checkbox">
            <input type="checkbox" name="ativa" checked={form.ativa} onChange={alterar} />
            Conta ativa (contas inativas não aceitam transações)
          </label>
        </div>

        <div className="formulario-acoes">
          <Link to="/contas" className="botao">Cancelar</Link>
          <button type="submit" className="botao botao-primario" disabled={salvando}>
            {salvando ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </>
  )
}
