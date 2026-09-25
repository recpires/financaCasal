package fintech.dao;

import fintech.exception.DBException;
import fintech.factory.ConnectionFactory;
import fintech.model.Cliente;
import fintech.model.Conta;
import fintech.model.ContaCorrente;
import fintech.model.ContaPoupanca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Grava ContaCorrente e ContaPoupanca na mesma tabela (T_CONTA).
 * A coluna TP_CONTA indica qual subclasse deve ser recriada na leitura.
 */
public class ContaDAO {

    private static final String TABELA = "T_CONTA";

    /** Cadastra a conta e guarda no objeto o id gerado pelo banco. O titular ja deve estar cadastrado. */
    public void insert(Conta conta) {
        String sql = "INSERT INTO T_CONTA (ID_CLIENTE, TP_CONTA, NR_CONTA, NR_AGENCIA, VL_SALDO, ST_ATIVA, "
                + "VL_LIMITE_CHEQUE, VL_TAXA_MANUTENCAO, TX_RENDIMENTO_MENSAL, NR_DIA_ANIVERSARIO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_CONTA"})) {

            stmt.setInt(1, conta.getTitular().getIdCliente());
            stmt.setString(3, conta.getNumero());
            stmt.setString(4, conta.getAgencia());
            stmt.setDouble(5, conta.getSaldo());
            stmt.setString(6, conta.isAtiva() ? "S" : "N");

            // Colunas especificas de cada subclasse; as da outra ficam nulas
            if (conta instanceof ContaCorrente cc) {
                stmt.setString(2, "CORRENTE");
                stmt.setDouble(7, cc.getLimiteChequeEspecial());
                stmt.setDouble(8, cc.getTaxaManutencao());
                stmt.setNull(9, Types.NUMERIC);
                stmt.setNull(10, Types.NUMERIC);
            } else if (conta instanceof ContaPoupanca cp) {
                stmt.setString(2, "POUPANCA");
                stmt.setNull(7, Types.NUMERIC);
                stmt.setNull(8, Types.NUMERIC);
                stmt.setDouble(9, cp.getTaxaRendimentoMensal());
                stmt.setInt(10, cp.getDiaAniversario());
            } else {
                throw new IllegalArgumentException("Tipo de conta nao suportado: " + conta.getTipoConta());
            }
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    conta.setIdConta(chaves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("cadastrar", TABELA, e);
        }
    }

    /** Retorna todas as contas cadastradas, ja com o titular preenchido. */
    public List<Conta> getAll() {
        String sql = "SELECT c.ID_CONTA, c.TP_CONTA, c.NR_CONTA, c.NR_AGENCIA, c.VL_SALDO, c.ST_ATIVA, "
                + "c.VL_LIMITE_CHEQUE, c.VL_TAXA_MANUTENCAO, c.TX_RENDIMENTO_MENSAL, c.NR_DIA_ANIVERSARIO, "
                + "cl.ID_CLIENTE, cl.NM_CLIENTE, cl.NR_CPF, cl.DS_EMAIL, cl.NR_TELEFONE, cl.NR_IDADE "
                + "FROM T_CONTA c INNER JOIN T_CLIENTE cl ON cl.ID_CLIENTE = c.ID_CLIENTE "
                + "ORDER BY c.ID_CONTA";
        List<Conta> lista = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente titular = new Cliente(
                        rs.getInt("ID_CLIENTE"),
                        rs.getString("NM_CLIENTE"),
                        rs.getString("NR_CPF"),
                        rs.getString("DS_EMAIL"),
                        rs.getString("NR_TELEFONE"),
                        rs.getInt("NR_IDADE"));

                int id = rs.getInt("ID_CONTA");
                String numero = rs.getString("NR_CONTA");
                String agencia = rs.getString("NR_AGENCIA");
                double saldo = rs.getDouble("VL_SALDO");
                boolean ativa = "S".equals(rs.getString("ST_ATIVA"));

                if ("CORRENTE".equals(rs.getString("TP_CONTA"))) {
                    lista.add(new ContaCorrente(id, numero, agencia, saldo, titular, ativa,
                            rs.getDouble("VL_LIMITE_CHEQUE"), rs.getDouble("VL_TAXA_MANUTENCAO")));
                } else {
                    lista.add(new ContaPoupanca(id, numero, agencia, saldo, titular, ativa,
                            rs.getDouble("TX_RENDIMENTO_MENSAL"), rs.getInt("NR_DIA_ANIVERSARIO")));
                }
            }
        } catch (SQLException e) {
            throw DBException.from("consultar", TABELA, e);
        }
        return lista;
    }
}
