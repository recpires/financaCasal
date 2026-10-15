package br.com.fiap.fintech.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Movimentacao de uma conta (extrato). Cada transacao altera o saldo da conta.
 */
@Entity
@Table(name = "T_FIN_TRANSACAO")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRANSACAO")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CONTA", nullable = false)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_TRANSACAO", nullable = false, length = 20)
    private TipoTransacao tipo;

    @Column(name = "VL_TRANSACAO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "DS_TRANSACAO", nullable = false, length = 150)
    private String descricao;

    @Column(name = "DT_TRANSACAO", nullable = false)
    private LocalDateTime dataHora;

    /** Valor com sinal: credito soma no saldo, debito subtrai. */
    public BigDecimal getValorComSinal() {
        return tipo.isCredito() ? valor : valor.negate();
    }

    public Long getId() {
        return id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
