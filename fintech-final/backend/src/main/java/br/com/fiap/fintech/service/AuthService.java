package br.com.fiap.fintech.service;

import br.com.fiap.fintech.dto.LoginRequest;
import br.com.fiap.fintech.dto.UsuarioResponse;
import br.com.fiap.fintech.exception.AutenticacaoException;
import br.com.fiap.fintech.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Confere e-mail e senha. A mensagem e a mesma nos dois casos para nao revelar qual dado esta errado. */
    public UsuarioResponse login(LoginRequest request) {
        return usuarioRepository.findByEmailIgnoreCase(request.email().trim())
                .filter(usuario -> usuario.getSenhaHash().equals(SenhaUtil.hash(request.senha())))
                .map(UsuarioResponse::new)
                .orElseThrow(() -> new AutenticacaoException("E-mail ou senha invalidos"));
    }
}
