package fintech;

import fintech.model.Cartao;
import fintech.model.CartaoCredito;
import fintech.model.CartaoDebito;
import fintech.model.Cliente;
import fintech.model.Conta;
import fintech.model.ContaCorrente;
import fintech.model.ContaPoupanca;
import fintech.model.Tributavel;

/**
 * Classe de execucao do projeto Fintech.
 * Instancia as classes, alimenta os atributos e invoca os metodos,
 * demonstrando heranca, polimorfismo e encapsulamento.
 */
public class Main {

    public static void main(String[] args) {

        titulo("1. CRIACAO DOS CLIENTES (encapsulamento)");

        Cliente ana = new Cliente("Ana Beatriz Souza", "123.456.789-01", "ana.souza@email.com", "11 98888-1111", 27);
        Cliente carlos = new Cliente("Carlos Mendes", "987.654.321-09");

        System.out.println(ana);
        System.out.println(carlos);
        System.out.println("Nome resumido de Ana........: " + ana.getNomeResumido());
        System.out.println("CPF exposto (mascarado).....: " + ana.getCpfMascarado());
        System.out.println("Ana elegivel para credito?..: " + ana.isElegivelParaCredito());
        System.out.println("Carlos elegivel para credito: " + carlos.isElegivelParaCredito());

        // O encapsulamento barra dados invalidos antes de corromper o objeto
        try {
            carlos.setIdade(15);
        } catch (IllegalArgumentException e) {
            System.out.println("Validacao do setIdade.......: " + e.getMessage());
        }

        titulo("2. INSTANCIANDO AS CONTAS (heranca em acao)");

        ContaCorrente ccAna = new ContaCorrente(ana, "0001", 2000.00, 1500.00, 29.90);
        ContaPoupanca poupancaAna = new ContaPoupanca(ana, "0001", 3000.00, 0.005, 15);
        ContaCorrente ccCarlos = new ContaCorrente(carlos, 800.00);

        System.out.println(ccAna);
        System.out.println(poupancaAna);
        System.out.println(ccCarlos);
        System.out.println("Limite de Carlos foi zerado pela regra de credito: R$ "
                + String.format("%.2f", ccCarlos.getLimiteChequeEspecial()));

        titulo("3. OPERACOES E SOBRECARGA DE METODOS");

        ccAna.depositar(500.00);                                  // versao simples
        ccAna.depositar(1200.00, "Salario - Empresa XPTO");       // versao sobrecarregada
        System.out.printf("Saldo da conta corrente de Ana: R$ %.2f%n", ccAna.getSaldo());

        boolean saque1 = ccAna.sacar(300.00);
        System.out.println("Saque de R$ 300,00 na CC.....: " + (saque1 ? "aprovado (com tarifa)" : "negado"));
        System.out.printf("Saldo apos saque + tarifa....: R$ %.2f%n", ccAna.getSaldo());

        titulo("4. POLIMORFISMO NO SAQUE (mesma chamada, regras diferentes)");

        // Note que a variavel e do tipo Conta (superclasse), mas o metodo
        // executado e o da subclasse: isso e polimorfismo em tempo de execucao.
        Conta[] contas = { ccAna, poupancaAna, ccCarlos };

        for (Conta conta : contas) {
            double valor = 4000.00;
            boolean ok = conta.sacar(valor);
            System.out.printf("%-16s | saque de R$ %.2f -> %s | saldo: R$ %.2f%n",
                    conta.getTipoConta(), valor, ok ? "APROVADO" : "NEGADO", conta.getSaldo());
        }
        System.out.println();
        System.out.println("A conta corrente aprovou usando cheque especial; a poupanca negou por nao ter saldo.");
        System.out.println("Cheque especial utilizado por Ana: R$ "
                + String.format("%.2f", ccAna.getChequeEspecialUtilizado()));
        System.out.println("Saques gratuitos restantes na poupanca: " + poupancaAna.getSaquesRestantesGratuitos());

        titulo("5. TRANSFERENCIA ENTRE CONTAS DE TIPOS DIFERENTES");

        boolean transferiu = poupancaAna.transferir(ccAna, 1500.00);
        System.out.println("Transferencia poupanca -> corrente: " + (transferiu ? "OK" : "FALHOU"));
        System.out.printf("Poupanca: R$ %.2f | Corrente: R$ %.2f%n", poupancaAna.getSaldo(), ccAna.getSaldo());

        titulo("6. CARTOES (segunda hierarquia + polimorfismo)");

        CartaoDebito debito = new CartaoDebito(ccAna, 1234, 800.00);
        CartaoCredito credito = new CartaoCredito(ccAna, 4321, 5000.00, 10);

        Cartao[] cartoes = { debito, credito };
        for (Cartao cartao : cartoes) {
            boolean pago = cartao.pagar(450.00, "Supermercado Central");
            System.out.printf("%-18s | compra de R$ 450,00 -> %s%n",
                    cartao.getTipoCartao(), pago ? "APROVADA" : "NEGADA");
        }

        credito.parcelar(600.00, 10, "Notebook");
        System.out.println();
        System.out.println(debito);
        System.out.println("Limite diario disponivel no debito: R$ "
                + String.format("%.2f", debito.getLimiteDiarioDisponivel()));
        System.out.println(credito);

        System.out.println("\nPagamento da fatura do credito: "
                + (credito.pagarFatura() ? "realizado" : "recusado"));
        System.out.printf("Saldo da conta corrente apos a fatura: R$ %.2f%n", ccAna.getSaldo());

        titulo("7. FECHAMENTO MENSAL (metodo abstrato implementado por cada subclasse)");

        for (Conta conta : contas) {
            double ajuste = conta.aplicarAtualizacaoMensal();
            System.out.printf("%-16s | ajuste do mes: R$ %+9.2f | saldo final: R$ %.2f%n",
                    conta.getTipoConta(), ajuste, conta.getSaldo());
        }

        System.out.println();
        System.out.printf("Projecao de rendimento da poupanca em 12 meses: R$ %.2f%n",
                poupancaAna.projetarRendimento(12));

        titulo("8. POLIMORFISMO POR INTERFACE (Tributavel)");

        Tributavel[] tributaveis = { ccAna, ccCarlos, credito };
        double totalTributos = 0;
        for (Tributavel item : tributaveis) {
            double tributo = item.calcularTributo();
            totalTributos += tributo;
            System.out.printf("%-28s | R$ %.2f%n", item.getNomeTributo(), tributo);
        }
        System.out.printf("TOTAL DE TRIBUTOS DO PERIODO : R$ %.2f%n", totalTributos);

        titulo("9. EXTRATOS");

        System.out.println(ccAna.gerarExtrato());
        System.out.println(poupancaAna.gerarExtrato());

        titulo("10. RESUMO FINAL");

        double patrimonio = 0;
        for (Conta conta : contas) {
            patrimonio += conta.getSaldo();
            System.out.println(conta);
        }
        System.out.printf("%nSoma dos saldos das 3 contas: R$ %.2f%n", patrimonio);
        System.out.println("Transacoes registradas na conta corrente de Ana: " + ccAna.getQuantidadeTransacoes());
        System.out.printf("Conferencia extrato x saldo (CC Ana): R$ %.2f%n", ccAna.calcularTotalMovimentado());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(borda());
        System.out.println(" " + texto);
        System.out.println(borda());
    }

    /** Borda dos titulos (sem String.repeat, para rodar em qualquer JDK). */
    private static String borda() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 78; i++) {
            sb.append('=');
        }
        return sb.toString();
    }
}
