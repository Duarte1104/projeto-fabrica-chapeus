package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.design.CriarDesignEncomendaRequest;
import com.teuprojeto.projeto.entity.DesignEncomenda;
import com.teuprojeto.projeto.service.DesignEncomendaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/designs")
public class DesignEncomendaController {

    private final DesignEncomendaService designEncomendaService;

    public DesignEncomendaController(DesignEncomendaService designEncomendaService) {
        this.designEncomendaService = designEncomendaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DesignEncomenda criar(@RequestBody CriarDesignEncomendaRequest request) {
        return designEncomendaService.criar(request);
    }

    @GetMapping
    public List<DesignEncomenda> listarTodos() {
        return designEncomendaService.listarTodos();
    }

    @GetMapping("/{id}")
    public DesignEncomenda procurarPorId(@PathVariable Long id) {
        return designEncomendaService.procurarPorId(id);
    }

    @GetMapping("/encomenda/{idEncomenda}")
    public List<DesignEncomenda> listarPorEncomenda(@PathVariable BigDecimal idEncomenda) {
        return designEncomendaService.listarPorEncomenda(idEncomenda);
    }

    @GetMapping("/estado/{estado}")
    public List<DesignEncomenda> listarPorEstado(@PathVariable String estado) {
        return designEncomendaService.listarPorEstado(estado);
    }

    @PatchMapping("/{id}/estado/{novoEstado}")
    public DesignEncomenda mudarEstado(@PathVariable Long id, @PathVariable String novoEstado) {
        return designEncomendaService.mudarEstado(id, novoEstado);
    }

    @GetMapping("/pendentes")
    public List<DesignEncomenda> listarPendentes() {
        return designEncomendaService.listarPorEstado("PENDENTE");
    }
}