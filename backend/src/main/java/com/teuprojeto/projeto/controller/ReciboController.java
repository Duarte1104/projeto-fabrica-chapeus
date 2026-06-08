package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.rececionista.CriarReciboRequest;
import com.teuprojeto.projeto.entity.Recibo;
import com.teuprojeto.projeto.service.ReciboService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recibos")
public class ReciboController {

    private final ReciboService reciboService;

    public ReciboController(ReciboService reciboService) {
        this.reciboService = reciboService;
    }

    @GetMapping
    public List<Recibo> listarTodos() {
        return reciboService.listarTodos();
    }

    @GetMapping("/{id}")
    public Recibo procurarPorId(@PathVariable Long id) {
        return reciboService.procurarPorId(id);
    }

    @GetMapping("/pagamento/{idPagamento}")
    public Recibo procurarPorPagamento(@PathVariable Long idPagamento) {
        return reciboService.procurarPorPagamento(idPagamento);
    }

    @GetMapping("/fatura/{numFatura}")
    public List<Recibo> listarPorFatura(@PathVariable Long numFatura) {
        return reciboService.listarPorFatura(numFatura);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recibo criar(@RequestBody CriarReciboRequest request) {
        return reciboService.criar(request);
    }
}