// Icones em SVG inline (sem biblioteca externa). Recebe o nome e o tamanho por props.
const caminhos = {
  carteira: 'M3 7a2 2 0 0 1 2-2h13v4M3 7v10a2 2 0 0 0 2 2h15V9H5a2 2 0 0 1-2-2Zm14 7h.01',
  painel: 'M3 13h8V3H3v10Zm10 8h8V11h-8v10ZM3 21h8v-6H3v6Zm10-18v6h8V3h-8Z',
  clientes: 'M16 19v-1a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v1M9 10a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm13 9v-1a4 4 0 0 0-3-3.87M16 4.13a3 3 0 0 1 0 5.74',
  contas: 'M3 10h18M5 10V20M9.5 10V20M14.5 10V20M19 10V20M2 20h20M12 3l9 5H3l9-5Z',
  transacoes: 'M7 4v16M7 4 3 8m4-4 4 4m6 12V4m0 16-4-4m4 4 4-4',
  sair: 'M15 4h3a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-3M10 17l5-5-5-5M15 12H3',
  mais: 'M12 5v14M5 12h14',
  editar: 'M4 20h4L18.5 9.5a2.12 2.12 0 0 0-3-3L5 17v3Z',
  lixeira: 'M4 7h16M10 11v6m4-6v6M6 7l1 13h10l1-13M9 7V4h6v3',
  voltar: 'M19 12H5m6-6-6 6 6 6',
}

export default function Icon({ nome, tamanho = 18 }) {
  return (
    <svg width={tamanho} height={tamanho} viewBox="0 0 24 24" fill="none" stroke="currentColor"
      strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d={caminhos[nome]} />
    </svg>
  )
}
