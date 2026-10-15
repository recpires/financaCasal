package br.com.fiap.fintech.exception;

/** Login invalido -> HTTP 401. */
public class AutenticacaoException extends RuntimeException {

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
