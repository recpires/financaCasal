package br.com.fiap.fintech.dao;

import br.com.fiap.fintech.exception.DBException;
import br.com.fiap.fintech.factory.ConnectionFactory;
import br.com.fiap.fintech.model.Meta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetaDAO {

    private static final String TABELA = "T_FC_META";

    /** Cadastra uma meta e preenche o ID gerado pelo banco no próprio objeto. */
    public void insert(Meta meta) {
        String sql = "INSERT INTO T_FC_META (DS_META, VL_ALVO, VL_ATUAL, DT_LIMITE) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_META"})) {

            stmt.setString(1, meta.getDescricao());
            stmt.setBigDecimal(2, meta.getValorAlvo());
            stmt.setBigDecimal(3, meta.getValorAtual());
            stmt.setDate(4, meta.getDataLimite() == null ? null : Date.valueOf(meta.getDataLimite()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    meta.setIdMeta(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todas as metas cadastradas. */
    public List<Meta> getAll() {
        String sql = "SELECT ID_META, DS_META, VL_ALVO, VL_ATUAL, DT_LIMITE FROM T_FC_META ORDER BY ID_META";
        List<Meta> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date limite = rs.getDate("DT_LIMITE");
                lista.add(new Meta(
                        rs.getInt("ID_META"),
                        rs.getString("DS_META"),
                        rs.getBigDecimal("VL_ALVO"),
                        rs.getBigDecimal("VL_ATUAL"),
                        limite == null ? null : limite.toLocalDate()));
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
