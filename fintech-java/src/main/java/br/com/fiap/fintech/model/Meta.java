package br.com.fiap.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Meta {

    private int idMeta;
    private String descricao;
    private BigDecimal valorAlvo;
    private BigDecimal valorAtual;
    private LocalDate dataLimite;

    public Meta() {
    }

    public Meta(String descricao, BigDecimal valorAlvo, BigDecimal valorAtual, LocalDate dataLimite) {
        this.descricao = descricao;
        this.valorAlvo = valorAlvo;
        this.valorAtual = valorAtual;
        this.dataLimite = dataLimite;
    }

    public Meta(int idMeta, String descricao, BigDecimal valorAlvo, BigDecimal valorAtual, LocalDate dataLimite) {
        this(descricao, valorAlvo, valorAtual, dataLimite);
        this.idMeta = idMeta;
    }

    public int getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(int idMeta) {
        this.idMeta = idMeta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorAlvo() {
        return valorAlvo;
    }

    public void setValorAlvo(BigDecimal valorAlvo) {
        this.valorAlvo = valorAlvo;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    @Override
    public String toString() {
        return String.format("Meta [id=%d, descricao=%s, alvo=R$ %.2f, atual=R$ %.2f, limite=%s]",
                idMeta, descricao, valorAlvo, valorAtual, dataLimite);
    }
}
