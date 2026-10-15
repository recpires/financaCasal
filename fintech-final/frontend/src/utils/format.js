const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

export function formatarMoeda(valor) {
  return moeda.format(Number(valor) || 0)
}

export function formatarData(data) {
  if (!data) return '-'
  return new Date(`${data}T00:00:00`).toLocaleDateString('pt-BR')
}

export function formatarDataHora(dataHora) {
  if (!dataHora) return '-'
  return new Date(dataHora).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
}

export function formatarCpf(cpf) {
  const d = String(cpf || '').replace(/\D/g, '')
  return d.length === 11 ? `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}` : cpf
}

export const TIPOS_CONTA = {
  CORRENTE: 'Conta Corrente',
  POUPANCA: 'Conta Poupança',
}

/** Mesmos tipos do enum TipoTransacao do backend. */
export const TIPOS_TRANSACAO = {
  DEPOSITO: { rotulo: 'Depósito', credito: true },
  RENDIMENTO: { rotulo: 'Rendimento', credito: true },
  SAQUE: { rotulo: 'Saque', credito: false },
  PAGAMENTO: { rotulo: 'Pagamento', credito: false },
  TAXA: { rotulo: 'Taxa', credito: false },
}

export function descreverConta(conta) {
  return `${conta.numero} · ${TIPOS_CONTA[conta.tipo]} · ${conta.cliente.nome}`
}
