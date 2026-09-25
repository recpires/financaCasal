package fintech.dao;

import fintech.exception.DBException;
import fintech.factory.ConnectionFactory;
import fintech.model.TipoTransacao;
import fintech.model.Transacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO {

    private static final String TABELA = "T_TRANSACAO";

    /** Cadastra a transacao e guarda no objeto o id gerado pelo banco. A conta ja deve estar cadastrada. */
    public void insert(Transacao transacao) {
        String sql = "INSERT INTO T_TRANSACAO (ID_CONTA, TP_TRANSACAO, VL_TRANSACAO, DS_TRANSACAO, "
                + "DT_TRANSACAO, VL_SALDO_RESULTANTE) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_TRANSACAO"})) {

            stmt.setInt(1, transacao.getIdConta());
            stmt.setString(2, transacao.getTipo().name());
            stmt.setDouble(3, transacao.getValor());
            stmt.setString(4, transacao.getDescricao());
            stmt.setTimestamp(5, Timestamp.valueOf(transacao.getDataHora()));
            stmt.setDouble(6, transacao.getSaldoResultante());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    transacao.setId(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todas as transacoes cadastradas, em ordem cronologica. */
    public List<Transacao> getAll() {
        String sql = "SELECT ID_TRANSACAO, ID_CONTA, TP_TRANSACAO, VL_TRANSACAO, DS_TRANSACAO, "
                + "DT_TRANSACAO, VL_SALDO_RESULTANTE FROM T_TRANSACAO ORDER BY ID_TRANSACAO";
        List<Transacao> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Transacao(
                        rs.getInt("ID_TRANSACAO"),
                        rs.getInt("ID_CONTA"),
                        TipoTransacao.valueOf(rs.getString("TP_TRANSACAO")),
                        rs.getDouble("VL_TRANSACAO"),
                        rs.getString("DS_TRANSACAO"),
                        rs.getTimestamp("DT_TRANSACAO").toLocalDateTime(),
                        rs.getDouble("VL_SALDO_RESULTANTE")));
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
