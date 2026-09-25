package fintech.model;

/**
 * SUBCLASSE de Conta. Herda saldo, extrato e operacoes, e acrescenta
 * cheque especial + taxa de manutencao.
 *
 * Implementa Tributavel: a conta corrente recolhe IOF sobre o uso do limite.
 */
public class ContaCorrente extends Conta implements Tributavel {

    private static final double ALIQUOTA_IOF_DIARIA = 0.000082;
    private static final double TAXA_POR_SAQUE = 2.50;

    private double limiteChequeEspecial;
    private double taxaManutencao;

    // Construtor completo: chama o construtor da superclasse com super(...)
    public ContaCorrente(Cliente titular, String agencia, double saldoInicial,
                         double limiteChequeEspecial, double taxaManutencao) {
        super(titular, agencia, saldoInicial);
        setLimiteChequeEspecial(limiteChequeEspecial);
        setTaxaManutencao(taxaManutencao);
    }

    // Construtor reduzido (sobrecarga): valores padrao do produto
    public ContaCorrente(Cliente titular, double saldoInicial) {
        this(titular, "0001", saldoInicial, 1000.0, 29.90);
    }

    // Construtor usado pelo ContaDAO ao ler um registro do banco
    public ContaCorrente(int idConta, String numero, String agencia, double saldo, Cliente titular,
                         boolean ativa, double limiteChequeEspecial, double taxaManutencao) {
        super(idConta, numero, agencia, saldo, titular, ativa);
        setLimiteChequeEspecial(limiteChequeEspecial);
        setTaxaManutencao(taxaManutencao);
    }

    // ---------- Polimorfismo: implementacao dos metodos abstratos ----------

    @Override
    public String getTipoConta() {
        return "Conta Corrente";
    }

    @Override
    public double aplicarAtualizacaoMensal() {
        debitar(taxaManutencao);
        registrarTransacao(TipoTransacao.TAXA, taxaManutencao, "Tarifa mensal de manutencao");
        return -taxaManutencao;
    }

    // ---------- Polimorfismo: sobrescrita de comportamento herdado ----------

    /**
     * A conta corrente pode ficar negativa ate o limite do cheque especial.
     */
    @Override
    protected boolean podeDebitar(double valor) {
        return (getSaldo() + limiteChequeEspecial) >= valor;
    }

    /**
     * Saque da conta corrente cobra tarifa. Reaproveita a logica da superclasse
     * com super.sacar(...) e apenas complementa o comportamento.
     */
    @Override
    public boolean sacar(double valor) {
        if (!super.sacar(valor)) {
            return false;
        }
        if (podeDebitar(TAXA_POR_SAQUE)) {
            debitar(TAXA_POR_SAQUE);
            registrarTransacao(TipoTransacao.TAXA, TAXA_POR_SAQUE, "Tarifa de saque");
        }
        return true;
    }

    // ---------- Regras proprias da subclasse ----------

    /** Quanto do cheque especial ja foi consumido. */
    public double getChequeEspecialUtilizado() {
        return getSaldo() < 0 ? Math.abs(getSaldo()) : 0.0;
    }

    public double getSaldoDisponivel() {
        return getSaldo() + limiteChequeEspecial;
    }

    public boolean isUsandoChequeEspecial() {
        return getSaldo() < 0;
    }

    @Override
    public double calcularTributo() {
        return getChequeEspecialUtilizado() * ALIQUOTA_IOF_DIARIA * 30;
    }

    @Override
    public String getNomeTributo() {
        return "IOF sobre cheque especial";
    }

    // ---------- Encapsulamento ----------

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        if (limiteChequeEspecial < 0) {
            throw new IllegalArgumentException("Limite nao pode ser negativo.");
        }
        this.limiteChequeEspecial = getTitular().isElegivelParaCredito() ? limiteChequeEspecial : 0.0;
    }

    public double getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(double taxaManutencao) {
        if (taxaManutencao < 0) {
            throw new IllegalArgumentException("Taxa nao pode ser negativa.");
        }
        this.taxaManutencao = taxaManutencao;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Limite: R$ %.2f", limiteChequeEspecial);
    }
}
