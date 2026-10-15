import { createContext, useContext, useState } from 'react'
import { authApi } from '../services/api.js'

const AuthContext = createContext(null)
const CHAVE_STORAGE = 'fintech:usuario'

function lerUsuarioSalvo() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE_STORAGE))
  } catch {
    return null
  }
}

/** Guarda o usuario logado e disponibiliza login/logout para toda a aplicacao. */
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(lerUsuarioSalvo)

  async function login(email, senha) {
    const dados = await authApi.login(email, senha)
    localStorage.setItem(CHAVE_STORAGE, JSON.stringify(dados))
    setUsuario(dados)
  }

  function logout() {
    localStorage.removeItem(CHAVE_STORAGE)
    setUsuario(null)
  }

  return (
    <AuthContext.Provider value={{ usuario, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
