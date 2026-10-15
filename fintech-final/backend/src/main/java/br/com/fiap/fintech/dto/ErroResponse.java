package br.com.fiap.fintech.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Corpo padrao das respostas de erro da API. */
public record ErroResponse(LocalDateTime dataHora, int status, String erro, String mensagem,
                           Map<String, String> campos) {
}
