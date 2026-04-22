package com.teuprojeto.projeto.controller;

import com.teuprojeto.projeto.entity.Chapeu;
import com.teuprojeto.projeto.service.ChapeuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chapeus")
public class ChapeuController {

    private final ChapeuService chapeuService;

    public ChapeuController(ChapeuService chapeuService) {
        this.chapeuService = chapeuService;
    }

    @GetMapping
    public List<Chapeu> listarTodos() {
        return chapeuService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Chapeu> procurarPorId(@PathVariable Long id) {
        return chapeuService.procurarPorId(id);
    }

    @PostMapping
    public Chapeu guardar(@RequestBody Chapeu chapeu) {
        return chapeuService.guardar(chapeu);
    }

    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        chapeuService.apagar(id);
    }
}