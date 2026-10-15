package br.com.fiap.fintech.controller;

import br.com.fiap.fintech.dto.ContaRequest;
import br.com.fiap.fintech.model.Conta;
import br.com.fiap.fintech.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    /** GET /api/contas (opcional: ?clienteId=1) -> 200 */
    @GetMapping
    public List<Conta> listar(@RequestParam(required = false) Long clienteId) {
        return contaService.listar(clienteId);
    }

    /** GET /api/contas/{id} -> 200 ou 404 */
    @GetMapping("/{id}")
    public Conta buscar(@PathVariable Long id) {
        return contaService.buscar(id);
    }

    /** POST /api/contas -> 201 com o header Location */
    @PostMapping
    public ResponseEntity<Conta> criar(@Valid @RequestBody ContaRequest request) {
        Conta criada = contaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    /** PUT /api/contas/{id} -> 200, 404 ou 422 */
    @PutMapping("/{id}")
    public Conta atualizar(@PathVariable Long id, @Valid @RequestBody ContaRequest request) {
        return contaService.atualizar(id, request);
    }

    /** DELETE /api/contas/{id} -> 204, 404 ou 409 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        contaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
