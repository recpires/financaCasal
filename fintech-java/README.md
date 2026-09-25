# Projeto Fintech — Integração com Oracle (DAO)

Continuação do **FintechProject** (fase anterior): as classes do modelo (`Cliente`, `Conta`,
`ContaCorrente`, `ContaPoupanca`, `Transacao`...) agora são gravadas e lidas do banco Oracle
da FIAP por meio de classes DAO.

## Como executar

1. No SQL Developer (conectado ao Oracle da FIAP), rode `sql/script_tabelas.sql`.
2. Confira usuário e senha em `src/main/java/fintech/factory/ConnectionFactory.java`.
3. Abra a pasta no IntelliJ (projeto Maven: o driver `ojdbc11` é baixado automaticamente).
4. Rode `fintech.Teste`.

## Estrutura

```
src/main/java/fintech/
├── Teste.java                  <- classe de testes (insert + getAll de cada entidade)
├── Main.java                   <- demonstração da fase anterior (POO, sem banco)
├── dao/
│   ├── ClienteDAO.java         <- T_CLIENTE
│   ├── ContaDAO.java           <- T_CONTA (corrente e poupança)
│   └── TransacaoDAO.java       <- T_TRANSACAO (extrato)
├── factory/ConnectionFactory.java
├── exception/DBException.java
└── model/                      <- classes da fase anterior
```

## Onde cada requisito está

| Requisito | Onde encontrar |
|---|---|
| Classe DAO | `ClienteDAO`, `ContaDAO` e `TransacaoDAO` (3 entidades) |
| `getAll()` | Cada DAO faz um `SELECT` e devolve uma `List` de objetos. `ContaDAO` usa `JOIN` para trazer o titular e recria a subclasse certa (`ContaCorrente` ou `ContaPoupanca`) pela coluna `TP_CONTA`. |
| `insert()` | Cada DAO faz um `INSERT` com `PreparedStatement` e grava no objeto o id gerado pelo banco. |
| Tratamento de exceções | `try-catch` com `try-with-resources` em todos os acessos. `DBException` traduz os erros do Oracle (tabela inexistente, banco fora do ar, login inválido, chave estrangeira...). O `Teste` captura cada etapa separadamente. |
| Teste de cadastro e consulta | `Teste` cadastra 5 clientes, 5 contas e 14 transações e depois lista tudo com `getAll()`. |

## Mudanças no modelo da fase anterior

Para gravar no banco foram acrescentados apenas:

- `idCliente`, `idConta` e `id`/`idConta` em `Transacao`, preenchidos com o id gerado pelo Oracle;
- construtores que recriam `Cliente`, `ContaCorrente`, `ContaPoupanca` e `Transacao` a partir de um registro lido do banco (sem gerar número novo de conta nem depósito de abertura).

As regras de negócio não foram alteradas.
