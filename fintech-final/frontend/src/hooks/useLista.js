import { useEffect, useState } from 'react'

/**
 * Carrega uma lista da API e controla os estados de carregamento e erro.
 * "buscar" e a funcao que chama a API; "dependencias" refaz a busca quando mudam (ex.: filtros).
 * "recarregar" busca de novo (usado depois de excluir um item).
 */
export default function useLista(buscar, dependencias = []) {
  const [itens, setItens] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const [versao, setVersao] = useState(0)

  useEffect(() => {
    let ativo = true
    setCarregando(true)
    setErro('')
    buscar()
      .then((resultado) => ativo && setItens(resultado))
      .catch((e) => ativo && setErro(e.message))
      .finally(() => ativo && setCarregando(false))
    // Se a pagina mudar antes da resposta chegar, a resposta antiga e ignorada
    return () => {
      ativo = false
    }
  }, [...dependencias, versao])

  return { itens, carregando, erro, recarregar: () => setVersao((v) => v + 1) }
}
