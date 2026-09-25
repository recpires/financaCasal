package fintech.model;

/**
 * SUBCLASSE de Cartao: debita direto na conta, respeitando um limite diario.
 */
public class CartaoDebito extends Cartao {

    private double limiteDiario;
    private double gastoDoDia;

    public CartaoDebito(Conta contaVinculada, int senha, double limiteDiario) {
        super(contaVinculada, senha);
        setLimiteDiario(limiteDiario);
        this.gastoDoDia = 0.0;
    }

    // Sobrecarga: limite diario padrao
    public CartaoDebito(Conta contaVinculada, int senha) {
        this(contaVinculada, senha, 1500.0);
    }

    @Override
    public String getTipoCartao() {
        return "Cartao de Debito";
    }

    @Override
    public boolean pagar(double valor, String estabelecimento) {
        if (isBloqueado() || valor <= 0) {
            return false;
        }
        if (gastoDoDia + valor > limiteDiario) {
            return false;
        }
        boolean debitado = getContaVinculada().debitarPagamento(valor, "Debito - " + estabelecimento, false);
        if (debitado) {
            gastoDoDia += valor;
        }
        return debitado;
    }

    public void fecharDia() {
        this.gastoDoDia = 0.0;
    }

    public double getLimiteDiarioDisponivel() {
        return limiteDiario - gastoDoDia;
    }

    public double getLimiteDiario() {
        return limiteDiario;
    }

    public void setLimiteDiario(double limiteDiario) {
        if (limiteDiario <= 0) {
            throw new IllegalArgumentException("Limite diario deve ser positivo.");
        }
        this.limiteDiario = limiteDiario;
    }

    public double getGastoDoDia() {
        return gastoDoDia;
    }
}
