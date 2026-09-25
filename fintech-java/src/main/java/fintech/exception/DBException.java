package fintech.exception;

import java.sql.SQLException;

/**
 * Excecao lancada pelos DAOs quando ocorre algum problema no acesso ao banco.
 * Traduz os erros mais comuns do Oracle para mensagens mais claras.
 */
public class DBException extends RuntimeException {

    public DBException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    /** Monta uma mensagem amigavel a partir do codigo de erro do Oracle. */
    public static DBException from(String operacao, String tabela, SQLException e) {
        String motivo = switch (e.getErrorCode()) {
            case 942 -> "a tabela " + tabela + " nao existe (rode o script sql/script_tabelas.sql)";
            case 904 -> "coluna invalida na tabela " + tabela;
            case 1 -> "registro duplicado";
            case 1400 -> "campo obrigatorio nao informado";
            case 2291 -> "registro relacionado (chave estrangeira) nao encontrado";
            case 2290 -> "valor viola uma regra da tabela (check constraint)";
            case 12899 -> "valor maior que o tamanho permitido na coluna";
            case 17002, 17008, 17410 -> "conexao com o banco foi perdida";
            default -> e.getMessage();
        };
        return new DBException("Erro ao " + operacao + " em " + tabela + ": " + motivo, e);
    }
}
