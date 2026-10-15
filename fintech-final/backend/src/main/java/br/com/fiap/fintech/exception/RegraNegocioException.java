package br.com.fiap.fintech.exception;

/** Operacao viola uma regra de negocio (ex.: saldo insuficiente) -> HTTP 422. */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
