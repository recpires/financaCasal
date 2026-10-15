package br.com.fiap.fintech.dto;

import br.com.fiap.fintech.model.Usuario;

/** Dados do usuario devolvidos no login (nunca inclui a senha). */
public record UsuarioResponse(Long id, String nome, String email) {

    public UsuarioResponse(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
