package fintech.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SUPERCLASSE da hierarquia de contas.
 *
 * Concentra tudo que e comum a qualquer conta (saldo, extrato, deposito,
 * saque, transferencia) e declara os pontos que cada subclasse precisa
 * definir do seu jeito (metodos abstratos).
 */
public abstract class Conta {

    private static int contadorContas = 1000;

    private int idConta;
    private final String numero;
    private final String agencia;
    private double saldo;
    private Cliente titular;
    private boolean ativa;
    private final List<Transacao> extrato;

    // Construtor completo da superclasse
    public Conta(Cliente titular, String agencia, double saldoInicial) {
        if (titular == null) {
            throw new IllegalArgumentException("A conta precisa de um titular.");
        }
        this.titular = titular;
        this.agencia = (agencia == null || agencia.isEmpty()) ? "0001" : agencia;
        this.numero = gerarNumeroConta();
        this.saldo = Math.max(saldoInicial, 0.0);
        this.ativa = true;
        this.extrato = new ArrayList<>();

        if (this.saldo > 0) {
            registrarTransacao(TipoTransacao.DEPOSITO, this.saldo, "Deposito de abertura");
        }
    }

    // Construtor reduzido (sobrecarga) - agencia padrao e saldo zerado
    public Conta(Cliente titular) {
        this(titular, "0001", 0.0);
    }

    /**
     * Construtor usado pelo ContaDAO ao ler um registro do banco: recria a conta
     * com o numero e o saldo gravados, sem gerar numero novo nem deposito de abertura.
     */
    protected Conta(int idConta, String numero, String agencia, double saldo, Cliente titular, boolean ativa) {
        if (titular == null) {
            throw new IllegalArgumentException("A conta precisa de um titular.");
        }
        this.idConta = idConta;
        this.numero = numero;
        this.agencia = agencia;
        this.saldo = saldo;
        this.titular = titular;
        this.ativa = ativa;
        this.extrato = new ArrayList<>();
    }

    private static String gerarNumeroConta() {
        contadorContas++;
        return contadorContas + "-" + (contadorContas % 9);
    }

    // ---------- Metodos abstratos: cada subclasse implementa o seu ----------

    /** Nome do produto exibido nos relatorios. */
    public abstract String getTipoConta();

    /** Fechamento mensal: pode debitar taxa ou creditar rendimento. */
    public abstract double aplicarAtualizacaoMensal();

    // ---------- Operacoes concretas ----------

    /** Deposito simples. */
    public boolean depositar(double valor) {
        return depositar(valor, "Deposito em conta");
    }

    /** SOBRECARGA (polimorfismo estatico): deposito com descricao personalizada. */
    public boolean depositar(double valor, String descricao) {
        if (!validarOperacao(valor)) {
            return false;
        }
        creditar(valor);
        registrarTransacao(TipoTransacao.DEPOSITO, valor, descricao);
        return true;
    }

    /**
     * Saque padrao: so permite debitar o que existe de saldo.
     * As subclasses podem SOBRESCREVER este comportamento.
     */
    public boolean sacar(double valor) {
        if (!validarOperacao(valor) || !podeDebitar(valor)) {
            return false;
        }
        debitar(valor);
        registrarTransacao(TipoTransacao.SAQUE, valor, "Saque em conta");
        return true;
    }

    /**
     * Transferencia entre contas. Repare que o parametro e do tipo Conta:
     * qualquer subclasse pode ser recebida aqui (polimorfismo).
     */
    public boolean transferir(Conta destino, double valor) {
        if (destino == null || destino == this || !destino.isAtiva()) {
            return false;
        }
        if (!validarOperacao(valor) || !podeDebitar(valor)) {
            return false;
        }
        debitar(valor);
        registrarTransacao(TipoTransacao.TRANSFERENCIA_ENVIADA, valor,
                "Para " + destino.getTitular().getNomeResumido() + " (conta " + destino.getNumero() + ")");

        destino.creditar(valor);
        destino.registrarTransacao(TipoTransacao.TRANSFERENCIA_RECEBIDA, valor,
                "De " + this.getTitular().getNomeResumido() + " (conta " + this.getNumero() + ")");
        return true;
    }

