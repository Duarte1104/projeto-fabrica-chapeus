package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.rececionista.CriarPagamentoRequest;
import com.teuprojeto.projeto.entity.Pagamento;
import com.teuprojeto.projeto.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public Pagamento procurarPorId(@PathVariable Long id) {
        return pagamentoService.procurarPorId(id);
    }

    @GetMapping("/fatura/{idFatura}")
    public List<Pagamento> listarPorFatura(@PathVariable Long idFatura) {
        return pagamentoService.listarPorFatura(idFatura);
    }

    @GetMapping("/encomenda/{idEncomenda}")
    public List<Pagamento> listarPorEncomenda(@PathVariable BigDecimal idEncomenda) {
        return pagamentoService.listarPorEncomenda(idEncomenda);
    }

    @GetMapping("/fatura/{idFatura}/total-pago")
    public BigDecimal calcularTotalPagoPorFatura(@PathVariable Long idFatura) {
        return pagamentoService.calcularTotalPagoPorFatura(idFatura);
    }

    @GetMapping("/fatura/{idFatura}/valor-em-divida")
    public BigDecimal calcularValorEmDivida(@PathVariable Long idFatura) {
        return pagamentoService.calcularValorEmDivida(idFatura);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pagamento criar(@RequestBody CriarPagamentoRequest request) {
        return pagamentoService.criar(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable Long id) {
        pagamentoService.apagar(id);
    }
}