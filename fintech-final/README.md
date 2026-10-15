# Fintech — Finanças Casal

Projeto final do Fintech: API REST em **Java 21 + Spring Boot** com banco **Oracle da FIAP** e
frontend em **ReactJS** (Vite). Continua o modelo das fases anteriores: **Clientes**, **Contas**
(corrente e poupança) e **Transações** (extrato), com login de usuário.

```
fintech-final/
├── backend/     API Spring Boot (Model, Repository, Service, Controller)
├── frontend/    SPA em React
└── database/    script_tabelas.sql (criação das tabelas no Oracle)
```

## Usuário de teste (login)

| E-mail | Senha |
|---|---|
| `teste@fintech.com` | `fintech123` |

O usuário é criado automaticamente pelo backend na primeira execução, junto com dados de
exemplo (3 clientes, 4 contas e 10 transações).

## Pré-requisitos

- Java 21 (JDK)
- Node.js 20 ou superior (com npm)
- Acesso ao Oracle da FIAP (`oracle.fiap.com.br:1521/ORCL`)

## 1. Banco de dados

No SQL Developer, conectado ao Oracle da FIAP, execute `database/script_tabelas.sql`.
Ele cria as tabelas `T_FIN_USUARIO`, `T_FIN_CLIENTE`, `T_FIN_CONTA` e `T_FIN_TRANSACAO`.
(Na primeira vez os `DROP TABLE` dão erro porque as tabelas ainda não existem; pode ignorar.)

## 2. Backend (porta 8080)

Informe o usuário e a senha do Oracle de uma destas formas:

- **Variáveis de ambiente** `DB_USERNAME` e `DB_PASSWORD`; ou
- editando `backend/src/main/resources/application.properties` (troque `RMXXXXX` e `XXXXXX`).

Depois, na pasta `backend`:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Também é possível abrir a pasta `backend` no IntelliJ e rodar a classe `FintechApplication`.
A API fica em `http://localhost:8080/api`.

## 3. Frontend (porta 5173)

Com o backend rodando, na pasta `frontend`:

```bash
npm install
npm run dev
```

Acesse `http://localhost:5173` e entre com o usuário de teste.
Se o backend estiver em outro endereço, crie `frontend/.env` com
`VITE_API_URL=http://localhost:PORTA/api`.

## Endpoints da API

| Recurso | GET | POST | PUT | DELETE |
|---|---|---|---|---|
| `/api/clientes` | lista / `/{id}` | cria | `/{id}` atualiza | `/{id}` exclui |
| `/api/contas` | lista (`?clienteId=`) / `/{id}` | abre conta | `/{id}` atualiza | `/{id}` encerra |
| `/api/transacoes` | lista (`?contaId=`) / `/{id}` | registra | `/{id}` atualiza | `/{id}` exclui (estorna) |
| `/api/auth/login` | | login | | |

**Códigos de status:** `200` consulta/atualização, `201` criação (com header `Location`),
`204` exclusão, `400` dados inválidos, `401` login inválido, `404` registro inexistente,
`409` conflito (CPF repetido, cliente com contas, conta com saldo) e `422` regra de negócio
violada (saldo insuficiente, conta inativa...).

## Regras de negócio (camada Service)

- **Cliente:** CPF com 11 dígitos e único; maior de 18 anos; não pode ser excluído se tiver contas.
- **Conta:** abre com saldo zero e número gerado automaticamente. Conta corrente tem cheque
  especial, mas só para clientes com 21 anos ou mais (regra da fase anterior). Poupança rende de
  0% a 5% ao mês. Titular e tipo não mudam depois da abertura. Só é encerrada com saldo zero.
- **Transação:** depósito e rendimento somam no saldo; saque, pagamento e taxa subtraem. Uma saída
  não pode passar do saldo disponível (saldo + cheque especial). Alterar uma transação desfaz o
  valor antigo e aplica o novo; excluir estorna o valor. Conta inativa não aceita transações.

## Frontend

- **Páginas:** login, início (resumo e últimas transações), erro 404 personalizada, e para cada
  entidade uma lista (consultar e excluir) e um formulário (cadastrar e editar).
- **Rotas (React Router):** `/login`, `/`, `/clientes`, `/clientes/novo`, `/clientes/:id/editar`,
  `/contas`, `/contas/nova`, `/contas/:id/editar`, `/transacoes`, `/transacoes/nova`,
  `/transacoes/:id/editar`. Qualquer outro endereço cai na página 404.
- **Componentização:** `Layout`, `Sidebar`, `PrivateRoute`, `PageHeader`, `FormField`, `Alert`,
  `StatCard`, `TransacaoItem`, `Loading`, `Icon`, comunicando-se por props.
- **Estado e hooks:** `useState`, `useEffect`, contexto de autenticação (`AuthContext` com
  `useContext`) e o hook próprio `useLista` para carregar listas da API.
- **Consumo da API:** `src/services/api.js` (fetch), com os erros de validação do backend
  exibidos em cada campo do formulário.
