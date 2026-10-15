package br.com.fiap.fintech.repository;

import br.com.fiap.fintech.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByOrderByDataHoraDescIdDesc();

    List<Transacao> findByContaIdOrderByDataHoraDescIdDesc(Long contaId);

    void deleteByContaId(Long contaId);
}
