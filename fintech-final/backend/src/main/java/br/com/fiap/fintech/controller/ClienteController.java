package br.com.fiap.fintech.controller;

import br.com.fiap.fintech.model.Cliente;
import br.com.fiap.fintech.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /** GET /api/clientes -> 200 */
    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    /** GET /api/clientes/{id} -> 200 ou 404 */
    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Long id) {
        return clienteService.buscar(id);
    }

    /** POST /api/clientes -> 201 com o header Location */
    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        Cliente criado = clienteService.criar(cliente);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    /** PUT /api/clientes/{id} -> 200 ou 404 */
    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    /** DELETE /api/clientes/{id} -> 204, 404 ou 409 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
