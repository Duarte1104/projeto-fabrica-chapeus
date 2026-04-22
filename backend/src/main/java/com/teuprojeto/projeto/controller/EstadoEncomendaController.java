package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.EstadoEncomenda;
import com.teuprojeto.projeto.service.EstadoEncomendaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/estados-encomenda")
public class EstadoEncomendaController {

    private final EstadoEncomendaService estadoEncomendaService;

    public EstadoEncomendaController(EstadoEncomendaService estadoEncomendaService) {
        this.estadoEncomendaService = estadoEncomendaService;
    }

    @GetMapping
    public List<EstadoEncomenda> listarTodos() {
        return estadoEncomendaService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<EstadoEncomenda> procurarPorId(@PathVariable Long id) {
        return estadoEncomendaService.procurarPorId(id);
    }

    @PostMapping
    public EstadoEncomenda guardar(@RequestBody EstadoEncomenda estadoEncomenda) {
        return estadoEncomendaService.guardar(estadoEncomenda);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        estadoEncomendaService.apagar(id);
    }
}
