package br.com.fiap.fintech.service;

import br.com.fiap.fintech.exception.ConflitoException;
import br.com.fiap.fintech.exception.RecursoNaoEncontradoException;
import br.com.fiap.fintech.exception.RegraNegocioException;
import br.com.fiap.fintech.model.Cliente;
import br.com.fiap.fintech.repository.ClienteRepository;
import br.com.fiap.fintech.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;

    public ClienteService(ClienteRepository clienteRepository, ContaRepository contaRepository) {
        this.clienteRepository = clienteRepository;
        this.contaRepository = contaRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAllByOrderByNomeAsc();
    }

    public Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " nao encontrado"));
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        normalizar(cliente);
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new ConflitoException("Ja existe um cliente com este CPF");
        }
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscar(id);
        normalizar(dados);
        if (clienteRepository.existsByCpfAndIdNot(dados.getCpf(), id)) {
            throw new ConflitoException("Ja existe outro cliente com este CPF");
        }
        cliente.setNome(dados.getNome());
        cliente.setCpf(dados.getCpf());
        cliente.setEmail(dados.getEmail());
        cliente.setTelefone(dados.getTelefone());
        cliente.setIdade(dados.getIdade());
        return clienteRepository.save(cliente);
    }

    /** Cliente com conta aberta nao pode ser removido: as contas precisam ser encerradas antes. */
    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscar(id);
        if (contaRepository.existsByClienteId(id)) {
            throw new ConflitoException("O cliente possui contas. Encerre as contas antes de exclui-lo");
        }
        clienteRepository.delete(cliente);
    }

    /** Guarda o CPF so com digitos e o e-mail em minusculas. */
    private void normalizar(Cliente cliente) {
        String cpf = cliente.getCpf() == null ? "" : cliente.getCpf().replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new RegraNegocioException("O CPF deve ter 11 digitos");
        }
        cliente.setCpf(cpf);
        cliente.setNome(cliente.getNome().trim());
        cliente.setEmail(cliente.getEmail().trim().toLowerCase());
    }
}
