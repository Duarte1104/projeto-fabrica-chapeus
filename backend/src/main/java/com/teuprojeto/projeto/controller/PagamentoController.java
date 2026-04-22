package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Pagamento;
import com.teuprojeto.projeto.service.PagamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public List<Pagamento> listarTodos() {
        return pagamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Pagamento> procurarPorId(@PathVariable Long id) {
        return pagamentoService.procurarPorId(id);
    }

    @PostMapping
    public Pagamento guardar(@RequestBody Pagamento pagamento) {
        return pagamentoService.guardar(pagamento);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        pagamentoService.apagar(id);
    }
}