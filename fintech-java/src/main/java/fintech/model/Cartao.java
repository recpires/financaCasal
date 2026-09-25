package fintech.model;

/**
 * SUPERCLASSE da segunda hierarquia do projeto.
 * Guarda o que todo cartao tem: numero, titular, conta vinculada e bloqueio.
 */
public abstract class Cartao {

    private static int contadorCartoes = 0;

    private final String numero;
    private final Cliente titular;
    private final Conta contaVinculada;
    private boolean bloqueado;
    private int senha;

    public Cartao(Conta contaVinculada, int senha) {
        if (contaVinculada == null) {
            throw new IllegalArgumentException("Cartao precisa estar vinculado a uma conta.");
        }
        this.contaVinculada = contaVinculada;
        this.titular = contaVinculada.getTitular();
        this.numero = gerarNumero();
        this.bloqueado = false;
        setSenha(senha);
    }

    private static String gerarNumero() {
        contadorCartoes++;
        return String.format("5432 %04d %04d %04d", contadorCartoes, contadorCartoes * 7 % 10000,
                contadorCartoes * 13 % 10000);
    }

    // ---------- Metodos abstratos ----------

    public abstract String getTipoCartao();

    /** Cada cartao decide de onde sai o dinheiro. */
    public abstract boolean pagar(double valor, String estabelecimento);

    // ---------- Comportamento comum ----------

    protected boolean autorizar(double valor, int senhaInformada) {
        return !bloqueado && valor > 0 && this.senha == senhaInformada;
    }

    public boolean bloquear() {
        if (bloqueado) {
            return false;
        }
        this.bloqueado = true;
        return true;
    }

    public boolean desbloquear(int senhaInformada) {
        if (this.senha != senhaInformada) {
            return false;
        }
        this.bloqueado = false;
        return true;
    }

    /** Exibe apenas os 4 ultimos digitos. */
    public String getNumeroMascarado() {
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }

    // ---------- Encapsulamento ----------

    public String getNumero() {
        return numero;
    }

    public Cliente getTitular() {
        return titular;
    }

    protected Conta getContaVinculada() {
        return contaVinculada;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    /** A senha nunca tem getter publico: so pode ser trocada. */
    public final void setSenha(int senha) {
        if (senha < 1000 || senha > 9999) {
            throw new IllegalArgumentException("A senha deve ter 4 digitos.");
        }
        this.senha = senha;
    }

    @Override
    public String toString() {
        return String.format("%s %s | %s | %s", getTipoCartao(), getNumeroMascarado(),
                titular.getNomeResumido(), bloqueado ? "BLOQUEADO" : "ATIVO");
    }
}
