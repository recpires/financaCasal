// Endereco do backend. Pode ser trocado com a variavel VITE_API_URL (arquivo .env).
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

/** Erro devolvido pela API, com a mensagem e os erros de cada campo (quando houver). */
export class ApiError extends Error {
  constructor(status, mensagem, campos) {
    super(mensagem)
    this.status = status
    this.campos = campos || {}
  }
}

async function request(metodo, caminho, corpo) {
  let resposta
  try {
    resposta = await fetch(`${API_URL}${caminho}`, {
      method: metodo,
      headers: corpo ? { 'Content-Type': 'application/json' } : undefined,
      body: corpo ? JSON.stringify(corpo) : undefined,
    })
  } catch {
    throw new ApiError(0, 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.')
  }

  // 204 No Content (DELETE) nao tem corpo
  if (resposta.status === 204) return null

  const dados = await resposta.json().catch(() => null)
  if (!resposta.ok) {
    throw new ApiError(resposta.status, dados?.mensagem || `Erro ${resposta.status}`, dados?.campos)
  }
  return dados
}

/** Cria as funcoes de CRUD de um recurso REST (GET, POST, PUT, DELETE). */
function recurso(caminho) {
  return {
    listar: (filtros = {}) => {
      const query = new URLSearchParams(
        Object.entries(filtros).filter(([, valor]) => valor !== undefined && valor !== null && valor !== ''),
      ).toString()
      return request('GET', query ? `${caminho}?${query}` : caminho)
    },
    buscar: (id) => request('GET', `${caminho}/${id}`),
    criar: (dados) => request('POST', caminho, dados),
    atualizar: (id, dados) => request('PUT', `${caminho}/${id}`, dados),
    excluir: (id) => request('DELETE', `${caminho}/${id}`),
  }
}

export const authApi = {
  login: (email, senha) => request('POST', '/auth/login', { email, senha }),
}

export const clienteApi = recurso('/clientes')
export const contaApi = recurso('/contas')
export const transacaoApi = recurso('/transacoes')
