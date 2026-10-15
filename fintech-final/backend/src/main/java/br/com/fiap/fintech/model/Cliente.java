package br.com.fiap.fintech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * Cliente (titular das contas). Mesmos dados da fase anterior:
 * nome, CPF, e-mail, telefone e idade.
 */
@Entity
@Table(name = "T_FIN_CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE")
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(name = "NM_CLIENTE", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Informe o CPF")
    @Column(name = "NR_CPF", nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail invalido")
    @Column(name = "DS_EMAIL", nullable = false, length = 120)
    private String email;

    @Size(max = 20, message = "Telefone muito longo")
    @Column(name = "NR_TELEFONE", length = 20)
    private String telefone;

    @NotNull(message = "Informe a idade")
    @Min(value = 18, message = "O cliente deve ser maior de idade")
    @Max(value = 120, message = "Idade invalida")
    @Column(name = "NR_IDADE", nullable = false)
    private Integer idade;

    /** Regra da fase anterior: credito so para maiores de 21 anos. */
    public boolean isElegivelParaCredito() {
        return idade != null && idade >= 21;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }
}
