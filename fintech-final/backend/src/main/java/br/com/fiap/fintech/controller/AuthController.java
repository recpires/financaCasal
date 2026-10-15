package br.com.fiap.fintech.controller;

import br.com.fiap.fintech.dto.LoginRequest;
import br.com.fiap.fintech.dto.UsuarioResponse;
import br.com.fiap.fintech.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/login -> 200 com os dados do usuario, ou 401 */
    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
