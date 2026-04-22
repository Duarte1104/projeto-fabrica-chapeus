package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.rececionista.CriarFaturaRequest;
import com.teuprojeto.projeto.entity.Fatura;
import com.teuprojeto.projeto.service.FaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/faturas")
public class FaturaController {

    private final FaturaService faturaService;

    public FaturaController(FaturaService faturaService) {
        this.faturaService = faturaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fatura criar(@RequestBody CriarFaturaRequest request) {
        return faturaService.criar(request);
    }

    @GetMapping
    public List<Fatura> listarTodas() {
        return faturaService.listarTodas();
    }

    @GetMapping("/encomenda/{idEncomenda}")
    public List<Fatura> listarPorEncomenda(@PathVariable BigDecimal idEncomenda) {
        return faturaService.listarPorEncomenda(idEncomenda);
    }
}