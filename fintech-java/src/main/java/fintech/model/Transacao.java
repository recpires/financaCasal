package fintech.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registro de uma movimentacao financeira.
 * Os dados da movimentacao sao private final: depois de criada, ela nunca muda.
 * Apenas o id e a conta de origem sao definidos ao gravar no banco.
 */
public class Transacao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static int sequencia = 0;

    private int id;
    private int idConta;
    private final TipoTransacao tipo;
    private final double valor;
    private final String descricao;
    private final LocalDateTime dataHora;
    private final double saldoResultante;

    public Transacao(TipoTransacao tipo, double valor, String descricao, double saldoResultante) {
        this.id = ++sequencia;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = (descricao == null || descricao.isEmpty()) ? tipo.getDescricao() : descricao;
        this.dataHora = LocalDateTime.now();
        this.saldoResultante = saldoResultante;
    }

    // Construtor usado pelo TransacaoDAO ao ler um registro do banco
    public Transacao(int id, int idConta, TipoTransacao tipo, double valor, String descricao,
                     LocalDateTime dataHora, double saldoResultante) {
        this.id = id;
        this.idConta = idConta;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.saldoResultante = saldoResultante;
    }

    public int getId() {
        return id;
    }

    /** Recebe o id gerado pelo banco no cadastro. */
    public void setId(int id) {
        this.id = id;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public double getSaldoResultante() {
        return saldoResultante;
    }

    /** Valor com sinal: credito soma, debito subtrai. */
    public double getValorComSinal() {
        return tipo.isCredito() ? valor : -valor;
    }

    /** Linha formatada usada na impressao do extrato. */
    public String formatarLinha() {
        return String.format("#%03d | %s | %-24s | %s R$ %10.2f | Saldo: R$ %10.2f | %s",
                id,
                dataHora.format(FORMATO),
                tipo.getDescricao(),
                tipo.getSinal(),
                valor,
                saldoResultante,
                descricao);
    }

    @Override
    public String toString() {
        return formatarLinha();
    }
}
