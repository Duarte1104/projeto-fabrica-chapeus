package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.LinhaEncomenda;
import com.teuprojeto.projeto.service.LinhaEncomendaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/linhas-encomenda")
public class LinhaEncomendaController {

    private final LinhaEncomendaService linhaEncomendaService;

    public LinhaEncomendaController(LinhaEncomendaService linhaEncomendaService) {
        this.linhaEncomendaService = linhaEncomendaService;
    }

    @GetMapping
    public List<LinhaEncomenda> listarTodos() {
        return linhaEncomendaService.listarTodos();
    }

    @PostMapping
    public LinhaEncomenda guardar(@RequestBody LinhaEncomenda linhaEncomenda) {
        return linhaEncomendaService.guardar(linhaEncomenda);
    }
}