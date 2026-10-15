package br.com.fiap.fintech.exception;

/** Registro procurado nao existe -> HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
