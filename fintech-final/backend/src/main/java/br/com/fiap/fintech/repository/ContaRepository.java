package br.com.fiap.fintech.repository;

import br.com.fiap.fintech.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findAllByOrderByIdAsc();

    List<Conta> findByClienteIdOrderByIdAsc(Long clienteId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByNumero(String numero);
}
