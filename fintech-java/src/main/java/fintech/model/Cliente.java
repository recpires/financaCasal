package fintech.model;

/**
 * Representa o cliente (titular) do sistema Fintech.
 * Todos os atributos sao privados (encapsulamento) e o acesso
 * acontece apenas por getters/setters com regras de validacao.
 */
public class Cliente {

    private int idCliente;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private int idade;

    // Construtor completo
    public Cliente(String nome, String cpf, String email, String telefone, int idade) {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setTelefone(telefone);
        setIdade(idade);
    }

    // Construtor reduzido (sobrecarga de construtor) - reaproveita o completo com this(...)
    public Cliente(String nome, String cpf) {
        this(nome, cpf, "nao informado", "nao informado", 18);
    }

    // Construtor usado pelo ClienteDAO ao ler um registro do banco
    public Cliente(int idCliente, String nome, String cpf, String email, String telefone, int idade) {
        this(nome, cpf, email, telefone, idade);
        this.idCliente = idCliente;
    }

    /** Identificador gerado pelo banco (0 enquanto o cliente nao foi cadastrado). */
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().length() < 3) {
            throw new IllegalArgumentException("Nome invalido: informe ao menos 3 caracteres.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        String somenteDigitos = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
        if (somenteDigitos.length() != 11) {
            throw new IllegalArgumentException("CPF invalido: sao esperados 11 digitos.");
        }
        this.cpf = somenteDigitos;
    }

    /** Retorna o CPF mascarado - o dado sensivel nunca e exposto por completo. */
    public String getCpfMascarado() {
        return "***.***." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email.toLowerCase();
        } else {
            this.email = "nao informado";
        }
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = (telefone == null || telefone.isEmpty()) ? "nao informado" : telefone;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        if (idade < 18) {
            throw new IllegalArgumentException("Cliente deve ser maior de idade.");
        }
        this.idade = idade;
    }

    /** Regra de negocio: define se o cliente pode contratar credito. */
    public boolean isElegivelParaCredito() {
        return idade >= 21 && !email.equals("nao informado");
    }

    /** Primeiro nome + inicial do sobrenome, usado nos comprovantes. */
    public String getNomeResumido() {
        String[] partes = nome.split(" ");
        if (partes.length == 1) {
            return partes[0];
        }
        return partes[0] + " " + partes[partes.length - 1].charAt(0) + ".";
    }

    @Override
    public String toString() {
        return String.format("Cliente{nome='%s', cpf='%s', idade=%d}", nome, getCpfMascarado(), idade);
    }
}
