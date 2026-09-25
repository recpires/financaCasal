package fintech.model;

/**
 * SUBCLASSE de Conta. Nao possui cheque especial, mas rende juros
 * no aniversario e limita a quantidade de saques gratuitos no mes.
 */
public class ContaPoupanca extends Conta {

    private static final int SAQUES_GRATUITOS = 3;
    private static final double TAXA_SAQUE_EXCEDENTE = 4.90;

    private double taxaRendimentoMensal;
    private int diaAniversario;
    private int saquesNoMes;

    // Construtor completo
    public ContaPoupanca(Cliente titular, String agencia, double saldoInicial,
                         double taxaRendimentoMensal, int diaAniversario) {
        super(titular, agencia, saldoInicial);
        setTaxaRendimentoMensal(taxaRendimentoMensal);
        setDiaAniversario(diaAniversario);
        this.saquesNoMes = 0;
    }

    // Construtor reduzido (sobrecarga)
    public ContaPoupanca(Cliente titular, double saldoInicial) {
        this(titular, "0001", saldoInicial, 0.005, 10);
    }

    // Construtor usado pelo ContaDAO ao ler um registro do banco
    public ContaPoupanca(int idConta, String numero, String agencia, double saldo, Cliente titular,
                         boolean ativa, double taxaRendimentoMensal, int diaAniversario) {
        super(idConta, numero, agencia, saldo, titular, ativa);
        setTaxaRendimentoMensal(taxaRendimentoMensal);
        setDiaAniversario(diaAniversario);
        this.saquesNoMes = 0;
    }

    // ---------- Polimorfismo: implementacao dos metodos abstratos ----------

    @Override
    public String getTipoConta() {
        return "Conta Poupanca";
    }

    @Override
    public double aplicarAtualizacaoMensal() {
        double rendimento = getSaldo() * taxaRendimentoMensal;
        if (rendimento <= 0) {
            return 0.0;
        }
        creditar(rendimento);
        registrarTransacao(TipoTransacao.RENDIMENTO, rendimento,
                "Rendimento do dia " + diaAniversario);
        this.saquesNoMes = 0;
        return rendimento;
    }

    // ---------- Polimorfismo: sobrescrita ----------

    /**
     * A poupanca nunca fica negativa e cobra tarifa a partir do 4o saque do mes.
     */
    @Override
    public boolean sacar(double valor) {
        double custo = (saquesNoMes >= SAQUES_GRATUITOS) ? TAXA_SAQUE_EXCEDENTE : 0.0;

        if (!validarOperacao(valor) || getSaldo() < (valor + custo)) {
            return false;
        }

        debitar(valor);
        registrarTransacao(TipoTransacao.SAQUE, valor, "Saque poupanca");
        saquesNoMes++;

        if (custo > 0) {
            debitar(custo);
            registrarTransacao(TipoTransacao.TAXA, custo, "Tarifa de saque excedente");
        }
        return true;
    }

    // ---------- Regras proprias ----------

    /** Simula quanto a poupanca renderia em N meses (juros compostos). */
    public double projetarRendimento(int meses) {
        if (meses <= 0) {
            return 0.0;
        }
        double montante = getSaldo() * Math.pow(1 + taxaRendimentoMensal, meses);
        return montante - getSaldo();
    }

    public int getSaquesRestantesGratuitos() {
        return Math.max(SAQUES_GRATUITOS - saquesNoMes, 0);
    }

    // ---------- Encapsulamento ----------

    public double getTaxaRendimentoMensal() {
        return taxaRendimentoMensal;
    }

    public void setTaxaRendimentoMensal(double taxaRendimentoMensal) {
        if (taxaRendimentoMensal < 0 || taxaRendimentoMensal > 0.05) {
            throw new IllegalArgumentException("Taxa de rendimento fora da faixa permitida (0% a 5%).");
        }
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }

    public int getDiaAniversario() {
        return diaAniversario;
    }

    public void setDiaAniversario(int diaAniversario) {
        if (diaAniversario < 1 || diaAniversario > 28) {
            throw new IllegalArgumentException("Dia de aniversario deve estar entre 1 e 28.");
        }
        this.diaAniversario = diaAniversario;
    }

    public int getSaquesNoMes() {
        return saquesNoMes;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Rende %.2f%% a.m.", taxaRendimentoMensal * 100);
    }
}
