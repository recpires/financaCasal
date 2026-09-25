package fintech.model;

/**
 * Tipos de movimentacao suportados pelo extrato.
 */
public enum TipoTransacao {

    DEPOSITO("Deposito", true),
    SAQUE("Saque", false),
    TRANSFERENCIA_ENVIADA("Transferencia enviada", false),
    TRANSFERENCIA_RECEBIDA("Transferencia recebida", true),
    TAXA("Taxa", false),
    RENDIMENTO("Rendimento", true),
    PAGAMENTO_CARTAO("Pagamento com cartao", false),
    PAGAMENTO_FATURA("Pagamento de fatura", false);

    private final String descricao;
    private final boolean credito;

    TipoTransacao(String descricao, boolean credito) {
        this.descricao = descricao;
        this.credito = credito;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isCredito() {
        return credito;
    }

    public String getSinal() {
        return credito ? "+" : "-";
    }
}
