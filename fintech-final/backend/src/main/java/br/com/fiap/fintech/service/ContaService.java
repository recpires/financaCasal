package br.com.fiap.fintech.service;

import br.com.fiap.fintech.dto.ContaRequest;
import br.com.fiap.fintech.exception.ConflitoException;
import br.com.fiap.fintech.exception.RecursoNaoEncontradoException;
import br.com.fiap.fintech.exception.RegraNegocioException;
import br.com.fiap.fintech.model.Cliente;
import br.com.fiap.fintech.model.Conta;
import br.com.fiap.fintech.model.TipoConta;
import br.com.fiap.fintech.repository.ContaRepository;
import br.com.fiap.fintech.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ContaService {

    private static final BigDecimal LIMITE_PADRAO = new BigDecimal("1000.00");
    private static final BigDecimal TAXA_POUPANCA_PADRAO = new BigDecimal("0.0050");

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final ClienteService clienteService;

    public ContaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository,
                        ClienteService clienteService) {
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
        this.clienteService = clienteService;
    }

    public List<Conta> listar(Long clienteId) {
        return clienteId == null
                ? contaRepository.findAllByOrderByIdAsc()
                : contaRepository.findByClienteIdOrderByIdAsc(clienteId);
    }

    public Conta buscar(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta " + id + " nao encontrada"));
    }

    /** Abre a conta com saldo zero; o dinheiro entra depois, por transacoes de deposito. */
    @Transactional
    public Conta criar(ContaRequest request) {
        Cliente cliente = clienteService.buscar(request.clienteId());

        Conta conta = new Conta();
        conta.setCliente(cliente);
        conta.setTipo(request.tipo());
        conta.setNumero(gerarNumero());
        conta.setAgencia(request.agencia() == null || request.agencia().isBlank() ? "0001" : request.agencia());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setAtiva(request.ativa() == null || request.ativa());
        conta.setDataAbertura(LocalDate.now());
        aplicarRegrasDoTipo(conta, request);
        return contaRepository.save(conta);
    }

    @Transactional
    public Conta atualizar(Long id, ContaRequest request) {
        Conta conta = buscar(id);
        if (!conta.getCliente().getId().equals(request.clienteId())) {
            throw new RegraNegocioException("O titular da conta nao pode ser alterado");
        }
        if (conta.getTipo() != request.tipo()) {
            throw new RegraNegocioException("O tipo da conta nao pode ser alterado");
        }
        if (request.agencia() != null && !request.agencia().isBlank()) {
            conta.setAgencia(request.agencia());
        }
        if (request.ativa() != null) {
            conta.setAtiva(request.ativa());
        }
        aplicarRegrasDoTipo(conta, request);

        // Reduzir o limite nao pode deixar a conta usando mais cheque especial do que o permitido
        if (conta.getSaldoDisponivel().signum() < 0) {
            throw new RegraNegocioException("O novo limite e menor do que o cheque especial ja utilizado");
        }
        return contaRepository.save(conta);
    }

    /** So encerra conta com saldo zerado. O extrato da conta e removido junto. */
    @Transactional
    public void excluir(Long id) {
        Conta conta = buscar(id);
        if (conta.getSaldo().signum() != 0) {
            throw new ConflitoException("A conta ainda tem saldo. Zere o saldo antes de encerra-la");
        }
        transacaoRepository.deleteByContaId(id);
        contaRepository.delete(conta);
    }

    /**
     * Regras da fase anterior:
     * - corrente tem cheque especial, mas so para cliente elegivel a credito (21+ anos);
     * - poupanca nao tem cheque especial e rende uma taxa mensal.
     */
    private void aplicarRegrasDoTipo(Conta conta, ContaRequest request) {
        if (conta.getTipo() == TipoConta.CORRENTE) {
            BigDecimal limite = request.limiteChequeEspecial() == null ? LIMITE_PADRAO : request.limiteChequeEspecial();
            conta.setLimiteChequeEspecial(conta.getCliente().isElegivelParaCredito() ? limite : BigDecimal.ZERO);
            conta.setTaxaRendimentoMensal(null);
        } else {
            conta.setLimiteChequeEspecial(null);
            conta.setTaxaRendimentoMensal(request.taxaRendimentoMensal() == null
                    ? TAXA_POUPANCA_PADRAO : request.taxaRendimentoMensal());
        }
    }

    /** Numero no formato 123456-7, onde o ultimo digito e verificador (soma dos digitos % 10). */
    private String gerarNumero() {
        String numero;
        do {
            int base = ThreadLocalRandom.current().nextInt(100000, 1000000);
            int digito = String.valueOf(base).chars().map(c -> c - '0').sum() % 10;
            numero = base + "-" + digito;
        } while (contaRepository.existsByNumero(numero));
        return numero;
    }
}
