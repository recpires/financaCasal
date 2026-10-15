package br.com.fiap.fintech.service;

import br.com.fiap.fintech.dto.TransacaoRequest;
import br.com.fiap.fintech.exception.RecursoNaoEncontradoException;
import br.com.fiap.fintech.exception.RegraNegocioException;
import br.com.fiap.fintech.model.Conta;
import br.com.fiap.fintech.model.Transacao;
import br.com.fiap.fintech.repository.ContaRepository;
import br.com.fiap.fintech.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Toda transacao mexe no saldo da conta:
 * - criar aplica o valor;
 * - atualizar desfaz o valor antigo e aplica o novo;
 * - excluir desfaz o valor (estorno).
 * Em todos os casos a conta nao pode passar do permitido (saldo + cheque especial).
 */
@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final ContaService contaService;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository,
                            ContaService contaService) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.contaService = contaService;
    }

    public List<Transacao> listar(Long contaId) {
        return contaId == null
                ? transacaoRepository.findAllByOrderByDataHoraDescIdDesc()
                : transacaoRepository.findByContaIdOrderByDataHoraDescIdDesc(contaId);
    }

    public Transacao buscar(Long id) {
        return transacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transacao " + id + " nao encontrada"));
    }

    @Transactional
    public Transacao criar(TransacaoRequest request) {
        Conta conta = contaService.buscar(request.contaId());
        if (!conta.isAtiva()) {
            throw new RegraNegocioException("A conta esta inativa e nao aceita movimentacoes");
        }

        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        copiarDados(transacao, request);
        transacao.setDataHora(LocalDateTime.now());

        aplicarNoSaldo(conta, transacao, "Saldo insuficiente para esta operacao");
        contaRepository.save(conta);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public Transacao atualizar(Long id, TransacaoRequest request) {
        Transacao transacao = buscar(id);
        Conta conta = transacao.getConta();
        if (!conta.getId().equals(request.contaId())) {
            throw new RegraNegocioException("A conta de uma transacao nao pode ser alterada");
        }

        estornarDoSaldo(conta, transacao);
        copiarDados(transacao, request);
        aplicarNoSaldo(conta, transacao, "Saldo insuficiente para o novo valor da transacao");

        contaRepository.save(conta);
        return transacaoRepository.save(transacao);
    }

    @Transactional
    public void excluir(Long id) {
        Transacao transacao = buscar(id);
        Conta conta = transacao.getConta();

        estornarDoSaldo(conta, transacao);
        if (conta.getSaldoDisponivel().signum() < 0) {
            throw new RegraNegocioException("O estorno desta transacao deixaria a conta sem saldo suficiente");
        }

        contaRepository.save(conta);
        transacaoRepository.delete(transacao);
    }

    private void copiarDados(Transacao transacao, TransacaoRequest request) {
        transacao.setTipo(request.tipo());
        transacao.setValor(request.valor());
        transacao.setDescricao(request.descricao().trim());
    }

    private void aplicarNoSaldo(Conta conta, Transacao transacao, String mensagemErro) {
        conta.setSaldo(conta.getSaldo().add(transacao.getValorComSinal()));
        if (conta.getSaldoDisponivel().signum() < 0) {
            throw new RegraNegocioException(mensagemErro);
        }
    }

    private void estornarDoSaldo(Conta conta, Transacao transacao) {
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValorComSinal()));
    }
}
