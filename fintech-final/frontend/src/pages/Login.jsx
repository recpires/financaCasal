import { useState } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import Alert from '../components/Alert.jsx'
import FormField from '../components/FormField.jsx'
import Icon from '../components/Icon.jsx'
import { useAuth } from '../context/AuthContext.jsx'

export default function Login() {
  const { usuario, login } = useAuth()
  const location = useLocation()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  // Volta para a pagina que o usuario tentou abrir antes de ser mandado para o login
  const destino = location.state?.origem || '/'

  // Ja logado (inclusive logo apos entrar): segue para o destino
  if (usuario) return <Navigate to={destino} replace />

  async function entrar(evento) {
    evento.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      // Ao salvar o usuario, a pagina se redesenha e o <Navigate> acima leva ao destino
      await login(email, senha)
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <div className="login-pagina">
      <form className="card login-card" onSubmit={entrar}>
        <div className="marca marca-login">
          <Icon nome="carteira" tamanho={28} />
          <span>Finanças Casal</span>
        </div>
        <p className="login-subtitulo">Entre para acompanhar as finanças da família.</p>

        <Alert mensagem={erro} />

        <FormField rotulo="E-mail">
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
            autoComplete="username" required autoFocus />
        </FormField>
        <FormField rotulo="Senha">
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)}
            autoComplete="current-password" required />
        </FormField>

        <button type="submit" className="botao botao-primario botao-largo" disabled={enviando}>
          {enviando ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
    </div>
  )
}
