package fintech.model;

/**
 * SUBCLASSE de Cartao: acumula os gastos em uma fatura em vez de
 * debitar na hora. Tambem implementa Tributavel (IOF da fatura).
 */
public class CartaoCredito extends Cartao implements Tributavel {

    private static final double ALIQUOTA_IOF = 0.0038;

    private double limiteTotal;
    private double faturaAtual;
    private int diaVencimento;

    public CartaoCredito(Conta contaVinculada, int senha, double limiteTotal, int diaVencimento) {
        super(contaVinculada, senha);
        setLimiteTotal(limiteTotal);
        setDiaVencimento(diaVencimento);
        this.faturaAtual = 0.0;
    }

    // Sobrecarga: vencimento padrao dia 10
    public CartaoCredito(Conta contaVinculada, int senha, double limiteTotal) {
        this(contaVinculada, senha, limiteTotal, 10);
    }

    @Override
    public String getTipoCartao() {
        return "Cartao de Credito";
    }

    /**
     * No credito o valor entra na fatura; a conta so e debitada no pagamento.
     * Comportamento totalmente diferente do CartaoDebito -> polimorfismo.
     */
    @Override
    public boolean pagar(double valor, String estabelecimento) {
        if (isBloqueado() || valor <= 0 || valor > getLimiteDisponivel()) {
            return false;
        }
        faturaAtual += valor;
        return true;
    }

    /** Parcelamento: divide o valor e lanca tudo na fatura. */
    public boolean parcelar(double valor, int parcelas, String estabelecimento) {
        if (parcelas < 1 || parcelas > 12) {
            return false;
        }
        double comJuros = parcelas > 6 ? valor * 1.05 : valor;
        return pagar(comJuros, estabelecimento + " (" + parcelas + "x)");
    }

    /** Quita a fatura debitando da conta vinculada. */
    public boolean pagarFatura() {
        if (faturaAtual <= 0) {
            return false;
        }
        double total = faturaAtual + calcularTributo();
        boolean pago = getContaVinculada().debitarPagamento(total,
                "Fatura cartao " + getNumeroMascarado(), true);
        if (pago) {
            faturaAtual = 0.0;
        }
        return pago;
    }

    @Override
    public double calcularTributo() {
        return faturaAtual * ALIQUOTA_IOF;
    }

    @Override
    public String getNomeTributo() {
        return "IOF sobre fatura";
    }

    public double getLimiteDisponivel() {
        return limiteTotal - faturaAtual;
    }

    public double getLimiteTotal() {
        return limiteTotal;
    }

    public void setLimiteTotal(double limiteTotal) {
        if (limiteTotal <= 0) {
            throw new IllegalArgumentException("Limite deve ser positivo.");
        }
        this.limiteTotal = getTitular().isElegivelParaCredito() ? limiteTotal : 500.0;
    }

    public double getFaturaAtual() {
        return faturaAtual;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(int diaVencimento) {
        if (diaVencimento < 1 || diaVencimento > 28) {
            throw new IllegalArgumentException("Vencimento deve estar entre 1 e 28.");
        }
        this.diaVencimento = diaVencimento;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Fatura: R$ %.2f | Disponivel: R$ %.2f",
                faturaAtual, getLimiteDisponivel());
    }
}
