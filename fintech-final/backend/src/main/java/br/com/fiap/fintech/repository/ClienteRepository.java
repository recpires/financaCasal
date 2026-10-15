package br.com.fiap.fintech.repository;

import br.com.fiap.fintech.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<Cliente> findAllByOrderByNomeAsc();
}
