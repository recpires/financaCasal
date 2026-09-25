package fintech.factory;

import fintech.exception.DBException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsavel por abrir conexoes com o banco de dados Oracle da FIAP.
 */
public class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    // Troque pelo seu RM e senha do Oracle FIAP
    private static final String USUARIO = "RMXXXXX";
    private static final String SENHA = "XXXXXX";

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            // 1017 = usuario/senha invalidos; demais = banco fora do ar ou host inacessivel
            if (e.getErrorCode() == 1017) {
                throw new DBException("Usuario ou senha do banco invalidos. Verifique a ConnectionFactory.", e);
            }
            throw new DBException("Nao foi possivel conectar ao banco Oracle (banco fora do ar ou sem rede?).", e);
        }
    }
}
