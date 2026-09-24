# Fintech Finanças Casal — DAO com Oracle

Projeto Java 21 + Maven que integra 4 entidades do sistema FINTECH ao Oracle da FIAP via JDBC.

| Entidade | Tabela | DAO |
|---|---|---|
| Usuario | T_FC_USUARIO | `UsuarioDAO` |
| Receita | T_FC_RECEITA | `ReceitaDAO` |
| Despesa | T_FC_DESPESA | `DespesaDAO` |
| Meta | T_FC_META | `MetaDAO` |

Cada DAO tem `insert()` (INSERT) e `getAll()` (SELECT que devolve uma `List`). Erros de banco
(banco fora do ar, tabela inexistente, login inválido, FK inexistente...) são capturados com
try-catch e convertidos em `DBException` com mensagem clara.

## Como executar

1. No SQL Developer (conectado ao Oracle da FIAP), rode `sql/script_tabelas.sql`.
2. Em `ConnectionFactory.java`, coloque seu RM e senha.
3. Abra a pasta no IntelliJ (ele carrega o Maven e baixa o driver `ojdbc11`).
4. Rode `br.com.fiap.fintech.Teste` — ela cadastra 5 registros de cada entidade e lista tudo com `getAll()`.
