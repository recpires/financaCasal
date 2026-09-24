package br.com.fiap.fintech.dao;

import br.com.fiap.fintech.exception.DBException;
import br.com.fiap.fintech.factory.ConnectionFactory;
import br.com.fiap.fintech.model.Despesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DespesaDAO {

    private static final String TABELA = "T_FC_DESPESA";

    /** Cadastra uma despesa e preenche o ID gerado pelo banco no próprio objeto. */
    public void insert(Despesa despesa) {
        String sql = "INSERT INTO T_FC_DESPESA (ID_USUARIO, DS_DESPESA, DS_CATEGORIA, VL_DESPESA, DT_DESPESA) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_DESPESA"})) {

            stmt.setInt(1, despesa.getIdUsuario());
            stmt.setString(2, despesa.getDescricao());
            stmt.setString(3, despesa.getCategoria());
            stmt.setBigDecimal(4, despesa.getValor());
            stmt.setDate(5, Date.valueOf(despesa.getData()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    despesa.setIdDespesa(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todas as despesas cadastradas. */
    public List<Despesa> getAll() {
        String sql = "SELECT ID_DESPESA, ID_USUARIO, DS_DESPESA, DS_CATEGORIA, VL_DESPESA, DT_DESPESA "
                + "FROM T_FC_DESPESA ORDER BY DT_DESPESA, ID_DESPESA";
        List<Despesa> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Despesa(
                        rs.getInt("ID_DESPESA"),
                        rs.getInt("ID_USUARIO"),
                        rs.getString("DS_DESPESA"),
                        rs.getString("DS_CATEGORIA"),
                        rs.getBigDecimal("VL_DESPESA"),
                        rs.getDate("DT_DESPESA").toLocalDate()));
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
