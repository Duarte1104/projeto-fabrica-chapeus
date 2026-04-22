package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Etapa;
import com.teuprojeto.projeto.service.EtapaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/etapas")
public class EtapaController {

    private final EtapaService etapaService;

    public EtapaController(EtapaService etapaService) {
        this.etapaService = etapaService;
    }

    @GetMapping
    public List<Etapa> listarTodos() {
        return etapaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Etapa> procurarPorId(@PathVariable Long id) {
        return etapaService.procurarPorId(id);
    }

    @PostMapping
    public Etapa guardar(@RequestBody Etapa etapa) {
        return etapaService.guardar(etapa);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        etapaService.apagar(id);
    }
}