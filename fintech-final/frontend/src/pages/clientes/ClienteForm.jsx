import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Alert from '../../components/Alert.jsx'
import FormField from '../../components/FormField.jsx'
import Icon from '../../components/Icon.jsx'
import Loading from '../../components/Loading.jsx'
import PageHeader from '../../components/PageHeader.jsx'
import { clienteApi } from '../../services/api.js'

const VAZIO = { nome: '', cpf: '', email: '', telefone: '', idade: '' }

/** Mesmo formulario para cadastrar (/clientes/novo) e editar (/clientes/:id/editar). */
export default function ClienteForm() {
  const { id } = useParams()
  const editando = Boolean(id)
  const navigate = useNavigate()

  const [form, setForm] = useState(VAZIO)
  const [carregando, setCarregando] = useState(editando)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [salvando, setSalvando] = useState(false)

  useEffect(() => {
    if (!editando) return
    clienteApi.buscar(id)
      .then((cliente) => setForm({ ...cliente, telefone: cliente.telefone || '' }))
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
    const dados = { ...form, idade: form.idade === '' ? null : Number(form.idade) }
    try {
      if (editando) await clienteApi.atualizar(id, dados)
      else await clienteApi.criar(dados)
      navigate('/clientes', { state: { mensagem: `Cliente ${form.nome} ${editando ? 'atualizado' : 'cadastrado'}.` } })
    } catch (e) {
      setErro(e.message)
      setErros(e.campos || {})
    } finally {
      setSalvando(false)
    }
  }

  if (carregando) return <Loading />

  return (
    <>
      <PageHeader titulo={editando ? 'Editar cliente' : 'Novo cliente'}
        subtitulo="Clientes com 21 anos ou mais podem ter cheque especial.">
        <Link to="/clientes" className="botao"><Icon nome="voltar" /> Voltar</Link>
      </PageHeader>

      <form className="card formulario" onSubmit={salvar} noValidate>
        <Alert mensagem={erro} />

        <div className="grade-campos">
          <FormField rotulo="Nome completo" erro={erros.nome}>
            <input name="nome" value={form.nome} onChange={alterar} required />
          </FormField>
          <FormField rotulo="CPF" erro={erros.cpf} dica="Somente números ou no formato 000.000.000-00">
            <input name="cpf" value={form.cpf} onChange={alterar} inputMode="numeric" maxLength={14} required />
          </FormField>
          <FormField rotulo="E-mail" erro={erros.email}>
            <input name="email" type="email" value={form.email} onChange={alterar} required />
          </FormField>
          <FormField rotulo="Telefone" erro={erros.telefone}>
            <input name="telefone" value={form.telefone} onChange={alterar} />
          </FormField>
          <FormField rotulo="Idade" erro={erros.idade}>
            <input name="idade" type="number" min="18" value={form.idade} onChange={alterar} required />
          </FormField>
        </div>

        <div className="formulario-acoes">
          <Link to="/clientes" className="botao">Cancelar</Link>
          <button type="submit" className="botao botao-primario" disabled={salvando}>
            {salvando ? 'Salvando...' : 'Salvar'}
          </button>
        </div>
      </form>
    </>
  )
}
