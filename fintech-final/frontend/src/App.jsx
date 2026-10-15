import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import PrivateRoute from './components/PrivateRoute.jsx'
import Login from './pages/Login.jsx'
import Home from './pages/Home.jsx'
import NotFound from './pages/NotFound.jsx'
import ClientesLista from './pages/clientes/ClientesLista.jsx'
import ClienteForm from './pages/clientes/ClienteForm.jsx'
import ContasLista from './pages/contas/ContasLista.jsx'
import ContaForm from './pages/contas/ContaForm.jsx'
import TransacoesLista from './pages/transacoes/TransacoesLista.jsx'
import TransacaoForm from './pages/transacoes/TransacaoForm.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      {/* Rotas protegidas: so acessiveis depois do login */}
      <Route element={<PrivateRoute />}>
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />

          <Route path="/clientes" element={<ClientesLista />} />
          <Route path="/clientes/novo" element={<ClienteForm />} />
          <Route path="/clientes/:id/editar" element={<ClienteForm />} />

          <Route path="/contas" element={<ContasLista />} />
          <Route path="/contas/nova" element={<ContaForm />} />
          <Route path="/contas/:id/editar" element={<ContaForm />} />

          <Route path="/transacoes" element={<TransacoesLista />} />
          <Route path="/transacoes/nova" element={<TransacaoForm />} />
          <Route path="/transacoes/:id/editar" element={<TransacaoForm />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
