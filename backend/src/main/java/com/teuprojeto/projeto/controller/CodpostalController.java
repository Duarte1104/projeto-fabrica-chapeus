package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Codpostal;
import com.teuprojeto.projeto.service.CodpostalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/codpostais")
public class CodpostalController {

    private final CodpostalService codpostalService;

    public CodpostalController(CodpostalService codpostalService) {
        this.codpostalService = codpostalService;
    }

    @GetMapping
    public List<Codpostal> listarTodos() {
        return codpostalService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Codpostal> procurarPorId(@PathVariable String id) {
        return codpostalService.procurarPorId(id);
    }

    @PostMapping
    public Codpostal guardar(@RequestBody Codpostal codpostal) {
        return codpostalService.guardar(codpostal);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable String id) {
        codpostalService.apagar(id);
    }
}