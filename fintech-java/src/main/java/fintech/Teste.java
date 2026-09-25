package fintech;

import fintech.dao.ClienteDAO;
import fintech.dao.ContaDAO;
import fintech.dao.TransacaoDAO;
import fintech.exception.DBException;
import fintech.model.Cliente;
import fintech.model.Conta;
import fintech.model.ContaCorrente;
import fintech.model.ContaPoupanca;
import fintech.model.Transacao;

import java.util.List;

/**
 * Classe de testes da integracao com o Oracle.
 * Cadastra clientes, contas e transacoes com insert() e depois
 * consulta tudo com getAll().
 */
public class Teste {

    public static void main(String[] args) {
        List<Cliente> clientes = testarClientes();
        if (clientes.isEmpty()) {
            System.out.println("\nContas e transacoes nao testadas: nenhum cliente cadastrado.");
            return;
        }

        List<Conta> contas = testarContas(clientes);
        if (contas.isEmpty()) {
            System.out.println("\nTransacoes nao testadas: nenhuma conta cadastrada.");
            return;
        }

        testarTransacoes(contas);
    }

    private static List<Cliente> testarClientes() {
        titulo("1. CLIENTES");
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> novos = List.of(
                new Cliente("Ana Beatriz Souza", "123.456.789-01", "ana.souza@email.com", "11 98888-1111", 27),
                new Cliente("Carlos Mendes", "987.654.321-09"),
                new Cliente("Juliana Ferreira", "321.654.987-00", "juliana.f@email.com", "11 97777-2222", 34),
                new Cliente("Rafael Lima", "456.789.123-45", "rafael.lima@email.com", "21 96666-3333", 22),
                new Cliente("Beatriz Rocha", "654.321.987-11", "bia.rocha@email.com", "31 95555-4444", 45));

        try {
            for (Cliente cliente : novos) {
                dao.insert(cliente);
                System.out.println("Cadastrado (id " + cliente.getIdCliente() + "): " + cliente);
            }
            System.out.println("\n--- getAll() ---");
            for (Cliente cliente : dao.getAll()) {
                System.out.println("id " + cliente.getIdCliente() + " | " + cliente);
            }
            return novos;
        } catch (DBException e) {
            exibirErro(e);
            return List.of();
        }
    }

    private static List<Conta> testarContas(List<Cliente> clientes) {
        titulo("2. CONTAS");
        ContaDAO dao = new ContaDAO();

        ContaCorrente ccAna = new ContaCorrente(clientes.get(0), "0001", 2000.00, 1500.00, 29.90);
        ContaPoupanca poupancaAna = new ContaPoupanca(clientes.get(0), "0001", 3000.00, 0.005, 15);
        ContaCorrente ccCarlos = new ContaCorrente(clientes.get(1), 800.00);
        ContaCorrente ccJuliana = new ContaCorrente(clientes.get(2), "0001", 5000.00, 2000.00, 19.90);
        ContaPoupanca poupancaRafael = new ContaPoupanca(clientes.get(3), 1200.00);

        // Movimentacoes antes de gravar: o saldo salvo ja reflete o extrato
        ccAna.depositar(1200.00, "Salario - Empresa XPTO");
        ccAna.sacar(300.00);
        poupancaAna.transferir(ccAna, 500.00);
        ccCarlos.depositar(250.00);
        ccJuliana.sacar(150.00);
        poupancaRafael.depositar(300.00, "Deposito programado");

        List<Conta> novas = List.of(ccAna, poupancaAna, ccCarlos, ccJuliana, poupancaRafael);

        try {
            for (Conta conta : novas) {
                dao.insert(conta);
                System.out.println("Cadastrada (id " + conta.getIdConta() + "): " + conta);
            }
            System.out.println("\n--- getAll() ---");
            for (Conta conta : dao.getAll()) {
                System.out.println("id " + conta.getIdConta() + " | " + conta);
            }
            return novas;
        } catch (DBException e) {
            exibirErro(e);
            return List.of();
        }
    }

    private static void testarTransacoes(List<Conta> contas) {
        titulo("3. TRANSACOES (extrato das contas)");
        TransacaoDAO dao = new TransacaoDAO();

        try {
            for (Conta conta : contas) {
                for (Transacao transacao : conta.getExtrato()) {
                    transacao.setIdConta(conta.getIdConta());
                    dao.insert(transacao);
                    System.out.println("Cadastrada (conta " + conta.getIdConta() + "): " + transacao);
                }
            }
            System.out.println("\n--- getAll() ---");
            for (Transacao transacao : dao.getAll()) {
                System.out.println("conta " + transacao.getIdConta() + " | " + transacao);
            }
        } catch (DBException e) {
            exibirErro(e);
        }
    }

    private static void titulo(String texto) {
        System.out.println("\n========== " + texto + " ==========");
    }

    private static void exibirErro(DBException e) {
        System.err.println(">> " + e.getMessage());
        if (e.getCause() != null) {
            System.err.println("   Detalhe tecnico: " + e.getCause().getMessage());
        }
    }
}
