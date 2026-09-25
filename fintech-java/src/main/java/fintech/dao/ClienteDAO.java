package fintech.dao;

import fintech.exception.DBException;
import fintech.factory.ConnectionFactory;
import fintech.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String TABELA = "T_CLIENTE";

    /** Cadastra o cliente e guarda no objeto o id gerado pelo banco. */
    public void insert(Cliente cliente) {
        String sql = "INSERT INTO T_CLIENTE (NM_CLIENTE, NR_CPF, DS_EMAIL, NR_TELEFONE, NR_IDADE) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_CLIENTE"})) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setInt(5, cliente.getIdade());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setIdCliente(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todos os clientes cadastrados. */
    public List<Cliente> getAll() {
        String sql = "SELECT ID_CLIENTE, NM_CLIENTE, NR_CPF, DS_EMAIL, NR_TELEFONE, NR_IDADE "
                + "FROM T_CLIENTE ORDER BY ID_CLIENTE";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getInt("ID_CLIENTE"),
                        rs.getString("NM_CLIENTE"),
                        rs.getString("NR_CPF"),
                        rs.getString("DS_EMAIL"),
                        rs.getString("NR_TELEFONE"),
                        rs.getInt("NR_IDADE")));
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
