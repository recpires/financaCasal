package br.com.fiap.fintech.exception;

/** Conflito com o estado atual dos dados (ex.: CPF duplicado) -> HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
