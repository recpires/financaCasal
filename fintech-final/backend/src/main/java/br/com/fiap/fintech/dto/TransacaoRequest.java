package br.com.fiap.fintech.dto;

import br.com.fiap.fintech.model.TipoTransacao;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Dados enviados para registrar ou alterar uma transacao. */
public record TransacaoRequest(
        @NotNull(message = "Informe a conta") Long contaId,
        @NotNull(message = "Informe o tipo da transacao") TipoTransacao tipo,
        @NotNull(message = "Informe o valor")
        @Positive(message = "O valor deve ser maior que zero")
        @Digits(integer = 13, fraction = 2, message = "Valor invalido") BigDecimal valor,
        @NotBlank(message = "Informe a descricao")
        @Size(max = 150, message = "A descricao deve ter no maximo 150 caracteres") String descricao) {
}
