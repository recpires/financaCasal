package br.com.fiap.fintech.dao;

import br.com.fiap.fintech.exception.DBException;
import br.com.fiap.fintech.factory.ConnectionFactory;
import br.com.fiap.fintech.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String TABELA = "T_FC_USUARIO";

    /** Cadastra um usuário e preenche o ID gerado pelo banco no próprio objeto. */
    public void insert(Usuario usuario) {
        String sql = "INSERT INTO T_FC_USUARIO (NM_USUARIO, DS_EMAIL, DT_NASCIMENTO) VALUES (?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_USUARIO"})) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setDate(3, usuario.getDataNascimento() == null ? null : Date.valueOf(usuario.getDataNascimento()));
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setIdUsuario(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todos os usuários cadastrados. */
    public List<Usuario> getAll() {
        String sql = "SELECT ID_USUARIO, NM_USUARIO, DS_EMAIL, DT_NASCIMENTO FROM T_FC_USUARIO ORDER BY ID_USUARIO";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date nascimento = rs.getDate("DT_NASCIMENTO");
                lista.add(new Usuario(
                        rs.getInt("ID_USUARIO"),
                        rs.getString("NM_USUARIO"),
                        rs.getString("DS_EMAIL"),
                        nascimento == null ? null : nascimento.toLocalDate()));
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
