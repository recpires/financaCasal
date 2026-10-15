package br.com.fiap.fintech.controller;

import br.com.fiap.fintech.dto.TransacaoRequest;
import br.com.fiap.fintech.model.Transacao;
import br.com.fiap.fintech.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /** GET /api/transacoes (opcional: ?contaId=1) -> 200 */
    @GetMapping
    public List<Transacao> listar(@RequestParam(required = false) Long contaId) {
        return transacaoService.listar(contaId);
    }

    /** GET /api/transacoes/{id} -> 200 ou 404 */
    @GetMapping("/{id}")
    public Transacao buscar(@PathVariable Long id) {
        return transacaoService.buscar(id);
    }

    /** POST /api/transacoes -> 201, ou 422 se nao houver saldo */
    @PostMapping
    public ResponseEntity<Transacao> criar(@Valid @RequestBody TransacaoRequest request) {
        Transacao criada = transacaoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.getId()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    /** PUT /api/transacoes/{id} -> 200, 404 ou 422 */
    @PutMapping("/{id}")
    public Transacao atualizar(@PathVariable Long id, @Valid @RequestBody TransacaoRequest request) {
        return transacaoService.atualizar(id, request);
    }

    /** DELETE /api/transacoes/{id} -> 204 (estorna o valor no saldo da conta) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        transacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
