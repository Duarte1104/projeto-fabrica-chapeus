package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.OrdemProducao;
import com.teuprojeto.projeto.service.OrdemProducaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ordens-producao")
public class OrdemProducaoController {

    private final OrdemProducaoService ordemProducaoService;

    public OrdemProducaoController(OrdemProducaoService ordemProducaoService) {
        this.ordemProducaoService = ordemProducaoService;
    }

    @GetMapping
    public List<OrdemProducao> listarTodos() {
        return ordemProducaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<OrdemProducao> procurarPorId(@PathVariable Long id) {
        return ordemProducaoService.procurarPorId(id);
    }

    @PostMapping
    public OrdemProducao guardar(@RequestBody OrdemProducao ordemProducao) {
        return ordemProducaoService.guardar(ordemProducao);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        ordemProducaoService.apagar(id);
    }
}