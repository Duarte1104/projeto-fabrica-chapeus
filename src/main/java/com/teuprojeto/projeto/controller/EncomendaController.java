package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.service.EncomendaService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;

    public EncomendaController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @GetMapping
    public List<Encomenda> listarTodos() {
        return encomendaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Encomenda> procurarPorId(@PathVariable BigDecimal id) {
        return encomendaService.procurarPorId(id);
    }

    @PostMapping
    public Encomenda guardar(@RequestBody Encomenda encomenda) {
        return encomendaService.guardar(encomenda);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable BigDecimal id) {
        encomendaService.apagar(id);
    }
}