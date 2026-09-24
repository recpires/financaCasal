package br.com.fiap.fintech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Receita {

    private int idReceita;
    private int idUsuario;
    private String descricao;
    private String categoria;
    private BigDecimal valor;
    private LocalDate data;

    public Receita() {
    }

    public Receita(int idUsuario, String descricao, String categoria, BigDecimal valor, LocalDate data) {
        this.idUsuario = idUsuario;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.data = data;
    }

    public Receita(int idReceita, int idUsuario, String descricao, String categoria, BigDecimal valor, LocalDate data) {
        this(idUsuario, descricao, categoria, valor, data);
        this.idReceita = idReceita;
    }

    public int getIdReceita() {
        return idReceita;
    }

    public void setIdReceita(int idReceita) {
        this.idReceita = idReceita;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Receita [id=%d, usuario=%d, descricao=%s, categoria=%s, valor=R$ %.2f, data=%s]",
                idReceita, idUsuario, descricao, categoria, valor, data);
    }
}
