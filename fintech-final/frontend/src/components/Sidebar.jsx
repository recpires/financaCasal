import { NavLink } from 'react-router-dom'
import Icon from './Icon.jsx'

const itens = [
  { para: '/', rotulo: 'Início', icone: 'painel' },
  { para: '/clientes', rotulo: 'Clientes', icone: 'clientes' },
  { para: '/contas', rotulo: 'Contas', icone: 'contas' },
  { para: '/transacoes', rotulo: 'Transações', icone: 'transacoes' },
]

export default function Sidebar({ usuario, onSair }) {
  return (
    <nav className="sidebar">
      <div>
        <div className="marca">
          <Icon nome="carteira" tamanho={22} />
          <span>Finanças Casal</span>
        </div>
        <ul className="menu">
          {itens.map((item) => (
            <li key={item.para}>
              <NavLink to={item.para} end={item.para === '/'}
                className={({ isActive }) => (isActive ? 'menu-link ativo' : 'menu-link')}>
                <Icon nome={item.icone} /> {item.rotulo}
              </NavLink>
            </li>
          ))}
        </ul>
      </div>

      <div className="usuario">
        <div>
          <strong>{usuario.nome}</strong>
          <small>{usuario.email}</small>
        </div>
        <button type="button" className="botao-icone" onClick={onSair} title="Sair">
          <Icon nome="sair" />
        </button>
      </div>
    </nav>
  )
}
