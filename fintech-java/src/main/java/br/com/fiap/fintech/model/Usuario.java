package br.com.fiap.fintech.model;

import java.time.LocalDate;

public class Usuario {

    private int idUsuario;
    private String nome;
    private String email;
    private LocalDate dataNascimento;

    public Usuario() {
    }

    public Usuario(String nome, String email, LocalDate dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    public Usuario(int idUsuario, String nome, String email, LocalDate dataNascimento) {
        this(nome, email, dataNascimento);
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return String.format("Usuario [id=%d, nome=%s, email=%s, nascimento=%s]",
                idUsuario, nome, email, dataNascimento);
    }
}
