package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.producao.AtualizarProducaoEncomendaRequest;
import com.teuprojeto.projeto.entity.ProducaoEncomenda;
import com.teuprojeto.projeto.service.ProducaoEncomendaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/producao")
public class ProducaoEncomendaController {

    private final ProducaoEncomendaService producaoEncomendaService;

    public ProducaoEncomendaController(ProducaoEncomendaService producaoEncomendaService) {
        this.producaoEncomendaService = producaoEncomendaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProducaoEncomenda atualizar(@RequestBody AtualizarProducaoEncomendaRequest request) {
        return producaoEncomendaService.atualizar(request);
    }

    @GetMapping("/encomenda/{idEncomenda}")
    public ProducaoEncomenda procurarPorEncomenda(@PathVariable BigDecimal idEncomenda) {
        return producaoEncomendaService.procurarPorEncomenda(idEncomenda);
    }
}