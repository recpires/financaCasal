package br.com.fiap.fintech.dto;

import br.com.fiap.fintech.model.TipoConta;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Dados enviados para abrir ou alterar uma conta. */
public record ContaRequest(
        @NotNull(message = "Informe o cliente") Long clienteId,
        @NotNull(message = "Informe o tipo da conta") TipoConta tipo,
        @Pattern(regexp = "\\d{4}", message = "A agencia deve ter 4 digitos") String agencia,
        @PositiveOrZero(message = "O limite nao pode ser negativo") BigDecimal limiteChequeEspecial,
        @DecimalMin(value = "0.0", message = "A taxa nao pode ser negativa")
        @DecimalMax(value = "0.05", message = "A taxa deve ser de no maximo 5% ao mes") BigDecimal taxaRendimentoMensal,
        Boolean ativa) {
}
