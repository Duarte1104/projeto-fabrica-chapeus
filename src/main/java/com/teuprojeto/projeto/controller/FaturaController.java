package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Fatura;
import com.teuprojeto.projeto.service.FaturaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/faturas")
public class FaturaController {

    private final FaturaService faturaService;

    public FaturaController(FaturaService faturaService) {
        this.faturaService = faturaService;
    }

    @GetMapping
    public List<Fatura> listarTodos() {
        return faturaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Fatura> procurarPorId(@PathVariable Long id) {
        return faturaService.procurarPorId(id);
    }

    @PostMapping
    public Fatura guardar(@RequestBody Fatura fatura) {
        return faturaService.guardar(fatura);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        faturaService.apagar(id);
    }
}