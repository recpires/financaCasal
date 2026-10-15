package br.com.fiap.fintech.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Conta do cliente. Corrente e poupanca ficam na mesma tabela,
 * diferenciadas pela coluna TP_CONTA.
 */
@Entity
@Table(name = "T_FIN_CONTA")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONTA")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CLIENTE", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_CONTA", nullable = false, length = 8)
    private TipoConta tipo;

    @Column(name = "NR_CONTA", nullable = false, unique = true, length = 10)
    private String numero;

    @Column(name = "NR_AGENCIA", nullable = false, length = 4)
    private String agencia;

    @Column(name = "VL_SALDO", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    /** Apenas conta corrente. */
    @Column(name = "VL_LIMITE_CHEQUE", precision = 15, scale = 2)
    private BigDecimal limiteChequeEspecial;

    /** Apenas poupanca (ex.: 0.0050 = 0,5% ao mes). */
    @Column(name = "TX_RENDIMENTO_MENSAL", precision = 6, scale = 4)
    private BigDecimal taxaRendimentoMensal;

    @Column(name = "ST_ATIVA", nullable = false)
    private boolean ativa = true;

    @Column(name = "DT_ABERTURA", nullable = false)
    private LocalDate dataAbertura;

    /** Quanto pode sair da conta: saldo + cheque especial (corrente) ou so o saldo (poupanca). */
    public BigDecimal getSaldoDisponivel() {
        if (tipo == TipoConta.CORRENTE && limiteChequeEspecial != null) {
            return saldo.add(limiteChequeEspecial);
        }
        return saldo;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(BigDecimal limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public BigDecimal getTaxaRendimentoMensal() {
        return taxaRendimentoMensal;
    }

    public void setTaxaRendimentoMensal(BigDecimal taxaRendimentoMensal) {
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }
}
