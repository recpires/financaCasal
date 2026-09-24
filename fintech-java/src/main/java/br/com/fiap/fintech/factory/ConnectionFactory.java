package br.com.fiap.fintech.factory;

import br.com.fiap.fintech.exception.DBException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsável por abrir conexões com o banco de dados Oracle da FIAP.
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    // TODO: troque pelo seu RM e senha do Oracle FIAP
    private static final String USUARIO = "RMXXXXX";
    private static final String SENHA = "XXXXXX";

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            // 1017 = usuário/senha inválidos; 17002 / 17868 = banco fora do ar ou host inacessível
            if (e.getErrorCode() == 1017) {
                throw new DBException("Usuário ou senha do banco inválidos. Verifique a ConnectionFactory.", e);
            }
            throw new DBException("Não foi possível conectar ao banco Oracle (banco fora do ar ou sem rede?).", e);
        }
    }
}
