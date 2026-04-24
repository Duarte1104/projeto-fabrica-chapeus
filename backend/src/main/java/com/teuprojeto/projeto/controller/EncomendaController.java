package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.dto.encomenda.CriarEncomendaRequest;
import com.teuprojeto.projeto.entity.Encomenda;
import com.teuprojeto.projeto.service.EncomendaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;

    public EncomendaController(EncomendaService encomendaService) {
        this.encomendaService = encomendaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Encomenda criar(@RequestBody CriarEncomendaRequest request) {
        return encomendaService.criar(request);
    }

    @GetMapping
    public List<Encomenda> listarTodas() {
        return encomendaService.listarTodas();
    }

    @GetMapping("/cliente/{idCliente}")
    public List<Encomenda> listarPorCliente(@PathVariable Integer idCliente) {
        return encomendaService.listarPorCliente(idCliente);
    }

    @GetMapping("/disponiveis")
    public List<Encomenda> listarDisponiveisParaFuncionario() {
        return encomendaService.listarDisponiveisParaFuncionario();
    }

    @GetMapping("/funcionario/{idFuncionario}")
    public List<Encomenda> listarPorFuncionario(@PathVariable Long idFuncionario) {
        return encomendaService.listarPorFuncionario(idFuncionario);
    }

    @PatchMapping("/{id}/aceitar/{idFuncionario}")
    public Encomenda aceitarEncomenda(
            @PathVariable BigDecimal id,
            @PathVariable Long idFuncionario
    ) {
        return encomendaService.aceitarEncomenda(id, idFuncionario);
    }

    @GetMapping("/{id}")
    public Encomenda procurarPorId(@PathVariable BigDecimal id) {
        return encomendaService.procurarPorId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable BigDecimal id) {
        encomendaService.apagar(id);
    }

    @PatchMapping("/{id}/estado/{idNovoEstado}")
    public Encomenda mudarEstado(
            @PathVariable BigDecimal id,
            @PathVariable Long idNovoEstado
    ) {
        return encomendaService.mudarEstado(id, idNovoEstado);
    }

    @GetMapping("/com-design")
    public List<Encomenda> listarComDesign() {
        return encomendaService.listarComDesign();
    }
}