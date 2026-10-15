package br.com.fiap.fintech.config;

import br.com.fiap.fintech.dto.ContaRequest;
import br.com.fiap.fintech.dto.TransacaoRequest;
import br.com.fiap.fintech.model.*;
import br.com.fiap.fintech.repository.ClienteRepository;
import br.com.fiap.fintech.repository.UsuarioRepository;
import br.com.fiap.fintech.service.ClienteService;
import br.com.fiap.fintech.service.ContaService;
import br.com.fiap.fintech.service.SenhaUtil;
import br.com.fiap.fintech.service.TransacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Na primeira execucao cria o usuario de teste do login e alguns dados de exemplo.
 * Se as tabelas ja tiverem dados, nada e alterado.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    public static final String EMAIL_TESTE = "teste@fintech.com";
    public static final String SENHA_TESTE = "fintech123";

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;
    private final ContaService contaService;
    private final TransacaoService transacaoService;

    public DadosIniciais(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository,
                         ClienteService clienteService, ContaService contaService,
                         TransacaoService transacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
        this.contaService = contaService;
        this.transacaoService = transacaoService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmailIgnoreCase(EMAIL_TESTE).isEmpty()) {
            usuarioRepository.save(new Usuario("Usuario Teste", EMAIL_TESTE, SenhaUtil.hash(SENHA_TESTE)));
            log.info("Usuario de teste criado: {} / {}", EMAIL_TESTE, SENHA_TESTE);
        }

        if (clienteRepository.count() == 0) {
            criarDadosDeExemplo();
            log.info("Dados de exemplo criados (clientes, contas e transacoes)");
        }
    }

    private void criarDadosDeExemplo() {
        Cliente ana = clienteService.criar(cliente("Ana Beatriz Souza", "12345678901", "ana.souza@email.com", "11 98888-1111", 27));
        Cliente carlos = clienteService.criar(cliente("Carlos Mendes", "98765432109", "carlos.mendes@email.com", "11 97777-2222", 19));
        Cliente juliana = clienteService.criar(cliente("Juliana Ferreira", "32165498700", "juliana.f@email.com", "21 96666-3333", 34));

        Conta ccAna = contaService.criar(new ContaRequest(ana.getId(), TipoConta.CORRENTE, "0001", new BigDecimal("1500.00"), null, true));
        Conta poupancaAna = contaService.criar(new ContaRequest(ana.getId(), TipoConta.POUPANCA, "0001", null, new BigDecimal("0.0050"), true));
        Conta ccCarlos = contaService.criar(new ContaRequest(carlos.getId(), TipoConta.CORRENTE, "0001", null, null, true));
        Conta ccJuliana = contaService.criar(new ContaRequest(juliana.getId(), TipoConta.CORRENTE, "0002", new BigDecimal("2000.00"), null, true));

        transacao(ccAna, TipoTransacao.DEPOSITO, "4500.00", "Salario - Empresa XPTO");
        transacao(ccAna, TipoTransacao.PAGAMENTO, "250.00", "Supermercado Central");
        transacao(ccAna, TipoTransacao.PAGAMENTO, "180.50", "Conta de luz");
        transacao(ccAna, TipoTransacao.SAQUE, "300.00", "Saque caixa eletronico");
        transacao(poupancaAna, TipoTransacao.DEPOSITO, "3000.00", "Reserva de emergencia");
        transacao(poupancaAna, TipoTransacao.RENDIMENTO, "15.00", "Rendimento do mes");
        transacao(ccCarlos, TipoTransacao.DEPOSITO, "800.00", "Deposito inicial");
        transacao(ccCarlos, TipoTransacao.TAXA, "29.90", "Tarifa mensal de manutencao");
        transacao(ccJuliana, TipoTransacao.DEPOSITO, "6200.00", "Salario");
        transacao(ccJuliana, TipoTransacao.PAGAMENTO, "2100.00", "Aluguel");
    }

    private Cliente cliente(String nome, String cpf, String email, String telefone, int idade) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setEmail(email);
        cliente.setTelefone(telefone);
        cliente.setIdade(idade);
        return cliente;
    }

    private void transacao(Conta conta, TipoTransacao tipo, String valor, String descricao) {
        transacaoService.criar(new TransacaoRequest(conta.getId(), tipo, new BigDecimal(valor), descricao));
    }
}
