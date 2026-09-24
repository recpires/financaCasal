package br.com.fiap.fintech;

import br.com.fiap.fintech.dao.DespesaDAO;
import br.com.fiap.fintech.dao.MetaDAO;
import br.com.fiap.fintech.dao.ReceitaDAO;
import br.com.fiap.fintech.dao.UsuarioDAO;
import br.com.fiap.fintech.exception.DBException;
import br.com.fiap.fintech.model.Despesa;
import br.com.fiap.fintech.model.Meta;
import br.com.fiap.fintech.model.Receita;
import br.com.fiap.fintech.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe de testes: cadastra 5 registros de cada entidade com insert()
 * e em seguida lista tudo com getAll().
 */
public class Teste {

    public static void main(String[] args) {
        List<Usuario> usuarios = testarUsuarios();

        // Receitas e despesas pertencem a um usuário, então só testamos se os usuários foram cadastrados
        if (usuarios.size() >= 2) {
            int idRodrigo = usuarios.get(0).getIdUsuario();
            int idAna = usuarios.get(1).getIdUsuario();
            testarReceitas(idRodrigo, idAna);
            testarDespesas(idRodrigo, idAna);
        } else {
            System.out.println("\nReceitas e despesas não testadas: nenhum usuário disponível.");
        }

        testarMetas();
    }

    private static List<Usuario> testarUsuarios() {
        titulo("USUÁRIOS");
        UsuarioDAO dao = new UsuarioDAO();
        List<Usuario> novos = List.of(
                new Usuario("Rodrigo Pires", "rodrigo@financascasal.com", LocalDate.of(1990, 3, 12)),
                new Usuario("Ana Pires", "ana@financascasal.com", LocalDate.of(1992, 7, 25)),
                new Usuario("Carlos Souza", "carlos@financascasal.com", LocalDate.of(1988, 11, 2)),
                new Usuario("Mariana Souza", "mariana@financascasal.com", LocalDate.of(1991, 1, 30)),
                new Usuario("Lucas Almeida", "lucas@financascasal.com", LocalDate.of(1995, 9, 18)));

        try {
            for (Usuario u : novos) {
                dao.insert(u);
                System.out.println("Cadastrado: " + u);
            }
            System.out.println("\n--- getAll() ---");
            dao.getAll().forEach(System.out::println);
            return novos;
        } catch (DBException e) {
            exibirErro(e);
            return List.of();
        }
    }

    private static void testarReceitas(int idRodrigo, int idAna) {
        titulo("RECEITAS");
        ReceitaDAO dao = new ReceitaDAO();
        List<Receita> novas = List.of(
                new Receita(idRodrigo, "Salário", "Salário", new BigDecimal("6500.00"), LocalDate.of(2026, 9, 5)),
                new Receita(idAna, "Salário", "Salário", new BigDecimal("4500.00"), LocalDate.of(2026, 9, 5)),
                new Receita(idRodrigo, "Freelance site", "Extra", new BigDecimal("1200.00"), LocalDate.of(2026, 9, 12)),
                new Receita(idAna, "Dividendos", "Investimentos", new BigDecimal("350.75"), LocalDate.of(2026, 9, 15)),
                new Receita(idRodrigo, "Venda bicicleta", "Extra", new BigDecimal("900.00"), LocalDate.of(2026, 9, 20)));

        try {
            for (Receita r : novas) {
                dao.insert(r);
                System.out.println("Cadastrada: " + r);
            }
            System.out.println("\n--- getAll() ---");
            dao.getAll().forEach(System.out::println);
        } catch (DBException e) {
            exibirErro(e);
        }
    }

    private static void testarDespesas(int idRodrigo, int idAna) {
        titulo("DESPESAS");
        DespesaDAO dao = new DespesaDAO();
        List<Despesa> novas = List.of(
                new Despesa(idRodrigo, "Mercado mensal", "Alimentação", new BigDecimal("250.00"), LocalDate.of(2026, 9, 6)),
                new Despesa(idRodrigo, "Conta de luz", "Moradia", new BigDecimal("180.50"), LocalDate.of(2026, 9, 10)),
                new Despesa(idAna, "Aluguel", "Moradia", new BigDecimal("2100.00"), LocalDate.of(2026, 9, 10)),
                new Despesa(idAna, "Internet", "Moradia", new BigDecimal("119.90"), LocalDate.of(2026, 9, 15)),
                new Despesa(idRodrigo, "Jantar de aniversário", "Lazer", new BigDecimal("320.00"), LocalDate.of(2026, 9, 21)));

        try {
            for (Despesa d : novas) {
                dao.insert(d);
                System.out.println("Cadastrada: " + d);
            }
            System.out.println("\n--- getAll() ---");
            dao.getAll().forEach(System.out::println);
        } catch (DBException e) {
            exibirErro(e);
        }
    }

    private static void testarMetas() {
        titulo("METAS");
        MetaDAO dao = new MetaDAO();
        List<Meta> novas = List.of(
                new Meta("Viagem de férias", new BigDecimal("12000.00"), new BigDecimal("3500.00"), LocalDate.of(2027, 1, 15)),
                new Meta("Reserva de emergência", new BigDecimal("30000.00"), new BigDecimal("12450.00"), LocalDate.of(2027, 6, 30)),
                new Meta("Troca do carro", new BigDecimal("45000.00"), new BigDecimal("8000.00"), LocalDate.of(2028, 3, 1)),
                new Meta("Casamento", new BigDecimal("50000.00"), new BigDecimal("15000.00"), LocalDate.of(2027, 11, 20)),
                new Meta("Curso de inglês", new BigDecimal("6000.00"), BigDecimal.ZERO, null));

        try {
            for (Meta m : novas) {
                dao.insert(m);
                System.out.println("Cadastrada: " + m);
            }
            System.out.println("\n--- getAll() ---");
            dao.getAll().forEach(System.out::println);
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
            System.err.println("   Detalhe técnico: " + e.getCause().getMessage());
        }
    }
}