    /** Debito originado de um cartao vinculado a esta conta. */
    public boolean debitarPagamento(double valor, String descricao, boolean fatura) {
        if (!validarOperacao(valor) || !podeDebitar(valor)) {
            return false;
        }
        debitar(valor);
        registrarTransacao(fatura ? TipoTransacao.PAGAMENTO_FATURA : TipoTransacao.PAGAMENTO_CARTAO,
                valor, descricao);
        return true;
    }

    /**
     * Regra de limite de debito. A conta comum so debita o que tem;
     * ContaCorrente sobrescreve para considerar o cheque especial.
     */
    protected boolean podeDebitar(double valor) {
        return saldo >= valor;
    }

    protected boolean validarOperacao(double valor) {
        return ativa && valor > 0;
    }

    protected final void creditar(double valor) {
        this.saldo += valor;
    }

    protected final void debitar(double valor) {
        this.saldo -= valor;
    }

    protected final void registrarTransacao(TipoTransacao tipo, double valor, String descricao) {
        extrato.add(new Transacao(tipo, valor, descricao, this.saldo));
    }

    /** Monta o extrato em texto (nao imprime nada aqui: quem imprime e o Main). */
    public String gerarExtrato() {
        StringBuilder sb = new StringBuilder();
        sb.append("EXTRATO - ").append(getTipoConta())
          .append(" | Ag ").append(agencia).append(" | Conta ").append(numero).append("\n");
        sb.append("Titular: ").append(titular.getNome()).append("\n");
        sb.append(linhaSeparadora()).append("\n");
        if (extrato.isEmpty()) {
            sb.append("Nenhuma movimentacao registrada.\n");
        } else {
            for (Transacao t : extrato) {
                sb.append(t.formatarLinha()).append("\n");
            }
        }
        sb.append(linhaSeparadora()).append("\n");
        sb.append(String.format("SALDO ATUAL: R$ %.2f%n", saldo));
        return sb.toString();
    }

    /** Linha de separacao do extrato (compativel com Java 8+). */
    private static String linhaSeparadora() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 110; i++) {
            sb.append('-');
        }
        return sb.toString();
    }

    /** Soma de tudo que entrou menos tudo que saiu (conferencia do extrato). */
    public double calcularTotalMovimentado() {
        double total = 0;
        for (Transacao t : extrato) {
            total += t.getValorComSinal();
        }
        return total;
    }

    public boolean encerrar() {
        if (saldo != 0) {
            return false;
        }
        this.ativa = false;
        return true;
    }

    // ---------- Getters e setters (encapsulamento) ----------

    /** Identificador gerado pelo banco (0 enquanto a conta nao foi cadastrada). */
    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getNumero() {
        return numero;
    }

    public String getAgencia() {
        return agencia;
    }

    /** Saldo so muda por operacoes: nao existe setSaldo publico. */
    public double getSaldo() {
        return saldo;
    }

    public Cliente getTitular() {
        return titular;
    }

    public void setTitular(Cliente titular) {
        if (titular == null) {
            throw new IllegalArgumentException("Titular nao pode ser nulo.");
        }
        this.titular = titular;
    }

    public boolean isAtiva() {
        return ativa;
    }

    /** Copia protegida: ninguem altera o extrato de fora da classe. */
    public List<Transacao> getExtrato() {
        return Collections.unmodifiableList(extrato);
    }

    public int getQuantidadeTransacoes() {
        return extrato.size();
    }

    @Override
    public String toString() {
        return String.format("%s | Ag %s | Conta %s | Titular: %s | Saldo: R$ %.2f",
                getTipoConta(), agencia, numero, titular.getNomeResumido(), saldo);
    }
}
