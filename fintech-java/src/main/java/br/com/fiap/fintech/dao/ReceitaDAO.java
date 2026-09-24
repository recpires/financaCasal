package br.com.fiap.fintech.dao;

import br.com.fiap.fintech.exception.DBException;
import br.com.fiap.fintech.factory.ConnectionFactory;
import br.com.fiap.fintech.model.Receita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDAO {

    private static final String TABELA = "T_FC_RECEITA";

    /** Cadastra uma receita e preenche o ID gerado pelo banco no próprio objeto. */
    public void insert(Receita receita) {
        String sql = "INSERT INTO T_FC_RECEITA (ID_USUARIO, DS_RECEITA, DS_CATEGORIA, VL_RECEITA, DT_RECEITA) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_RECEITA"})) {

            stmt.setInt(1, receita.getIdUsuario());
            stmt.setString(2, receita.getDescricao());
            stmt.setString(3, receita.getCategoria());
            stmt.setBigDecimal(4, receita.getValor());
            stmt.setDate(5, Date.valueOf(receita.getData()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    receita.setIdReceita(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todas as receitas cadastradas. */
    public List<Receita> getAll() {
        String sql = "SELECT ID_RECEITA, ID_USUARIO, DS_RECEITA, DS_CATEGORIA, VL_RECEITA, DT_RECEITA "
                + "FROM T_FC_RECEITA ORDER BY DT_RECEITA, ID_RECEITA";
        List<Receita> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Receita(
                        rs.getInt("ID_RECEITA"),
                        rs.getInt("ID_USUARIO"),
                        rs.getString("DS_RECEITA"),
                        rs.getString("DS_CATEGORIA"),
                        rs.getBigDecimal("VL_RECEITA"),
                        rs.getDate("DT_RECEITA").toLocalDate()));
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
