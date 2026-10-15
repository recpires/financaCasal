package br.com.fiap.fintech.model;

/**
 * Tipos de movimentacao. Cada tipo sabe se entra (credito) ou sai (debito) da conta.
 */
public enum TipoTransacao {

    DEPOSITO(true),
    RENDIMENTO(true),
    SAQUE(false),
    PAGAMENTO(false),
    TAXA(false);

    private final boolean credito;

    TipoTransacao(boolean credito) {
        this.credito = credito;
    }

    public boolean isCredito() {
        return credito;
    }
}
